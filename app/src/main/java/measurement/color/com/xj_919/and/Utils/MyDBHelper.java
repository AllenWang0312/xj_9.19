package measurement.color.com.xj_919.and.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by wpc on 2016/9/27.
 */

public class MyDBHelper extends SQLiteOpenHelper {


    public static final String CREATE_RGB_TABLE = "create table rgb ("
            + "id integer primary key autoincrement,"
            + "time text,"
            + "R1Size integer," + "G1Size integer," + "B1Size integer,"  + "R1Mean real," + "G1Mean real," + "B1Mean real,"  + "R1Wc real," + "G1Wc real," + "B1Wc real,"
            + "R2Size integer," + "G2Size integer," + "B2Size integer,"  + "R2Mean real," + "G2Mean real," + "B2Mean real,"  + "R2Wc real," + "G2Wc real," + "B2Wc real,"
            + "R3Size integer," + "G3Size integer," + "B3Size integer,"  + "R3Mean real," + "G3Mean real," + "B3Mean real,"  + "R3Wc real," + "G3Wc real," + "B3Wc real,"
            + "R4Size integer," + "G4Size integer," + "B4Size integer,"  + "R4Mean real," + "G4Mean real," + "B4Mean real,"  + "R4Wc real," + "G4Wc real," + "B4Wc real,"
            + "R5Size integer," + "G5Size integer," + "B5Size integer,"  + "R5Mean real," + "G5Mean real," + "B5Mean real,"  + "R5Wc real," + "G5Wc real," + "B5Wc real,"
            + "R6Size integer," + "G6Size integer," + "B6Size integer,"  + "R6Mean real," + "G6Mean real," + "B6Mean real,"  + "R6Wc real," + "G6Wc real," + "B6Wc real,"
            + "R7Size integer," + "G7Size integer," + "B7Size integer,"  + "R7Mean real," + "G7Mean real," + "B7Mean real,"  + "R7Wc real," + "G7Wc real," + "B7Wc real,"
            + "R8Size integer," + "G8Size integer," + "B8Size integer,"  + "R8Mean real," + "G8Mean real," + "B8Mean real,"  + "R8Wc real," + "G8Wc real," + "B8Wc real"
            + "R9Size integer," + "G9Size integer," + "B9Size integer,"  + "R9Mean real," + "G9Mean real," + "B9Mean real,"  + "R9Wc real," + "G9Wc real," + "B9Wc real";

    private Context context;

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RGB_TABLE);
        Log.i("MyDBhelper ", "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
