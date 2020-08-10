package io.github.turskyi.sqliteonupgradeexample

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import io.github.turskyi.sqliteonupgradeexample.MainActivity.Companion.DB_NAME
import io.github.turskyi.sqliteonupgradeexample.MainActivity.Companion.DB_VERSION
import io.github.turskyi.sqliteonupgradeexample.MainActivity.Companion.LOG_TAG

/* class for working with DB */
class DBHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        Log.d(LOG_TAG, " --- onCreate database --- ")
        val peopleName = arrayOf(
            "Иван", "Марья", "Петр", "Антон", "Даша",
            "Борис", "Костя", "Игорь"
        )
        val peoplePosId = intArrayOf(2, 3, 2, 2, 3, 1, 2, 4)

        /* data for the job table */
        val positionId = intArrayOf(1, 2, 3, 4)
        val positionName = arrayOf(
            "Директор", "Программер", "Бухгалтер",
            "Охранник"
        )
        val positionSalary = intArrayOf(15000, 13000, 10000, 8000)

        /* redundant data from DB1 */
//        val peoplePositions = arrayOf(
//            "Программер", "Бухгалтер",
//            "Программер", "Программер", "Бухгалтер", "Директор",
//            "Программер", "Охранник"
//        )

        val contentValues = ContentValues()

        /* create a table of people for DB1 */
//        db.execSQL(
//            "create table people ("
//                    + "id integer primary key autoincrement,"
//                    + "name text, position text);"
//        )

        /* create a job table */
        db.execSQL("create table position (" + "id integer primary key,"
                + "name text, salary integer" + ");")

        /* fill table of people from DB1 */
//        for (i in peopleName.indices) {
//            contentValues.clear()
//            contentValues.put("name", peopleName[i])
//            contentValues.put("position", peoplePositions[i])
//            db.insert("people", null, contentValues)
//        }

        /* fill job table from DB2 */
        for (i in positionId.indices) {
            contentValues.clear()
            contentValues.put("id", positionId[i])
            contentValues.put("name", positionName[i])
            contentValues.put("salary", positionSalary[i])
            db.insert("position", null, contentValues)
        }

        /* create a table of people for DB2 */
        db.execSQL("create table people ("
                + "id integer primary key autoincrement,"
                + "name text, posid integer);")

        /* fill table of people from DB2 */
        for (i in peopleName.indices) {
            contentValues.clear()
            contentValues.put("name", peopleName[i])
            contentValues.put("posid", peoplePosId[i])
            db.insert("people", null, contentValues)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(
            LOG_TAG, " --- onUpgrade database from " + oldVersion
                    + " to " + newVersion + " version --- "
        )
        if (oldVersion == 1 && newVersion == 2) {
            val contentValues = ContentValues()

            /* data for the job table */
            val positionId = intArrayOf(1, 2, 3, 4)
            val positionName = arrayOf(
                "Директор", "Программер",
                "Бухгалтер", "Охранник"
            )
            val positionSalary = intArrayOf(15000, 13000, 10000, 8000)
            db.beginTransaction()
            try {
                /* create a job table */
                db.execSQL(
                    "create table position ("
                            + "id integer primary key,"
                            + "name text, salary integer);"
                )

                /* fill it */
                for (i in positionId.indices) {
                    contentValues.clear()
                    contentValues.put("id", positionId[i])
                    contentValues.put("name", positionName[i])
                    contentValues.put("salary", positionSalary[i])
                    db.insert("position", null, contentValues)
                }
                db.execSQL("alter table people add column posid integer;")
                for (i in positionId.indices) {
                    contentValues.clear()
                    contentValues.put("posid", positionId[i])
                    db.update(
                        "people",
                        contentValues,
                        "position = ?",
                        arrayOf(positionName[i])
                    )
                }
                db.execSQL(
                    "create temporary table people_tmp ("
                            + "id integer, name text, position text, posid integer);"
                )
                db.execSQL("insert into people_tmp select id, name, position, posid from people;")
                db.execSQL("drop table people;")
                db.execSQL(
                    "create table people ("
                            + "id integer primary key autoincrement,"
                            + "name text, posid integer);"
                )
                db.execSQL("insert into people select id, name, posid from people_tmp;")
                db.execSQL("drop table people_tmp;")
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
}