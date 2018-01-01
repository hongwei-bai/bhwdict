package bhwWords.test;

import java.util.ArrayList;

import android.database.Cursor;
import bhwWords.provider.WordDbHelper;

abstract public class Rules {
    private ArrayList<WordData> mResultList = new ArrayList<>();
    private ArrayList<WordData> mOriginalList = new ArrayList<>();
    private Cursor mCursor;
    public final static int DIRECTION_EN = 0;
    public final static int DIRECTION_CN = 1;
    public final static int DIRECTION_BI = 2;

    private int mPassOrFailRule = PickerFragment.BOTH;

    public Rules() {
    }

    public Rules(int passOrFailRule) {
        mPassOrFailRule = passOrFailRule;
    }

    public ArrayList<WordData> exportList() {
        return mResultList;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    private void setCursor(Cursor cursor) {
        mCursor = cursor;
    }

    public void reset(Cursor cursor) {
        setCursor(cursor);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            int id = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
            int enPass = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.EN_PASS));
            int cnPass = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.CN_PASS));
            if (needTestEn(cursor)) {
                if (mPassOrFailRule == PickerFragment.BOTH || mPassOrFailRule == enPass) {
                    mOriginalList.add(new WordData(i, WordData.TEST_LANG_EN, id));
                }
            }
            if (needTestCn(cursor)) {
                if (mPassOrFailRule == PickerFragment.BOTH || mPassOrFailRule == cnPass) {
                    mOriginalList.add(new WordData(i, WordData.TEST_LANG_CN, id));
                }
            }
        }
        mResultList = doRules(mOriginalList);
    }

    private boolean needTestEn(Cursor cursor) {
        int direction = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.DIRECTION));
        return (direction == DIRECTION_BI || direction == DIRECTION_EN);
    }

    private boolean needTestCn(Cursor cursor) {
        int direction = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.DIRECTION));
        return (direction == DIRECTION_BI || direction == DIRECTION_CN);
    }

    abstract protected ArrayList<WordData> doRules(ArrayList<WordData> orgList);

    public WordData get(int index) {
        if (mResultList.isEmpty()) {
            return null;
        }
        return mResultList.get(index);
    }

    public void importFromList(ArrayList<WordData> importList, Cursor cursor) {
        mOriginalList = importList;
        mResultList = importList;
        mCursor = cursor;
    }

    public int getCount() {
        return mResultList.size();
    }

    abstract public String getType();
}
