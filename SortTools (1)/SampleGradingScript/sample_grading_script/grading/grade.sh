#!/bin/bash

TA_MODE=false

PREPARING=false
CLEANING=false

main_dir=$(pwd)
submissions_dir="$main_dir/submissions"
grading_project="$main_dir/grading_project"
detailed_feedback_dir="$main_dir/detailed_feedback"

collect_results()
{
	if [ ! -f "$main_dir/brief_results.csv" ]
	then
		echo '"submission_name", "grade", "comment"' > "$main_dir/brief_results.csv"
	fi

	mkdir -p "$detailed_feedback_dir"
	cd "$submissions_dir"
	for tested_submission in */
	do
        weights_file="$main_dir/helper_files/all_tests_weights.txt"
        if [[ -e "$weights_file" ]]; then
            weights_file="$main_dir/helper_files/all_tests_weights.txt"
        else
            weights_file="$main_dir/helper_files/sample_tests_weights.txt"
        fi

		tested_submission=${tested_submission:0:${#tested_submission}-1}
		brief_result_line=$( python2 "$main_dir/helper_files/score_extractor.py" "$tested_submission/maven_run_report.xml" "$tested_submission" "$weights_file")
		echo "$brief_result_line" >> "$main_dir/brief_results.csv"
		zip -ry9Tm "$tested_submission" "$tested_submission" > /dev/null
		mv "$tested_submission".zip "$detailed_feedback_dir"

	done
	cd "$main_dir"
}

perform_test_on_this_submission()
{
	this_dir=$(pwd)

	rm -rf "$grading_project/src/main/java/assignment1/*"
    for java_file in $(find . -name "*.java")
    do
        if ! ( echo "$java_file" | grep -iq "test" )
        then
            mv "$java_file" "$grading_project/src/main/java/assignment1/"
        fi
    done

	cd "$grading_project"
    rm -rf target

    if [ "$TA_MODE" = true ]; then
	    mvn test > /dev/null 2>&1 || echo " "
    else
	    mvn test || echo " "
    fi


	cd "$this_dir"

	rm -rf *
	if ( ls "$grading_project/target/surefire-reports/"*".xml" > /dev/null 2>&1 )
    then
        for xml in "$grading_project/target/surefire-reports/"*".xml"
        do
            mv "$xml" ./maven_run_report.xml
        done
    fi
}

run_test_on_submissions()
{
	cd "$submissions_dir"

    if ( ls *.java 1> /dev/null 2>&1 )
    then
	    for submission_raw_name in *.java
	    do
	    	submission_name="${submission_raw_name%%.*}"
	    	echo "### going on: " "$submission_name"

	    	mkdir "$submission_name"
            mv "$submission_raw_name" "$submission_name/SortTools.java"
            cd "$submission_name"

	    	perform_test_on_this_submission

	    	cd "$submissions_dir"
	    done
    fi

	cd "$main_dir"
}

prepare_maven_idempotent()
{
    if [ -f ~/.profile ]
    then
        . ~/.profile
    fi

    if ! type "mvn" > /dev/null 2>&1 # if maven is not loaded
    then
        module load maven
    fi
}

grade()
{
	set -e

	rm -rf  "$detailed_feedback_dir" "$main_dir/brief_results.csv"

    prepare_maven_idempotent
	run_test_on_submissions
	echo "collecting results ..."
	collect_results
	echo "finished"

	set +e
}

clean()
{
    rm -rf detailed_feedback brief_results.csv "$grading_project"
    rm -rf submissions
    git checkout -- submissions ./helper_files/all_tests_weights.txt
}

prepare()
{
    rm -rf "$grading_project"
    cp -R ../solution "$grading_project"
    rm -rf "$grading_project/src/main/java/assignment1/"*

    if [[ $TA_MODE == true ]]; then
        rm -f "$grading_project/src/test/java/assignment1/SampleTest.java"
    else
        rm -f "$grading_project/src/test/java/assignment1/AllTest.java"
        rm -f ./helper_files/all_tests_weights.txt
    fi
}

main()
{
    if [[ $CLEANING == true ]]; then
        clean
    elif [[ $PREPARING == true ]]; then
        prepare
    else
        grade
    fi
}

while [[ $# > 0 ]]
do
    arg="$1"
    case $arg in
        --ta)       TA_MODE=true;;
        --clean)    CLEANING=true;;
        --prepare)  PREPARING=true;;
    esac

    shift
done

main
