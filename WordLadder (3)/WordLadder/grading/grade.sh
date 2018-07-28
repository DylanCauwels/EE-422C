#!/bin/bash

TA_MODE=false

PREPARING=false
CLEANING=false

function try() { [[ $- = *e* ]]; SAVED_OPT_E=$?;set +e;}
function throw() { exit $1;}
function catch() { export ex_code=$?;(( $SAVED_OPT_E )) && set +e;return $ex_code;}
function enable_throwing_errors() { set -e;}
function disable_throwing_errors() { set +e;}

main_dir="$(pwd)"
submissions_dir="$main_dir/submissions"
grading_project="$main_dir/grading_project"
detailed_feedback_dir="$main_dir/detailed_feedback"
brief_feedback_file="$main_dir/brief_results.csv"

collect_results()
{
	mkdir -p "$detailed_feedback_dir"
	cd "$submissions_dir"
	for tested_submission in */
	do
		tested_submission="${tested_submission:0:${#tested_submission}-1}"
        if [ -f "$tested_submission/test_log.txt" ]
        then
		    brief_result_line=$( python2.6 "$main_dir/helper_files/score_extractor.py" "$tested_submission/test_log.txt" "$tested_submission" )
		    echo "$brief_result_line" >> "$brief_feedback_file"
        fi
        
		zip -ry9Tm "$tested_submission" "$tested_submission" > /dev/null
		mv "$tested_submission".zip "$detailed_feedback_dir"

	done
	cd "$main_dir"
}

perform_test_on_this_submission()
{
    enable_throwing_errors

	this_submission_dir="$(pwd)"

	rm -rf "$grading_project/src/main/java/assignment3/"*

    java_files=()
    while IFS=  read -r -d $'\0';
    do
        java_files+=("$REPLY")
    done < <(find . -name "*.java" -print0)

    for ((i = 0; i < ${#java_files[@]}; i++))
    do
        if [[ "${java_files[$i]}" != *Test* ]]; then
            mv "${java_files[$i]}" "$grading_project/src/main/java/assignment3"
        fi
    done

	cd "$grading_project"
    if [ "$TA_MODE" = true ]
    then
	    MAVEN_OPTS="-Xms1024m -Xmx1024m" mvn clean test > test_log.txt 2>&1 || true
    else
    	MAVEN_OPTS="-Xms1024m -Xmx1024m" mvn clean test 2>&1 | tee test_log.txt || true
    fi

	cd "$this_submission_dir"

	rm -rf *

	mv "$grading_project/test_log.txt" .

    if ls "$grading_project/target/surefire-reports/"*.xml 1> /dev/null 2>&1; then
        for xml in "$grading_project/target/surefire-reports/"*".xml"
        do
            mv "$xml" ./maven_run_report.xml
        done
    fi
}

run_test_on_submissions()
{
    enable_throwing_errors

	cd "$submissions_dir"

    if ( ls *.zip 1> /dev/null 2>&1 )
    then
	    for submission_raw_name in *.zip
	    do
	    	submission_name="${submission_raw_name%%.*}"
            try
            (
	    	    echo "### going on: " "$submission_name"

	    	    mkdir "$submission_name"
	    	    cd "$submission_name"
	    	    unzip ../"$submission_raw_name" > /dev/null
	    	    find . -name __MACOSX -exec rm -rfv {} \; > /dev/null 2>&1 || echo ""
	    	    rm ../"$submission_raw_name"

	    	    perform_test_on_this_submission

	    	    cd "$submissions_dir"
            )
            catch ||
            {
                echo "###### submission format error on $submission_name"
                echo "\"$submission_name\", 0, 0, 0, 0" >> "$brief_feedback_file"
                cd "$submissions_dir"
            }
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
    enable_throwing_errors

	if [ ! -f "$brief_feedback_file" ]
	then
		echo '"submission_name", "total", "fail", "error", "skipped"' > "$brief_feedback_file"
	fi

    prepare_maven_idempotent
	run_test_on_submissions
	echo "collecting results ..."
	collect_results
	echo "finished"

    disable_throwing_errors
}

clean()
{
    rm -rf detailed_feedback brief_results.csv "$grading_project"
    rm -rf submissions
    git checkout -- submissions
}

prepare()
{
    rm -rf "$grading_project"
    cp -R ../solution/canonical_solution "$grading_project"
    cp "../../file_for_students/five_letter_words.txt" "$grading_project"
    cp "../test_cases/"*.java "$grading_project/src/test/java/assignment3/"
    rm -rf "$grading_project/src/main/java/assignment3/"*

    if [[ $TA_MODE == true ]]; then
        rm -f "$grading_project/src/test/java/assignment3/SampleTest.java"
    else
        rm -f "$grading_project/src/test/java/assignment3/AllTest.java"
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
