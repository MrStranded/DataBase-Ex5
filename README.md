# DataBase-Ex5
Our solution for the second part of the exercise 5 of the databases course.

___________________________________________________________________________________
## How-To-Use:

Run the DB_Ex05_part2_artifact.jar file as follows:
`java -jar DB_Ex05_part2_artifact.jar`

You may use the following arguments:
`help` to get a description of how-to-use the program
`hamming` to run the Hamming comparison
`soundex` to run the Soundex comparison
`levenshtein` to run the Levenshtein comparison
`jaccard` to run the Jaccard comparison
`optimal` to run our own combination of comparators, which achieves a higher true positive rate
Or no arguments at all, to run all the similarity measures.

You may also combine arguments, to run a select subset of comparators in the order you want.
Example:
`java -jar DB_Ex05_part2_artifact.jar levenshtein optimal help soundex`

___________________________________________________________________________________
## (Cleaned) Console Output:

--------- Applying HammingDistance
Removed 3413 duplicates. 23718 remain.
It took 8466 milliseconds.
True Positive Rate = 0.13760340732246704

--- Sample names:
ERIC KEITH
STUART SEVERANCE
TERI LE

--------- Applying SoundexDistance
Removed 1100 duplicates. 26031 remain.
It took 8882 milliseconds.
True Positive Rate = 0.05450896879351298

--- Sample names:
KRISTOPHER KEITH
STUART SPRINGER
TRUDY LEATHERMAN

--------- Applying LevenshteinDistance
Removed 1357 duplicates. 25774 remain.
It took 178632 milliseconds.
True Positive Rate = 0.7411335899746089

--- Sample names:
KRISTIN KEITH
STUART SEVERANCE
TERI LEATHERWOOD

--------- Applying JaccardDistance
Removed 1229 duplicates. 25902 remain.
It took 233804 milliseconds.
True Positive Rate = 0.7186501760995987

--- Sample names:
KRISTIN KEITH
STUART SEVERANCE
TERI LEATHERWOOD

--------- Applying OptimalDistance
Removed 1408 duplicates. 25723 remain.
It took 181883 milliseconds.
True Positive Rate = 0.7674666229830452

--- Sample names:
KRISTIN KEITH
STUART SEVERANCE
TERI LEATHERWOOD

