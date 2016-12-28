package measurement.color.com.xj_919.and.Utils.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wpc on 2016/9/27.
 */

public class MyDBHelper extends SQLiteOpenHelper {

//    dbHelper = new MyDBHelper(context, app.SQLite_LOG, null, 1);
//    db = dbHelper.getWritableDatabase();

    //别忘了逗号
    public static final String CREATE_RGB =
            "create table rgb("
            + "id integer primary key autoincrement,"
            + "data text,"
            + "time text,"
            + "result1 text," + "detial1 text,"
            + "result2 text," + "detial2 text,"
            + "result3 text," + "detial3 text,"
            + "result4 text," + "detial4 text,"
            + "result5 text," + "detial5 text,"
            + "result6 text," + "detial6 text,"
            + "PNGpath text,"
            + "cachePath text,"
            + "tips text,"
            + "result text)";
    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RGB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
