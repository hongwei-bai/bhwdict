package bhwWords.provider;

import bhwWords.dbadapter.DbAdaTable;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DictProvider extends ContentProvider {
    private static UriMatcher uriMatcher;
    private static final int URI_WORD = 1;
    private static final int URI_WORD_FILE = 2;
    private static final int URI_PROPERTY = 3;
    private static final int URI_PROPERTY_ID = 4;
    private static final int URI_ITEM = 5;
    private static final int URI_ITEM_ID = 6;
    private static final int URI_EXAMPLE = 7;
    private static final int URI_EXAMPLE_ID = 8;

    private DictDbHelper dbHelper;
    public static final String AUTHORITIES = "bhwWords.dict";
    public static final Uri AUTHORITIES_URI = Uri.parse("content://" + AUTHORITIES);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITIES_URI, "word");
    public static final Uri PROPERTY_URI = Uri.withAppendedPath(AUTHORITIES_URI, "property");
    public static final Uri ITEM_URI = Uri.withAppendedPath(AUTHORITIES_URI, "item");
    public static final Uri EXAMPLE_URI = Uri.withAppendedPath(AUTHORITIES_URI, "example");

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITIES, "word", URI_WORD);
        uriMatcher.addURI(AUTHORITIES, "word/*", URI_WORD_FILE);
        uriMatcher.addURI(AUTHORITIES, "property", URI_PROPERTY);
        uriMatcher.addURI(AUTHORITIES, "property/#", URI_PROPERTY_ID);
        uriMatcher.addURI(AUTHORITIES, "item", URI_ITEM);
        uriMatcher.addURI(AUTHORITIES, "item/#", URI_ITEM_ID);
        uriMatcher.addURI(AUTHORITIES, "example", URI_EXAMPLE);
        uriMatcher.addURI(AUTHORITIES, "example/#", URI_EXAMPLE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DictDbHelper(getContext());
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
            tableNameString = DbAdaTable.TABLE_WORD.TABLE;
            break;
        case URI_PROPERTY:
            tableNameString = DbAdaTable.TABLE_PROPERTY.TABLE;
            break;
        case URI_PROPERTY_ID:
            tableNameString = DbAdaTable.TABLE_PROPERTY.TABLE;
            int propertyId = (int) ContentUris.parseId(uri);
            selection = DbAdaTable.TABLE_PROPERTY._ID + "=" + propertyId;
            break;
        case URI_ITEM:
            tableNameString = DbAdaTable.TABLE_ITEM.TABLE;
            break;
        case URI_ITEM_ID:
            tableNameString = DbAdaTable.TABLE_ITEM.TABLE;
            int itemId = (int) ContentUris.parseId(uri);
            selection = DbAdaTable.TABLE_ITEM._ID + "=" + itemId;
            break;
        case URI_EXAMPLE:
            tableNameString = DbAdaTable.TABLE_EXAMPLE.TABLE;
            break;
        case URI_EXAMPLE_ID:
            tableNameString = DbAdaTable.TABLE_EXAMPLE.TABLE;
            int exampleId = (int) ContentUris.parseId(uri);
            selection = DbAdaTable.TABLE_EXAMPLE._ID + "=" + exampleId;
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
            tableNameString = DbAdaTable.TABLE_WORD.TABLE;
            break;
        case URI_PROPERTY:
            tableNameString = DbAdaTable.TABLE_PROPERTY.TABLE;
            break;
        case URI_ITEM:
            tableNameString = DbAdaTable.TABLE_ITEM.TABLE;
            break;
        case URI_EXAMPLE:
            tableNameString = DbAdaTable.TABLE_EXAMPLE.TABLE;
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
            tableNameString = DbAdaTable.TABLE_WORD.TABLE;
            break;
        case URI_WORD_FILE:
            tableNameString = DbAdaTable.TABLE_WORD.TABLE;
            String filename = parseString(uri);
            selection = DbAdaTable.TABLE_WORD.FILE + "='" + filename + "'";
            break;
        case URI_PROPERTY:
            tableNameString = DbAdaTable.TABLE_PROPERTY.TABLE;
            break;
        case URI_PROPERTY_ID:
            tableNameString = DbAdaTable.TABLE_PROPERTY.TABLE;
            int propertyId = (int) ContentUris.parseId(uri);
            selection = DbAdaTable.TABLE_PROPERTY._ID + "=" + propertyId;
            break;
        case URI_ITEM:
            tableNameString = DbAdaTable.TABLE_ITEM.TABLE;
            break;
        case URI_ITEM_ID:
            tableNameString = DbAdaTable.TABLE_ITEM.TABLE;
            int itemId = (int) ContentUris.parseId(uri);
            selection = DbAdaTable.TABLE_ITEM._ID + "=" + itemId;
            break;
        case URI_EXAMPLE:
            tableNameString = DbAdaTable.TABLE_EXAMPLE.TABLE;
            break;
        case URI_EXAMPLE_ID:
            tableNameString = DbAdaTable.TABLE_EXAMPLE.TABLE;
            int exampleId = (int) ContentUris.parseId(uri);
            selection = DbAdaTable.TABLE_EXAMPLE._ID + "=" + exampleId;
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
            tableNameString = DbAdaTable.TABLE_WORD.TABLE;
            break;
        case URI_PROPERTY_ID:
            tableNameString = DbAdaTable.TABLE_PROPERTY.TABLE;
            int propertyId = (int) ContentUris.parseId(uri);
            selection = DbAdaTable.TABLE_PROPERTY._ID + "=" + propertyId;
            break;
        case URI_ITEM_ID:
            tableNameString = DbAdaTable.TABLE_ITEM.TABLE;
            int itemId = (int) ContentUris.parseId(uri);
            selection = DbAdaTable.TABLE_ITEM._ID + "=" + itemId;
            break;
        case URI_EXAMPLE_ID:
            tableNameString = DbAdaTable.TABLE_EXAMPLE.TABLE;
            int exampleId = (int) ContentUris.parseId(uri);
            selection = DbAdaTable.TABLE_EXAMPLE._ID + "=" + exampleId;
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

    private String parseString(Uri uri) {
        if (null == uri) {
            return "";
        }
        String string = uri.toString();
        int pos = string.lastIndexOf("/");
        if (pos < 0) {
            return "";
        }
        return string.substring(pos + 1);
    }
}
