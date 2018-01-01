package bhwWords.dict.model;

import java.io.File;
import java.util.ArrayList;

import android.util.Log;
import bhwWords.dbadapter.DbAdaInterface;
import bhwWords.dbadapter.DbAdaTable;
import bhwWords.dbadapter.SQLiteImpl;
import bhwWords.dict.constants.Constants;
import bhwWords.dict.txtparser.DictTxtParser;
import bhwWords.dict.txtparser.Hierarchy;
import bhwWords.dict.txtparser.OxfordEnglish2ChineseDictTxtParser;
import bhwWords.dict.txtparser.WordData;
import bhwWords.dict.txtparser.WordProperty;

public class DictInstall implements DbAdaTable, Constants.DB {
    private static final String TAG = Constants.TAG;
    private ReportProgressListener mReportProgressListener;
    private ArrayList<Object> mBatchSqlWord = new ArrayList<>();
    private ArrayList<Object> mBatchSqlProperty = new ArrayList<>();
    private ArrayList<Object> mBatchSqlItem = new ArrayList<>();
    private ArrayList<Object> mBatchSqlExample = new ArrayList<>();
    private DbAdaInterface mDbAdaInterface;
    private int mWordid = 0;
    private int mPropertyid = 0;
    private int mItemid = 0;
    // private int mExampleid = 0;
    private int mProgress = 0;

    private DictTxtParser mParser;
    private ArrayList<WordData> mDatalist;
    private ArrayList<String> mFilelist = new ArrayList<>();

    public DictInstall(Object dbWrapper) {
        mDbAdaInterface = new SQLiteImpl(dbWrapper);
        mParser = new OxfordEnglish2ChineseDictTxtParser();
        mDatalist = new ArrayList<>();
    }

    public boolean installDict() {
        reportProgress("start installDict...");
        File root = new File(DICT_PATH);
        recursiveFolders(root);

        if (!isAndroid()) {
            deleteAllWords(mFilelist);
            if (isDictExist()) {
                insertDataIncremental(mDatalist);
            } else {
                insertDataNew(mDatalist);
            }
            mFilelist.clear();
            mDatalist.clear();
        }

        reportProgress("installDict all complete!");
        reportProgress("[COMPLETE]");
        return true;
    }

    public void uninstallDict() {
        reportProgress("Start uninstall dictionary.", 0, 1);
        String conditionAll = "_id >= 0";
        mDbAdaInterface.delete(TABLE_WORD.TABLE, conditionAll);
        mDbAdaInterface.delete(TABLE_PROPERTY.TABLE, conditionAll);
        mDbAdaInterface.delete(TABLE_ITEM.TABLE, conditionAll);
        mDbAdaInterface.delete(TABLE_EXAMPLE.TABLE, conditionAll);
        reportProgress("Uninstall complete.", 1, 1);
    }

    public void setReportProgressListener(ReportProgressListener l) {
        mReportProgressListener = l;
    }

    private boolean recursiveFolders(File path) {

        if (path.isFile()) {
            String filename = path.getName();
            int pos = filename.lastIndexOf(".");
            if (pos > 0) {
                String extension = filename.substring(pos + 1, filename.length());
                if (extension.equalsIgnoreCase(DICT_TXT_EXTENSION)) {
                    boolean parseTxtResult = parseTxtFile(path);
                    if (isAndroid() && parseTxtResult) {
                        deleteAllWords(mFilelist);
                        String msg = "parse file: " + path + ", mDatalist.size = "
                                + mDatalist.size();
                        Log.d(TAG, "DictInstall " + msg);
                        reportProgress(msg);
                        if (isDictExist()) {
                            insertDataIncremental(mDatalist);
                        } else {
                            insertDataNew(mDatalist);
                        }
                        mFilelist.clear();
                        mDatalist.clear();
                    }
                    return parseTxtResult;
                }
            }
        }

        boolean result = false;
        if (path.isDirectory()) {
            File files[] = path.listFiles();
            for (File file : files) {
                result &= recursiveFolders(file);
            }
        }

        return result;
    }

    private boolean parseTxtFile(File txtFile) {
        Log.d(TAG, "DictInstall parseTxtFile file<" + txtFile + ">");
        if (mParser.parseTxtFile(txtFile)) {
            mDatalist.addAll(mParser.getData());
            mParser.clearData();
            mFilelist.add(txtFile.getName());
            return true;
        }
        return false;
    }

    public interface ReportProgressListener {
        public void onReportProgress(int mProgress, String msg);
    }

    private void insertDataNew(ArrayList<WordData> wordDataList) {
        resetCounter();
        insertData(wordDataList);
    }

    private void insertDataIncremental(ArrayList<WordData> wordDataList) {
        updateCounterFromDbTables();
        insertData(wordDataList);
    }

    private boolean isDictExist() {
        return mDbAdaInterface.isTableExist(TABLE_WORD.TABLE);
    }

    private void resetCounter() {
        mWordid = 0;
        mPropertyid = 0;
        mItemid = 0;
        // mExampleid = 0;
    }

    private void updateCounterFromDbTables() {
        mWordid = mDbAdaInterface.getLastIndex(TABLE_WORD.TABLE) + 1;
        mPropertyid = mDbAdaInterface.getLastIndex(TABLE_PROPERTY.TABLE) + 1;
        mItemid = mDbAdaInterface.getLastIndex(TABLE_ITEM.TABLE) + 1;
        // mExampleid = 0;
    }

    private boolean insertData(ArrayList<WordData> wordDataList) {
        boolean bResult = true;
        mBatchSqlWord.clear();
        mBatchSqlProperty.clear();
        mBatchSqlItem.clear();
        mBatchSqlExample.clear();

        Log.i(TAG, "DictInstall build word structures...");
        reportProgress("Start build dictionary.", 0, 1);
        for (WordData wordData : wordDataList) {
            String wordKeys[] = { TABLE_WORD._ID, TABLE_WORD.ENGLISH, TABLE_WORD.FILE };
            String wordValues[] = { Integer.toString(mWordid), wordData.english, wordData.file };
            mBatchSqlWord.add(mDbAdaInterface.buildInsertOperation(TABLE_WORD.TABLE, wordKeys,
                    wordValues));

            for (WordProperty property : wordData.properties) {
                String propertyKeys[] = { TABLE_PROPERTY._ID, TABLE_PROPERTY.PROPERTY,
                        TABLE_PROPERTY.CONTENT, TABLE_PROPERTY.PRONUNCIATION_RAW,
                        TABLE_PROPERTY.PRONUNCIATION, TABLE_PROPERTY.PARENT_ID };
                String propertyValues[] = { Integer.toString(mPropertyid), property.property,
                        property.content, property.pronunciationRaw, property.pronunciation,
                        Integer.toString(mWordid) };
                mBatchSqlProperty.add(mDbAdaInterface.buildInsertOperation(TABLE_PROPERTY.TABLE,
                        propertyKeys, propertyValues));
                Hierarchy hierarchy = property.items;
                if (hierarchy != null) {
                    insertHierarchyTop(hierarchy, mPropertyid);
                }
                mPropertyid++;
            }
            mWordid++;
        }

        insertSqlBatch();
        Log.i(TAG, "DictInstall insertBatch complete.");
        reportProgress("Install complete.", 1, 1);
        return bResult;
    }

    private void insertSqlBatch() {
        Log.i(TAG, "DictInstall insert words...");
        insertSqlBatch("Insert words", mBatchSqlWord);
        Log.i(TAG, "DictInstall insert properties...");
        insertSqlBatch("Insert properties", mBatchSqlProperty);
        Log.i(TAG, "DictInstall insert items...");
        insertSqlBatch("Insert items", mBatchSqlItem);
    }

    private void insertSqlBatch(String msg, ArrayList<Object> all) {
        final int BATCH = 1000;
        ArrayList<Object> batch = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            batch.add(all.get(i));
            if (batch.size() >= BATCH) {
                mDbAdaInterface.applyBatch(batch);
                batch.clear();
                reportProgress(msg, i, all.size());
            }
        }
        if (batch.size() > 0) {
            mDbAdaInterface.applyBatch(batch);
        }
        reportProgress(msg, batch.size(), all.size());
    }

    private void reportProgress(String msg, int i, int total) {
        int wordIndication = i * 100 / total;
        if (wordIndication != mProgress && wordIndication % 1 == 0) {
            Log.d(TAG, "mProgress: " + wordIndication + "%");
            mProgress = wordIndication;
        }
    }

    private void reportProgress(String msg) {
        if (mReportProgressListener != null) {
            mReportProgressListener.onReportProgress(0, msg);
        }
    }

    private void insertHierarchyTop(Hierarchy hierarchy, int propertyId) {
        if (hierarchy.isleaf) {
            insertHierarchy(hierarchy, propertyId, 0, 0);
            buildExampleBatchSql(hierarchy, mItemid);
        } else {
            // 1,2,3...
            for (int i = 0; i < hierarchy.list.size(); i++) {
                Hierarchy hierarchyLvl1 = hierarchy.list.get(i);
                insertHierarchy(hierarchyLvl1, propertyId, 1, i);
                if (hierarchyLvl1.item != null) {
                    buildExampleBatchSql(hierarchyLvl1, mItemid);
                }

                if (!hierarchyLvl1.isleaf) {
                    // (a) (b)...
                    for (int j = 0; j < hierarchyLvl1.list.size(); j++) {
                        Hierarchy hierarchyLvl2 = hierarchyLvl1.list.get(j);
                        insertHierarchy(hierarchyLvl2, propertyId, 2, j);
                        buildExampleBatchSql(hierarchyLvl2, mItemid);
                    }
                }
            }
        }
    }

    private int insertHierarchy(Hierarchy hierarchy, int propertyId, int level, int sequence) {
        int containInformation = hierarchy.item != null ? 1 : 0;
        String itemKeys[] = { TABLE_ITEM._ID, TABLE_ITEM.ITEM, TABLE_ITEM.LEVELNO,
                TABLE_ITEM.SEQUENCE, TABLE_ITEM.CONTAIN_INFORMATION, TABLE_ITEM.EXPLAINATION,
                TABLE_ITEM.EXPLAINATION_ENG, TABLE_ITEM.EXPLAINATION_CHN, TABLE_ITEM.PARENT_ID };

        if (hierarchy.item != null) {
            String itemValues[] = { Integer.toString(mItemid), hierarchy.index,
                    Integer.toString(level), Integer.toString(sequence),
                    Integer.toString(containInformation), hierarchy.item.explaination,
                    hierarchy.item.explainationEng, hierarchy.item.explainationChn,
                    Integer.toString(propertyId) };
            mBatchSqlItem.add(mDbAdaInterface.buildInsertOperation(TABLE_ITEM.TABLE, itemKeys,
                    itemValues));
        } else {
            String itemValues[] = { Integer.toString(mItemid), hierarchy.index,
                    Integer.toString(level), Integer.toString(sequence),
                    Integer.toString(containInformation), null, null, null,
                    Integer.toString(propertyId) };
            mBatchSqlItem.add(mDbAdaInterface.buildInsertOperation(TABLE_ITEM.TABLE, itemKeys,
                    itemValues));
        }
        mItemid++;
        return 0;
    }

    private void buildExampleBatchSql(Hierarchy hierarchy, int itemId) {
        // if (hierarchy.item.examples != null) {
        // for (String example : hierarchy.item.examples) {
        // String exampleKeys[] = { TABLE_EXAMPLE._ID, TABLE_EXAMPLE.EXAMPLE,
        // TABLE_EXAMPLE.PARENT_ID };
        // String exampleValues[] = { Integer.toString(exampleid), example,
        // Integer.toString(itemId) };
        // mBatchSqlExample.add(mDbAdaInterface.buildInsertOperationWrapper(TABLE_EXAMPLE.TABLE,
        // exampleKeys, exampleValues));
        // exampleid++;
        // }
        // }
    }

    private boolean deleteAllWords(ArrayList<String> mFilelist) {
        if (mFilelist.isEmpty()) {
            return true;
        }
        ArrayList<Object> batchSql = new ArrayList<>();
        String condition = "";
        condition += TABLE_WORD.FILE + " IN (";
        for (int i = 0; i < mFilelist.size() - 1; i++) {
            String file = mFilelist.get(i);
            condition += "'" + file + "'" + ",";
        }
        condition += "'" + mFilelist.get(mFilelist.size() - 1) + "'";
        condition += ")";
        Object sql = mDbAdaInterface.buildDeleteOperation(TABLE_WORD.TABLE, condition);
        batchSql.add(sql);
        ArrayList<Integer> wordIdList = mDbAdaInterface.queryIdList(TABLE_WORD.TABLE, condition);

        if (wordIdList.isEmpty()) {
            return true;
        }
        condition = buildInConditionClause(TABLE_PROPERTY.PARENT_ID, wordIdList);
        batchSql.add(mDbAdaInterface.buildDeleteOperation(TABLE_PROPERTY.TABLE, condition));
        ArrayList<Integer> propertyIdList = mDbAdaInterface.queryIdList(TABLE_PROPERTY.TABLE,
                condition);

        if (propertyIdList.isEmpty()) {
            return true;
        }
        condition = buildInConditionClause(TABLE_ITEM.PARENT_ID, propertyIdList);
        batchSql.add(mDbAdaInterface.buildDeleteOperation(TABLE_ITEM.TABLE, condition));
        ArrayList<Integer> itemIdList = mDbAdaInterface.queryIdList(TABLE_ITEM.TABLE, condition);

        if (itemIdList.isEmpty()) {
            return true;
        }
        condition = buildInConditionClause(TABLE_EXAMPLE.PARENT_ID, itemIdList);
        batchSql.add(mDbAdaInterface.buildDeleteOperation(TABLE_EXAMPLE.TABLE, condition));
        Log.d(TAG, "deleteAllWords batchSql.size=" + batchSql.size());
        mDbAdaInterface.applyBatch(batchSql);
        return true;
    }

    private String buildInConditionClause(String field, ArrayList<Integer> idlist) {
        String condition = "";
        condition += field + " IN (";
        for (int i = 0; i < idlist.size() - 1; i++) {
            int id = idlist.get(i);
            condition += id + ",";
        }
        condition += idlist.get(idlist.size() - 1);
        condition += ")";
        return condition;
    }

    private boolean isAndroid() {
        return "Android".equals(Constants.APP.PLATFORM);
    }
}
