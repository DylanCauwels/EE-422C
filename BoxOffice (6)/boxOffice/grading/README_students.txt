Instructions for students:

Follow the submission instructions (based on the assignment description), and put your submission file in the submissions directory.
(We provide 3 dummy submissions already. Add your own submission file to them.) You might notice that the submissions directory structure for the dummy submissions is slightly different from the one requested from you in the PDF description.  That should not be a problem.  But do ensure that you don't include test case files or non-necessary .java files.
Then execute ./perform_grading.sh

WARNING: after running the grading script, your original files will be removed. Make a copy before grading.
WARNING: do not change the structure of the provided methods at all, although your code might compile on your machine;
    it will cause a compilation error here as the tests do not expect your modified declarations.

You can find the results in the brief_results.csv and the detailed_feedback directory.

If everything goes well, for your submission in the brief_results.csv you should have 2 as your final grade.

In the detailed feedback directory, you will find one zip file for each submission.
If that zip file does not contain test_log.txt nor any xml file, it means you had submission structure violation.
If you have only one file named test_log.txt, it means you have had a compile error. You can find your error by inspecting test_log.txt.
If you have xml files too, it means your submission has been compiled and the tests were run.
You can see the results of tests by reading xml files; also the logs of whole process of grading is in test_log.txt.
The detailed_feedback folder for the 3 sample submissions is provided to you as a zip file for your comparison.

In order to understand the behavior of the grading script, there are 3 sample submissions there.

Project6_compileError.zip               : this submission causes compile error as it is not compatible with the test cases.
Project6_submissionStructureError.zip   : this submission does not have the appropriate format
Project6_someTestsFailure.zip           : this submission has appropriate format and compiles. It passes some (not all) of the tests.

After performing grading the brief results for the sample submissions should be exactly like this:

"submission_name", "grade", "comment"
"Project6_compileError",  0 , "compile_error"
"Project6_someTestsFailure",  1 , ""
"Project6_submissionStructureError",  0 , "compile_error"

Note: Only include files that we ask you to submit.

##################################################
DO NOT USE System.exit in your codes, that will
halt all testing procedure.
##################################################

If you have trouble running this script on kamek, it  may be because you have modified .bash_profile and/or other .xxx files in your home directory.  Try replacing your modified files with the standard ones from a friend's directory.
