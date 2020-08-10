package io.github.turskyi.sqliteonupgradeexample

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * Kotlin realization of lesson from
 * @see [https://startandroid.ru/en/lessons/550-lesson-39-onupgrade-database-migrating.html]
 */
/* all data only in logs */
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    companion object {
        const val LOG_TAG = "myLogs"
        /* DB name */
        const val DB_NAME = "staff"
        /* DB version */
        const val DB_VERSION = 2
    }

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dbh = DBHelper(this)
        val db = dbh.writableDatabase
        Log.d(LOG_TAG, " --- Staff db v." + db.version + " --- ")
        writeStaff(db)
        dbh.close()
    }

    /* data request and output to the log for DB1 */
//    private fun writeStaff(db: SQLiteDatabase) {
//        val c = db.rawQuery("select * from people", null)
//        logCursor( c, "Table people")
//        c.close()
//    }

    /* data request and output to the log for DB2 */
    private fun writeStaff(db: SQLiteDatabase) {
        var cursor = db.rawQuery("select * from people", null)
        logCursor(cursor, "Table people")
        cursor.close()
        cursor = db.rawQuery("select * from position", null)
        logCursor(cursor, "Table position")
        cursor.close()
        val sqlQuery = ("select PL.name as Name, PS.name as Position, salary as Salary "
                + "from people as PL "
                + "inner join position as PS "
                + "on PL.posid = PS.id ")
        cursor = db.rawQuery(sqlQuery, null)
        logCursor(cursor, "inner join")
        cursor.close()
    }

    /* logging data from the cursor */
    private fun logCursor(cursor: Cursor?, @Suppress("SameParameterValue") tableName: String) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Log.d(LOG_TAG, tableName + ". " + cursor.count + " rows")
                val stringBuilder = StringBuilder()
                do {
                    stringBuilder.setLength(0)
                    for (columnName in cursor.columnNames) {
                        stringBuilder.append(columnName + " = "
                                + cursor.getString(cursor.getColumnIndex(columnName)) + "; ")
                    }
                    Log.d(LOG_TAG, stringBuilder.toString())
                } while (cursor.moveToNext())
            }
        } else Log.d(LOG_TAG, "${tableName}. Cursor is null")
    }
}
