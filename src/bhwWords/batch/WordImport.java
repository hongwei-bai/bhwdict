package bhwWords.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import bhwWords.dict.constants.Constants;
import bhwWords.filter.DateFilter;
import bhwWords.provider.WordDbHelper;
import bhwWords.provider.WordProvider;

public class WordImport {
    private static final String EXTERNAL_PATH_ALTERNATIVE = Constants.PHONE_DIR.USER_FOLDER;
    private Context mContext;
    private ReportProgress reportProgressListener;

    public interface ReportProgress {
        public void onReport(String msg);
    }

    public void setReportProgressListener(ReportProgress l) {
        reportProgressListener = l;
    }

    public WordImport(Context context) {
        mContext = context;
    }

    public void importAll() {
        Cursor cursor = mContext.getContentResolver().query(WordProvider.CONTENT_URI, null, null,
                null, null);
        if (cursor == null || cursor.getCount() == 0) {
            importAdd(true);
        } else {
            // Toast.makeText(mContext,
            // "Can't import all when there are records!", Toast.LENGTH_LONG)
            // .show();
            report("Can't import all when there are records!");
        }
    }

    public void importAdd(boolean bAll) {
        report("start import...");
        // File path = Environment.getExternalStorageDirectory();
        File wordDir = new File(EXTERNAL_PATH_ALTERNATIVE);
        File file = new File(wordDir, Constants.PHONE_DIR.IMPORT_FILE);
        long date = new Date().getTime();
        if (!file.exists()) {
            // Toast.makeText(mContext, "import file not exist!",
            // Toast.LENGTH_LONG).show();
            report("import file not exist!");
            return;
        }
        FileOperation fileOperation = new FileOperation();
        report("read import.txt file...");
        String buffer = fileOperation.read(file);
        report("read import.txt file COMPLETE!");
        String wordlist[] = buffer.split("\n\n");
        // Toast.makeText(mContext, "import word number is " + wordlist.length,
        // Toast.LENGTH_LONG)
        // .show();
        report("import word number is " + wordlist.length);

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ArrayList<Integer> listWordIdUpdated = new ArrayList<>();
        int progress = 0;
        for (String wordstring : wordlist) {
            WordData wordData = new WordData();
            wordData.parse(wordstring);
            ContentValues values = new ContentValues();

            values.put(WordDbHelper.TABLE_WORD.ENGLISH, wordData.english);
            if (wordData.chinese != null) {
                values.put(WordDbHelper.TABLE_WORD.CHINESE, wordData.chinese);
            }
            if (wordData.phonetic != null) {
                values.put(WordDbHelper.TABLE_WORD.PHONETIC, wordData.phonetic);
            }
            if (wordData.importance != null) {
                values.put(WordDbHelper.TABLE_WORD.IMPORTANCE, wordData.importance);
            }
            if (wordData.direction != null) {
                int direction = Integer.valueOf(wordData.direction);
                values.put(WordDbHelper.TABLE_WORD.DIRECTION, direction);
            }
            if (wordData.source != null) {
                wordData.source = wordData.source.replace("'", "");
                values.put(WordDbHelper.TABLE_WORD.SOURCE, wordData.source);
            }
            if (wordData.dateAdded != null) {
                long dateAddedValue = DateFilter.buildCalenderDate(wordData.dateAdded);
                values.put(WordDbHelper.TABLE_WORD.DATE_ADDED, dateAddedValue);
            }
            if (wordData.newWord || bAll) {
                // new word
                if (!bAll) {
                    values.put(WordDbHelper.TABLE_WORD.DATE_ADDED, date);
                }
                values.put(WordDbHelper.TABLE_WORD.EN_PASS, 1); // new modified
                                                                // for pte
                values.put(WordDbHelper.TABLE_WORD.CN_PASS, 1); // new modified
                                                                // for pte
                values.put(WordDbHelper.TABLE_WORD.REVIEW_FLAG, 0);
                Uri uriRet = mContext.getContentResolver().insert(WordProvider.CONTENT_URI, values);
                wordData.id = (int) ContentUris.parseId(uriRet);
            } else {
                // update
                values.put(WordDbHelper.TABLE_WORD._ID, wordData.id);
                values.put(WordDbHelper.TABLE_WORD.DATE_UPDATED, date);
                Uri uri = ContentUris.withAppendedId(WordProvider.CONTENT_URI, wordData.id);
                ops.add(ContentProviderOperation.newUpdate(uri).withValues(values)
                        .withYieldAllowed(true).build());
                listWordIdUpdated.add(wordData.id);
            }
            if (wordData.listProperty != null) {
                Iterator<Entry<String, String>> it = wordData.listProperty.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                    ContentValues propertyValues = new ContentValues();
                    propertyValues.put(WordDbHelper.TABLE_DATA.WORD_ID, wordData.id);
                    propertyValues.put(WordDbHelper.TABLE_DATA.MIME_TYPE,
                            WordDbHelper.MIME_TYPE.PROPERTY);
                    propertyValues.put(WordDbHelper.TABLE_DATA.DATA3, entry.getKey());
                    propertyValues.put(WordDbHelper.TABLE_DATA.DATA4, entry.getValue());
                    ops.add(ContentProviderOperation.newInsert(WordProvider.DATA_URI)
                            .withValues(propertyValues).withYieldAllowed(true).build());
                }
            }
            if (wordData.listExample != null) {
                for (String exampleString : wordData.listExample) {
                    ContentValues exampleValues = new ContentValues();
                    exampleValues.put(WordDbHelper.TABLE_DATA.WORD_ID, wordData.id);
                    exampleValues.put(WordDbHelper.TABLE_DATA.MIME_TYPE,
                            WordDbHelper.MIME_TYPE.EXAMPLE);
                    exampleValues.put(WordDbHelper.TABLE_DATA.DATA5, exampleString);
                    ops.add(ContentProviderOperation.newInsert(WordProvider.DATA_URI)
                            .withValues(exampleValues).withYieldAllowed(true).build());
                }
            }
            if (wordData.listTopic != null) {
                for (String topicString : wordData.listTopic) {
                    ContentValues topicValues = new ContentValues();
                    topicValues.put(WordDbHelper.TABLE_DATA.WORD_ID, wordData.id);
                    topicValues
                            .put(WordDbHelper.TABLE_DATA.MIME_TYPE, WordDbHelper.MIME_TYPE.TOPIC);
                    topicValues.put(WordDbHelper.TABLE_DATA.DATA3, topicString);
                    ops.add(ContentProviderOperation.newInsert(WordProvider.DATA_URI)
                            .withValues(topicValues).withYieldAllowed(true).build());
                }
            }
            if (wordData.listCoherence != null) {
                for (String coherenceString : wordData.listCoherence) {
                    ContentValues coherenceValues = new ContentValues();
                    coherenceValues.put(WordDbHelper.TABLE_DATA.WORD_ID, wordData.id);
                    coherenceValues.put(WordDbHelper.TABLE_DATA.MIME_TYPE,
                            WordDbHelper.MIME_TYPE.COHERENCE);
                    coherenceValues.put(WordDbHelper.TABLE_DATA.DATA3, coherenceString);
                    ops.add(ContentProviderOperation.newInsert(WordProvider.DATA_URI)
                            .withValues(coherenceValues).withYieldAllowed(true).build());
                }
            }
            progress++;
            report("import word progress: " + progress + "/" + wordlist.length);
            Log.d(Constants.TAG, "WordImport import word progress: " + progress + "/"
                    + wordlist.length);
        }
        // delete data first
        if (listWordIdUpdated.size() > 0) {
            String selection = WordDbHelper.TABLE_DATA.WORD_ID + " IN (";
            for (int i = 0; i < listWordIdUpdated.size() - 1; i++) {
                selection += listWordIdUpdated.get(i) + ",";
            }
            selection += listWordIdUpdated.get(listWordIdUpdated.size() - 1) + ")";
            mContext.getContentResolver().delete(WordProvider.DATA_URI, selection, null);
        }

        report("start database applyBatch...");
        try {
            @SuppressWarnings("unused")
            ContentProviderResult result[] = mContext.getContentResolver().applyBatch(
                    WordProvider.AUTHORITIES, ops);
            // Log.d("aaaa", "result.size = " + result.length + ", r[0] = " +
            // result[0]);
        } catch (RemoteException e) {
            // Toast.makeText(mContext, "import exception!",
            // Toast.LENGTH_LONG).show();
            report("import RemoteException!");
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // Toast.makeText(mContext, "import exception!",
            // Toast.LENGTH_LONG).show();
            report("import OperationApplicationException!");
            e.printStackTrace();
        }
        // Toast.makeText(mContext, "import finished.",
        // Toast.LENGTH_LONG).show();
        report("import finished.");
    }

    public void clearWords(Context context) {
        String conditionAll = "_id >= 0";
        context.getContentResolver().delete(WordProvider.CONTENT_URI, conditionAll, null);
        context.getContentResolver().delete(WordProvider.DATA_URI, conditionAll, null);
        // Toast.makeText(context, "debug6_clearWords comlete.",
        // Toast.LENGTH_LONG).show();
        report("clear word complete!");
    }

    private void report(String msg) {
        if (reportProgressListener != null) {
            reportProgressListener.onReport(msg);
        }
    }
}
