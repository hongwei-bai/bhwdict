package bhwWords.dict.model;

import android.util.Log;
import bhwWords.dbadapter.DbAdaInterface;
import bhwWords.dbadapter.DbAdaTable.INDEX;
import bhwWords.dbadapter.DbAdaTable.IndexStru;
import bhwWords.dbadapter.DbAdaTable.TABLE_EXAMPLE;
import bhwWords.dbadapter.DbAdaTable.TABLE_IDIOM;
import bhwWords.dbadapter.DbAdaTable.TABLE_ITEM;
import bhwWords.dbadapter.DbAdaTable.TABLE_PROPERTY;
import bhwWords.dbadapter.DbAdaTable.TABLE_WORD;
import bhwWords.dbadapter.SQLiteImpl;
import bhwWords.dict.constants.Constants;

public class DbInit {
    private DbAdaInterface dbAdaInterface;

    public DbInit(Object dbWrapper) {
        dbAdaInterface = new SQLiteImpl(dbWrapper);
        if (!dbAdaInterface.getConnection(dbWrapper)) {
            Log.d(Constants.TAG, "DbInit getConnection failed!");
            return;
        }
    }

    public void init() {
        createTableIfNotExist();
    }

    private boolean createTableIfNotExist() {
        boolean bResult = true;
        boolean needCreateIndex = dbAdaInterface.isTableExist(TABLE_WORD.TABLE);
        bResult &= createWordTable();
        bResult &= createPropertyTable();
        bResult &= createItemTable();
        bResult &= createExampleTable();
        bResult &= createIdiomTable();
        if (!needCreateIndex) {
            createIndex();
        }
        if (!bResult) {
            Log.e(Constants.TAG, "DbInit createTableIfNotExist failed!");
        }
        return bResult;
    }

    private boolean createWordTable() {
        return dbAdaInterface.createTableIfNotExist(TABLE_WORD.TABLE, TABLE_WORD.CREATER);
    }

    private boolean createPropertyTable() {
        return dbAdaInterface.createTableIfNotExist(TABLE_PROPERTY.TABLE, TABLE_PROPERTY.CREATER);
    }

    private boolean createItemTable() {
        return dbAdaInterface.createTableIfNotExist(TABLE_ITEM.TABLE, TABLE_ITEM.CREATER);
    }

    private boolean createExampleTable() {
        return dbAdaInterface.createTableIfNotExist(TABLE_EXAMPLE.TABLE, TABLE_EXAMPLE.CREATER);
    }

    private boolean createIdiomTable() {
        return dbAdaInterface.createTableIfNotExist(TABLE_IDIOM.TABLE, TABLE_IDIOM.CREATER);
    }

    private boolean createIndex() {
        for (IndexStru indexStru : INDEX.ARRAY) {
            String name = indexStru.table + "_" + indexStru.field;
            String sql = "CREATE " + indexStru.type + " INDEX " + name + " ON " + indexStru.table
                    + "(" + indexStru.field + ")";
            dbAdaInterface.executeSql(sql);
        }
        return true;
    }
}
