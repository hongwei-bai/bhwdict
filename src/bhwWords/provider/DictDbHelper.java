package bhwWords.provider;

import bhwWords.dbadapter.DbAdaTable;
import bhwWords.dbadapter.DbAdaTable.INDEX;
import bhwWords.dbadapter.DbAdaTable.IndexStru;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DictDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DBNAME = "dict_db";

    public DictDbHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, DbAdaTable.TABLE_WORD.TABLE, DbAdaTable.TABLE_WORD.CREATER);
        createTable(db, DbAdaTable.TABLE_PROPERTY.TABLE, DbAdaTable.TABLE_PROPERTY.CREATER);
        createTable(db, DbAdaTable.TABLE_ITEM.TABLE, DbAdaTable.TABLE_ITEM.CREATER);
        createTable(db, DbAdaTable.TABLE_EXAMPLE.TABLE, DbAdaTable.TABLE_EXAMPLE.CREATER);
        createIndex(db);
    }

    private void createTable(SQLiteDatabase db, String table, String[] creator) {
        String sql = "CREATE TABLE " + table + "(";
        sql += "'" + DbAdaTable.TABLE_WORD._ID + "' integer primary key, ";
        for (int i = 0; i < creator.length - 1; i++) {
            sql += creator[i] + ",";
        }
        sql += creator[creator.length - 1] + ")";
        db.execSQL(sql);
        Log.d("aaaa", "createTable sql=" + sql);
    }

    private void createIndex(SQLiteDatabase db) {
        for (IndexStru indexStru : INDEX.ARRAY) {
            String name = indexStru.table + "_" + indexStru.field;
            String sql = "CREATE " + indexStru.type + " INDEX " + name + " ON " + indexStru.table
                    + "(" + indexStru.field + ")";
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
