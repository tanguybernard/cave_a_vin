package istic.fr.cavevin;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tbernard on 20/03/16.
 */
public class WinesDb {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_REGION = "region";
    public static final String KEY_YEAR = "year";
    public static final String KEY_MATURATION = "maturation";
    public static final String KEY_KIND = "kind";
    public static final String KEY_QUANTITY = "quantity";
    public static final String KEY_DETAILS = "details";

    private static final String LOG_TAG = "WinessDb";
    public static final String SQLITE_TABLE = "Wine";

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_NAME + "," +
                    KEY_QUANTITY + "," +
                    KEY_REGION + "," +
                    KEY_YEAR + "," +
                    KEY_DETAILS + "," +
                    KEY_MATURATION + "," +
                    KEY_KIND  +
                    ");";

    public static void onCreate(SQLiteDatabase db) {
        Log.w(LOG_TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
        onCreate(db);
    }

}
