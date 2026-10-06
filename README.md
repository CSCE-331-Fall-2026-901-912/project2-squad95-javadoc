# project2-squad95
This contains the code for Project 2 of Squad 95, containing Kenny Le, Ethan Faulkner, Christopher Jermia, Jeffrey Yu, and Andrew Schrick. With the exception for grading purposes, these codes shall not be used without permission.

This project uses Gradle as a build tool and JavaFX as an application framework. 

Current Status: Release 1

# Building/Running the GUI App

Navigate to the `frontend/` directory, then run `gradle build`. 
To run the app, do `gradle run`.

If `gradle` cannot be found on your system, replace `gradle` with
`./gradlew` for Linux/Mac and `./gradlew.bat` for Windows.

# Files Currently on The Repo

### items_ShareTea.txt

Contains the menu, toppings, ice, and sugar composition available for the POS system. This is also mentioned in csv_gen.py.

### queries.sql

Contains the first iteration of the SQL database. 14 of these are required, while 5 others are special queries. They include determining the most popular and least popular updates (for logistics matters), orders placed before and after 3 pm (to make decisions for dinner or lunch specials), the employee with the most sales (for pay rise purposes), the top and bottom 10 drinks, the most and least common drink size, monthly revenue, toppings, days with sales exceeding $5000, quantity exceeds 10, and large-sized drinks, all for financial and logistical purposes mostly used in manager view. The other 5 special queries are programmed according to the rubric.

### csv_gen.py

Contains the Python program to generate CSV based on the available menu. This sets up the headers, arrays for menu and price, formats dates, determines peak days, peak sales, and total sales amount for manager view, and prints all of those iteratively in a .csv file.

### about.html

Contains the About page included during the latest lab activity - more git. For the early release of the project, this file is not going to be used in any way, but this might be used in later releases.

### .gitignore

Contains configuration ignored by git during pull, commit, and push.

### NOTES (for TA)

1. Some commits are done under lonestarflightreviews. That is the secondary account for cjermia-zach and was accidentally used here for commit and push due to a technical issue on Christopher Jermia's machine. No security breach occurred in our repository while the code was being developed
