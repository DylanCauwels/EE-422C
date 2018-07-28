the script grade.sh does 3 operations in 2 modes:

modes:
TA_MODE: true/false
    The default mode is TA_MODE=false. In order to set it to true,
    ./grade.sh needs to be run with --ta argument (./grade.sh --ta).
    In order to see its effect, read the operations below.

1. Grading:
    This is the default operation (does not need any operation flag).
    It will look at the submissions directory and grades the submission.
    
    Grading is resumable if any error occures (after manual fix of submission error)
    but it removes the graded submissions from submissions directory while it is grading.

    It has two modes of operation: TA_MODE=true/flase, when it is in TA_MODE
    it will not provide so much details when grading is being done.

    Also, when it is on TA_MODE, it will search for the files which are prepared by prepare operation
    in TA_MODE (see the code for more information)


2. Preparing:
    In order to perform this operation you need to call grade.sh with --prepare flag. (./grade.sh --prepare)
    It will set up the grading project template and removes unnecessary tests.
    If TA_MODE is true, it will remove sample test cases and if it is false, it will remove actual test cases.

    Therefore, this operation is done when you want to prepare for grading or
    when you want to publish the grading script with sample tests to the students.

3. Cleaning:
    In order to perform this operation you need to call grade.sh with --clean flag. (./grade.sh --clean)
    It will remove all the submissions and grading results and grading project
    and restores the sample submissions.
    This is useful when you want to reset the process of grading/preparing.
    It doesn't have multiple modes of operation.

Examples:

I.      Typical grading for students: ./grade.sh
II.     Typical grading for TAs: ./grade.sh --prepare --ta && ./grade.sh --ta
III.    Preparing files for students to provide a sample grading script: ./grade.sh --clean && ./grade.sh --prepare
