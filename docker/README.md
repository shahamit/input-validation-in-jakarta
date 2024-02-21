# Input Validation in Java Jakarta - Practical Exercise

## How to run this app locally

* Before starting, make sure you have OpenJDK 21 installed on your computer. Note that the application is written for Java 11 and Java Jakarta EE 10.
* To run the app locally, first create the local database running the following command:
  
  ```bash
  sqlite3 db.sqlite3 < init_db.sql
  ```

* Then, using Visual Studio Code, press `Ctrl` + `Shift` + `B` on Windows/Linux or `Cmd` + `Shift` + `B` on macOS. The application will compile and run automatically.
* Finally, navigate to [`http://localhost:8080/search`][1].
* In case you are using another IDE, make sure you set the environment variable `DB_URL` defining the path to the `db.sqlite3` file, i.e. `export DB_URL="jdbc:sqlite:/path/to/db.sqlite3"`.

[1]: http://localhost:8080/search
