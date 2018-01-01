package bhwWords.dict.model;

import java.util.ArrayList;

import bhwWords.dbadapter.DbAdaInterface;
import bhwWords.dbadapter.DbAdaTable;
import bhwWords.dbadapter.SQLiteImpl;
import bhwWords.dict.constants.Constants;

public class SearchHintModel {
    private int mLimit = Constants.SEARCH_MAX_LIMIT;
    private DbAdaInterface mDbAdaInterface;
    private static int mSize = 0;
    public static final int MAX_HINT = 20;

    public SearchHintModel(Object dbWrapper) {
        mDbAdaInterface = new SQLiteImpl(dbWrapper);
    }

    public ArrayList<String> getStubWordList() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < MAX_HINT; i++) {
            list.add(" ");
        }
        mSize = 0;
        return list;
    }

    public ArrayList<String> getSuggestionWordList(String keyword) {
        ArrayList<String> accurateList = getAccurateMatchWordList(keyword);
        ArrayList<String> prefixList = getPrefixMatchWordList(keyword);
        ArrayList<String> list = new ArrayList<>(accurateList);
        for (String prefix : prefixList) {
            if (list.size() >= MAX_HINT) {
                break;
            }
            if (!list.contains(prefix)) {
                list.add(prefix);
            }
        }

        mSize = list.size();
        return list;
    }

    private ArrayList<String> getAccurateMatchWordList(String keyword) {
        String limitClause = "";
        if (mLimit > 0) {
            limitClause = " LIMIT " + mLimit;
        }
        String condition = DbAdaTable.TABLE_WORD.ENGLISH + " = '" + keyword + "' " + limitClause;
        return mDbAdaInterface.queryFieldList(DbAdaTable.TABLE_WORD.TABLE,
                DbAdaTable.TABLE_WORD.ENGLISH, condition);
    }

    @SuppressWarnings("unused")
    private ArrayList<String> getPhraseMatchWordList(String keyword) {
        String limitClause = "";
        if (mLimit > 0) {
            limitClause = " LIMIT " + mLimit;
        }
        String condition = DbAdaTable.TABLE_WORD.ENGLISH + " LIKE '" + keyword + " %' "
                + limitClause;
        return mDbAdaInterface.queryFieldList(DbAdaTable.TABLE_WORD.TABLE,
                DbAdaTable.TABLE_WORD.ENGLISH, condition);
    }

    private ArrayList<String> getPrefixMatchWordList(String keyword) {
        String limitClause = "";
        if (mLimit > 0) {
            limitClause = " LIMIT " + mLimit;
        }
        String condition = DbAdaTable.TABLE_WORD.ENGLISH + " LIKE '" + keyword + "%' "
                + limitClause;
        return mDbAdaInterface.queryFieldList(DbAdaTable.TABLE_WORD.TABLE,
                DbAdaTable.TABLE_WORD.ENGLISH, condition);
    }

    public int getSize() {
        return mSize;
    }
}
