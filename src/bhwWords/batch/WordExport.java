package bhwWords.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.support.v4.util.ArrayMap;
import android.widget.Toast;
import bhwWords.dict.constants.Constants;
import bhwWords.filter.DateFilter;
import bhwWords.provider.WordDbHelper;
import bhwWords.provider.WordProvider;

public class WordExport {
    private static final String EXTERNAL_PATH_ALTERNATIVE = Constants.PHONE_DIR.USER_FOLDER;
    private Context mContext;
    private HashMap<Integer, ArrayList<Integer>> mHashMap = new HashMap<>();
    private Cursor mCursorData;
    public static final int ALL = 0;
    public static final int LATEST_3_DAYS = 3;

    public WordExport(Context context) {
        mContext = context;
    }

    public void export(int para) {
        // File path = Environment.getExternalStorageDirectory();
        File wordDir = new File(EXTERNAL_PATH_ALTERNATIVE);
        File file = new File(wordDir, Constants.PHONE_DIR.EXPORT_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String selection = null;
        String msg;
        switch (para) {
        case ALL:
            msg = "exportAll finished.";
            break;
        case LATEST_3_DAYS:
            selection = DateFilter.getLatestDayClause(3);
            msg = "export latest 3 days words finished.";
            break;
        default:
            msg = "";
            break;
        }
        FileOperation fileOperation = new FileOperation();
        fileOperation.write(file, buildBuffer(selection));
        String scanPath[] = { file.toString() };
        MediaScannerConnection.scanFile(mContext, scanPath, null, null);

        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    private String buildBuffer(String selection) {
        String buffer = "";
        Cursor cursor = mContext.getContentResolver().query(WordProvider.CONTENT_URI, null,
                selection, null, WordDbHelper.TABLE_WORD._ID + " DESC");
        buildHashMap();
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                WordData word = new WordData();
                word.id = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
                word.english = cursor.getString(cursor
                        .getColumnIndex(WordDbHelper.TABLE_WORD.ENGLISH));
                word.chinese = cursor.getString(cursor
                        .getColumnIndex(WordDbHelper.TABLE_WORD.CHINESE));
                word.direction = cursor.getString(cursor
                        .getColumnIndex(WordDbHelper.TABLE_WORD.DIRECTION));
                word.phonetic = cursor.getString(cursor
                        .getColumnIndex(WordDbHelper.TABLE_WORD.PHONETIC));
                word.source = cursor.getString(cursor
                        .getColumnIndex(WordDbHelper.TABLE_WORD.SOURCE));
                word.importance = cursor.getString(cursor
                        .getColumnIndex(WordDbHelper.TABLE_WORD.IMPORTANCE));
                long dateLong = cursor.getLong(cursor
                        .getColumnIndex(WordDbHelper.TABLE_WORD.DATE_ADDED));
                word.dateAdded = DateFilter.toExportString(dateLong);
                if (mHashMap.containsKey(word.id)) {
                    ArrayList<Integer> list = mHashMap.get(word.id);
                    for (int position : list) {
                        mCursorData.moveToPosition(position);
                        String mimeType = mCursorData.getString(mCursorData
                                .getColumnIndex(WordDbHelper.TABLE_DATA.MIME_TYPE));
                        String data3 = mCursorData.getString(mCursorData
                                .getColumnIndex(WordDbHelper.TABLE_DATA.DATA3));
                        String data4 = mCursorData.getString(mCursorData
                                .getColumnIndex(WordDbHelper.TABLE_DATA.DATA4));
                        String data5 = mCursorData.getString(mCursorData
                                .getColumnIndex(WordDbHelper.TABLE_DATA.DATA5));
                        if (mimeType.equals(WordDbHelper.MIME_TYPE.PROPERTY)) {
                            if (word.listProperty == null) {
                                word.listProperty = new ArrayMap<>();
                            }
                            word.listProperty.put(data3, data4);
                        } else if (mimeType.equals(WordDbHelper.MIME_TYPE.EXAMPLE)) {
                            if (word.listExample == null) {
                                word.listExample = new ArrayList<>();
                            }
                            word.listExample.add(data5);
                        } else if (mimeType.equals(WordDbHelper.MIME_TYPE.TOPIC)) {
                            if (word.listTopic == null) {
                                word.listTopic = new ArrayList<>();
                            }
                            word.listTopic.add(data3);
                        } else if (mimeType.equals(WordDbHelper.MIME_TYPE.COHERENCE)) {
                            if (word.listCoherence == null) {
                                word.listCoherence = new ArrayList<>();
                            }
                            word.listCoherence.add(data3);
                        }
                    }
                }
                buffer += word.toBuffer();
            }
        }
        if (mCursorData != null) {
            mCursorData.close();
        }
        return buffer;
    }

    private void buildHashMap() {
        mCursorData = mContext.getContentResolver().query(WordProvider.DATA_URI, null, null, null,
                null);
        if (mCursorData != null) {
            mHashMap.clear();
            mCursorData.moveToPosition(-1);
            while (mCursorData.moveToNext()) {
                int wordid = mCursorData.getInt(mCursorData
                        .getColumnIndex(WordDbHelper.TABLE_DATA.WORD_ID));
                if (mHashMap.containsKey(wordid)) {
                    mHashMap.get(wordid).add(mCursorData.getPosition());
                } else {
                    ArrayList<Integer> list = new ArrayList<>();
                    list.add(mCursorData.getPosition());
                    mHashMap.put(wordid, list);
                }
            }
        }
    }
}
