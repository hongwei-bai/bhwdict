package bhwWords.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class WordProvider extends ContentProvider {
    private static UriMatcher uriMatcher;
    private static final int URI_WORD = 1;
    private static final int URI_WORD_ID = 2;
    private static final int URI_DATA = 3;
    private static final int URI_DATA_ID = 4;
    private WordDbHelper dbHelper;
    public static final String AUTHORITIES = "bhwWords.provider";
    public static final Uri AUTHORITIES_URI = Uri.parse("content://" + AUTHORITIES);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITIES_URI, "word");
    public static final Uri DATA_URI = Uri.withAppendedPath(AUTHORITIES_URI, "data");

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITIES, "word", URI_WORD);
        uriMatcher.addURI(AUTHORITIES, "word/#", URI_WORD_ID);
        uriMatcher.addURI(AUTHORITIES, "data", URI_DATA);
        uriMatcher.addURI(AUTHORITIES, "data/#", URI_DATA_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new WordDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor;
        String tableNameString = "";
        switch (uriMatcher.match(uri)) {
        case URI_WORD:
            tableNameString = WordDbHelper.TABLE_WORD.TABLE;
            break;
        case URI_WORD_ID:
            tableNameString = WordDbHelper.TABLE_WORD.TABLE;
            int id = (int) ContentUris.parseId(uri);
            selection = WordDbHelper.TABLE_WORD._ID + " = " + id;
            break;
        case URI_DATA:
            tableNameString = WordDbHelper.TABLE_DATA.TABLE;
            break;
        case URI_DATA_ID:
            tableNameString = WordDbHelper.TABLE_DATA.TABLE;
            int wordId = (int) ContentUris.parseId(uri);
            selection = WordDbHelper.TABLE_DATA.WORD_ID + " = " + wordId;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI" + uri);
        }
        cursor = db.query(tableNameString, projection, selection, selectionArgs, null, null, null);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;
        String tableNameString = "";
        switch (uriMatcher.match(uri)) {
        case URI_WORD:
            tableNameString = WordDbHelper.TABLE_WORD.TABLE;
            break;
        case URI_DATA:
            tableNameString = WordDbHelper.TABLE_DATA.TABLE;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI" + uri);
        }
        rowId = db.insert(tableNameString, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        throw new IllegalArgumentException("Unknown URI" + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;
        String tableNameString = "";
        switch (uriMatcher.match(uri)) {
        case URI_WORD:
            tableNameString = WordDbHelper.TABLE_WORD.TABLE;
            break;
        case URI_WORD_ID:
            tableNameString = WordDbHelper.TABLE_WORD.TABLE;
            int id = (int) ContentUris.parseId(uri);
            selection = WordDbHelper.TABLE_WORD._ID + " = " + id;
            break;
        case URI_DATA:
            tableNameString = WordDbHelper.TABLE_DATA.TABLE;
            break;
        case URI_DATA_ID:
            tableNameString = WordDbHelper.TABLE_DATA.TABLE;
            int wordId = (int) ContentUris.parseId(uri);
            selection = WordDbHelper.TABLE_DATA.WORD_ID + " = " + wordId;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        rowId = db.delete(tableNameString, selection, selectionArgs);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
        }
        return (int) rowId;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;
        String tableNameString = "";
        switch (uriMatcher.match(uri)) {
        case URI_WORD:
            tableNameString = WordDbHelper.TABLE_WORD.TABLE;
            break;
        case URI_WORD_ID:
            tableNameString = WordDbHelper.TABLE_WORD.TABLE;
            int id = (int) ContentUris.parseId(uri);
            selection = WordDbHelper.TABLE_WORD._ID + " = " + id;
            break;
        case URI_DATA:
            tableNameString = WordDbHelper.TABLE_DATA.TABLE;
            break;
        case URI_DATA_ID:
            tableNameString = WordDbHelper.TABLE_DATA.TABLE;
            int wordId = (int) ContentUris.parseId(uri);
            selection = WordDbHelper.TABLE_DATA.WORD_ID + " = " + wordId;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI" + uri);
        }
        rowId = db.update(tableNameString, values, selection, selectionArgs);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return (int) rowId;
        }
        throw new IllegalArgumentException("Unknown URI" + uri);
    }

}
