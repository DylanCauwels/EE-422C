
Assignment 1 grading script sample
__________________________________

This is a sample script (grade.sh) for assignment 1.  You have to run this script successfully with your program and our testing files.  The script runs two JUNIT testcases on your code using the same script that we will use to grade  your code.

In order to run these scripts you need certain prerequisites:
* A unix-like environment (e.g. macOS, GNU/Linux, BSD)
* Internet connection (for downloading some dependencies)
* Python 2.6    ( python2.6 )
* Maven         ( mvn )
* Java 8        ( some JDK 1.8 e.g. openjdk, Oracle's JDK )
* zip           ( there's a good chance you already have this )

All these are available on the ECE Linux 64-bit servers, such as kamek.ece.utexas.edu.  In fact, we ask that you run this script on kamek, after copying your files over there. Copy the zip file over to kamek (using scp, YummyFTP, WinSCP etc.) to a fresh directory, and unzip the files.  Set the permission of your directory so that only you can read it, by executing:
chmod 700 <your_directory_name>

Once you have finished your coding, you can place "your_code.java" in the "submissions" directory. A Java file has been placed there as an example.

Once everything is ready, you can run the "grade.sh" script on your command line. The shells Bash and zsh have been tested. If you have any questions regarding running a shell script, Google it! It's very easy! For example, ./grade.sh usually works. The JUNIT tests are under grading_project.

If you take a look at the helper scripts, you'll find that the testcases might have different weights (the weights provided are just given as an example).

When the script has run successfully, you can find the brief results in the "brief_results.csv" file. You can open it with Microsoft Excel, Numbers, LibreOffice Calc, or any text editor. If everything goes well, with the provided sample test cases, your grade should be 5 in the "brief_results.csv". The provided sample_submission.java file, get grade 2 out of 5 (for passing one sample test and failing one other).

When you run the script, the SortTools.java file will be removed from the submissions folder, and you will have to put it back in there before each run. (In fact, it will be moved to grading_project/src/main/java/assignment1.)

You can also find your detailed feedback of each tests run in the "detailed_feedback" directory. If your zip file is empty, it means that you had a compile error / fatal error in your code (one of the most frequent one is caused by using System.exit in your code).  We will explain later how to look at the XML file.
