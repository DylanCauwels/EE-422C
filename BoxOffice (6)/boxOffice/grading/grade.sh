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
grading_project_original="$main_dir/grading_project_original"
grading_project="$main_dir/grading_project"
detailed_feedback_dir="$main_dir/detailed_feedback"
brief_feedback_file="$main_dir/brief_results.csv"

collect_results()
{
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

		tested_submission="${tested_submission:0:${#tested_submission}-1}"
        if [ -f "$tested_submission/test_log.txt" ]
        then
		    brief_result_line=$( python2.6 "$main_dir/helper_files/score_extractor.py" "$tested_submission"/*.xml "$tested_submission" "$weights_file")
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
	java_files="$(find . -name '*.java')"

	rm -rf "$grading_project"
    cp -R "$grading_project_original" "$grading_project"
    for jfile in $java_files
    do
	    mv "$jfile" "$grading_project/src/main/java/assignment6/"
    done

	cd "$grading_project"
    if [ "$TA_MODE" = true ]
    then
	    mvn clean test > test_log.txt 2>&1 || true
    else
	    mvn clean test 2>&1 | tee test_log.txt || true
    fi

	cd "$this_submission_dir"

	rm -rf *

	mv "$grading_project/test_log.txt" .

    if ls "$grading_project/target/surefire-reports/"*.xml 1> /dev/null 2>&1; then
        mv "$grading_project/target/surefire-reports/"*.xml .
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
                echo "\"$submission_name\", 0, \"submission_format_error\"" >> "$brief_feedback_file"
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

    prepare_maven_idempotent
	run_test_on_submissions
	echo "collecting results ..."
	collect_results
	echo "finished"

    disable_throwing_errors
}

clean()
{
    rm -rf "$grading_project" "$grading_project_original" "$brief_feedback_file" "$detailed_feedback_dir"
    rm -rf submissions
    git checkout -- submissions ./helper_files/all_tests_weights.txt
}

prepare()
{
	if [ ! -f "$brief_feedback_file" ]
	then
		echo '"submission_name", "grade", "comment"' > "$brief_feedback_file"
	fi

    cp -R "../solutions/sol1" "$grading_project_original"
    cp -R "../tests/assignment6" "$grading_project_original/src/test/java"
	rm -f "$grading_project_original/src/main/java/assignment6/"*.java

    if [[ $TA_MODE == true ]]; then
        rm "$grading_project_original/src/test/java/assignment6/A6SampleTest.java"
    else
        rm "$grading_project_original/src/test/java/assignment6/A6AllTest.java"
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
