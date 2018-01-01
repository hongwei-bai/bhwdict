package bhwWords.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WordDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DBNAME = "word_db";

    public interface TABLE_WORD {
        public String TABLE = "table_word";
        public String _ID = "_id";
        public String ENGLISH = "english";
        public String CHINESE = "chinese";
        public String PHONETIC = "phonetic";
        public String DATE_ADDED = "date_added";
        public String DATE_UPDATED = "date_updated";
        public String IMPORTANCE = "importance";

        // has mod function for these 3 columns
        public String EN_PASS = "count_pass";
        public String CN_PASS = "count_fail";
        public String REVIEW_FLAG = "count_sum";

        public String DIRECTION = "direction";
        public String DELETE = "delete";
        public String ARCHIVE = "archive";
        public String EXT1 = "ext1";
        public String EXT2 = "ext2";
        public String SOURCE = "ext3";
        public String EXT4 = "ext4";
    }

    public interface MIME_TYPE {
        public String PROPERTY = "property";
        public String EXAMPLE = "example";
        public String TOPIC = "topic";
        public String COHERENCE = "coherence";
    }

    public interface TABLE_MIME_TYPE {
        public String TABLE = "table_mime_type";
        public String MIME_ID = "mime_id";
        public String MIME_TYPE = "mime_type";
    }

    public interface TABLE_DATA {
        public String TABLE = "table_data";
        public String _ID = "_id";
        public String WORD_ID = "word_id";
        public String MIME_ID = "mime_id";
        public String MIME_TYPE = "mime_type";
        public String DATA1 = "data1";
        public String DATA2 = "data2";
        public String DATA3 = "data3";
        public String DATA4 = "data4";
        public String DATA5 = "data5";
        public String DATA6 = "data6";
    }

    public WordDbHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createWordTable(db);
        createDataTable(db);
    }

    private void createWordTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_WORD.TABLE + "(";
        sql += "'" + TABLE_WORD._ID + "' integer primary key autoincrement, ";
        sql += "'" + TABLE_WORD.ENGLISH + "' varchar(500), ";
        sql += "'" + TABLE_WORD.CHINESE + "' varchar(3000), ";
        sql += "'" + TABLE_WORD.PHONETIC + "' varchar(100), ";
        sql += "'" + TABLE_WORD.DATE_ADDED + "' integer, ";
        sql += "'" + TABLE_WORD.DATE_UPDATED + "' integer, ";
        sql += "'" + TABLE_WORD.IMPORTANCE + "' integer, ";
        sql += "'" + TABLE_WORD.EN_PASS + "' integer, ";
        sql += "'" + TABLE_WORD.CN_PASS + "' integer, ";
        sql += "'" + TABLE_WORD.REVIEW_FLAG + "' integer, ";
        sql += "'" + TABLE_WORD.DIRECTION + "' integer, ";
        sql += "'" + TABLE_WORD.DELETE + "' integer, ";
        sql += "'" + TABLE_WORD.ARCHIVE + "' integer, ";
        sql += "'" + TABLE_WORD.EXT1 + "' integer, ";
        sql += "'" + TABLE_WORD.EXT2 + "' integer, ";
        sql += "'" + TABLE_WORD.SOURCE + "' varchar(50), ";
        sql += "'" + TABLE_WORD.EXT4 + "' varchar(50))";
        db.execSQL(sql);
    }

    private void createDataTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_DATA.TABLE + "(";
        sql += "'" + TABLE_DATA._ID + "' integer primary key autoincrement, ";
        sql += "'" + TABLE_DATA.WORD_ID + "' integer, ";
        sql += "'" + TABLE_DATA.MIME_ID + "' integer, ";
        sql += "'" + TABLE_DATA.MIME_TYPE + "' varchar(20), ";
        sql += "'" + TABLE_DATA.DATA1 + "' integer, ";
        sql += "'" + TABLE_DATA.DATA2 + "' integer, ";
        sql += "'" + TABLE_DATA.DATA3 + "' varchar(50), ";
        sql += "'" + TABLE_DATA.DATA4 + "' varchar(50), ";
        sql += "'" + TABLE_DATA.DATA5 + "' varchar(500), ";
        sql += "'" + TABLE_DATA.DATA6 + "' varchar(500))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
