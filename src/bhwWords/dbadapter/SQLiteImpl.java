package bhwWords.dbadapter;

import java.util.ArrayList;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import bhwWords.provider.DictProvider;

public class SQLiteImpl implements DbAdaInterface {
    private Context mContext;
    private static final int FIRST_COLUMN = 0;

    public SQLiteImpl(Object dbWrapper) {
        mContext = (Context) dbWrapper;
    }

    @Override
    public int applyBatch(ArrayList<Object> list) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        for (Object object : list) {
            operations.add((ContentProviderOperation) object);
        }
        try {
            mContext.getContentResolver().applyBatch(DictProvider.AUTHORITIES, operations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Object buildInsertOperation(String table, String[] keys, String[] values) {
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < keys.length; i++) {
            contentValues.put(keys[i], values[i]);
        }
        ContentProviderOperation operation = ContentProviderOperation.newInsert(getUri(table))
                .withValues(contentValues).build();
        return operation;
    }

    @Override
    public Object buildDeleteOperation(String table, String condition) {
        ContentProviderOperation operation = ContentProviderOperation.newDelete(getUri(table))
                .withSelection(condition, null).build();
        return operation;
    }

    private Uri getUri(String table) {
        switch (table) {
        case DbAdaTable.TABLE_WORD.TABLE:
            return DictProvider.CONTENT_URI;
        case DbAdaTable.TABLE_PROPERTY.TABLE:
            return DictProvider.PROPERTY_URI;
        case DbAdaTable.TABLE_ITEM.TABLE:
            return DictProvider.ITEM_URI;
        case DbAdaTable.TABLE_EXAMPLE.TABLE:
            return DictProvider.EXAMPLE_URI;
        default:
            break;
        }
        return null;
    }

    @Override
    public int insert(String table, String[] keys, String[] values) {
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < keys.length; i++) {
            contentValues.put(keys[i], values[i]);
        }
        mContext.getContentResolver().insert(getUri(table), contentValues);
        return 0;
    }

    @Override
    public int getLastIndex(String table) {
        String projection[] = { "MAX(_id)" };
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(getUri(table), projection, null, null,
                    null);
            if (null == cursor) {
                return -1;
            }
            if (!cursor.isClosed() && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int lastid = cursor.getInt(FIRST_COLUMN);
                return lastid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }

    @Override
    public int queryId(String table, String condition) {
        String projection[] = { "_id" };
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(getUri(table), projection, condition,
                    null, null);
            if (null == cursor) {
                return -1;
            }
            if (!cursor.isClosed() && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int id = cursor.getInt(FIRST_COLUMN);
                return id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }

    @Override
    public String queryField(String table, String field, String condition) {
        String projection[] = { field };
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(getUri(table), projection, condition,
                    null, null);
            if (null == cursor) {
                return null;
            }
            if (!cursor.isClosed() && cursor.getCount() > 0) {
                cursor.moveToFirst();
                String string = cursor.getString(FIRST_COLUMN);
                return string;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    public ArrayList<Integer> queryIdList(String table, String condition) {
        ArrayList<Integer> list = new ArrayList<>();
        String projection[] = { "_id" };
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(getUri(table), projection, condition,
                    null, null);
            if (null == cursor) {
                return list;
            }
            if (!cursor.isClosed() && cursor.getCount() > 0) {
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    list.add(cursor.getInt(FIRST_COLUMN));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    @Override
    public ArrayList<String> queryFieldList(String table, String field, String condition) {
        ArrayList<String> list = new ArrayList<>();
        String projection[] = { field };
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(getUri(table), projection, condition,
                    null, null);
            if (null == cursor) {
                return list;
            }
            if (!cursor.isClosed() && cursor.getCount() > 0) {
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(FIRST_COLUMN));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    @Override
    public ArrayList<ArrayList<String>> queryFieldsList(String table, ArrayList<String> fields,
            String condition) {
        Cursor cursor = null;
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        try {
            cursor = mContext.getContentResolver()
                    .query(getUri(table), null, condition, null, null);
            if (null == cursor) {
                return list;
            }

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                ArrayList<String> wordData = new ArrayList<>();
                for (String field : fields) {
                    wordData.add(cursor.getString(cursor.getColumnIndex(field)));
                }
                list.add(wordData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    @Override
    public boolean createTableIfNotExist(String table, String[] columns) {
        return false;
    }

    @Override
    public boolean isTableExist(String table) {
        return true;
    }

    @Override
    public int executeSql(String sql) {
        return 0;
    }

    @Override
    public int delete(String table, String condition) {
        int delresult = mContext.getContentResolver().delete(getUri(table), condition, null);
        return delresult;
    }

    @Override
    public boolean getConnection(Object dbWrapper) {
        return true;
    }
}
