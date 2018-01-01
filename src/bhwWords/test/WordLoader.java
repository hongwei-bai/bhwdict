package bhwWords.test;

import java.util.ArrayList;

import android.app.Activity;
import bhwWords.filter.DateFilter;
import bhwWords.provider.WordDbHelper;
import bhwWords.sourcepicker.view.SourcePickerFragment;
import bhwWords.test.PickerFragment.PickListener;

public class WordLoader {
    public static final String LOAD_ARRAY[] = { "All", "Customized", "Browse Sources", "1 day fail",
            "2 day fail", "3 day fail", "1 week fail", "All fail", "1 day sentences", "3 day sentences",
            "1 week sentences", "All sentences", "Review" };
    public static final int LOAD_ALL = 0;
    public static final int LOAD_CUSTOMIZED = 1;
    public static final int LOAD_SOURCES = 2;
    public static final int LOAD_1_DAY_FAIL = 3;
    public static final int LOAD_2_DAY_FAIL = 4;
    public static final int LOAD_3_DAY_FAIL = 5;
    public static final int LOAD_1_WEEK_FAIL = 6;
    public static final int LOAD_ALL_FAIL = 7;
    public static final int LOAD_1_DAY_SENTENCE = 8;
    public static final int LOAD_3_DAY_SENTENCE = 9;
    public static final int LOAD_1_WEEK_SENTENCE = 10;
    public static final int LOAD_ALL_SENTENCE = 11;
    public static final int LOAD_REVIEW = 12;
    public static final int LOAD_ADDITIONAL = LOAD_ARRAY.length;

    public static final int LOAD_IN_CUSTOMIZED = 98;
    public static final int LOAD_FROM_PROGRESS = 99;

    private int mLoaderId = LOAD_ALL;
    private PickerFragment mPickerFragment;
    private SourcePickerFragment mSourcePickerFragment;
    private PickData mPickData;
    private OnCustomizedPickCompleteListener mOnCustomizedPickCompleteListener;
    private int mAdditionalLoaderId = 0;

    public void customizedLoader(Activity activity) {
        mLoaderId = LOAD_CUSTOMIZED;
        mAdditionalLoaderId = LOAD_CUSTOMIZED;
        mPickerFragment = new PickerFragment(mPickData);
        mPickerFragment.show(activity.getFragmentManager(), "picker");
        mPickerFragment.setOnPickListener(new PickListener() {

            @Override
            public void onPick(PickData pickData) {
                mPickData = pickData;
                if (mOnCustomizedPickCompleteListener != null) {
                    mOnCustomizedPickCompleteListener.onCustomizedPickComplete();
                }
            }
        });
    }

    public void browseSourcesLoader(Activity activity) {
        mLoaderId = LOAD_SOURCES;
        mAdditionalLoaderId = LOAD_SOURCES;
        mSourcePickerFragment = new SourcePickerFragment();
        mSourcePickerFragment.show(activity.getFragmentManager(), "picker");
        mSourcePickerFragment.setOnPickListener(new SourcePickerFragment.PickListener() {

            @Override
            public void onPick(PickData pickData) {
                mPickData = pickData;
                if (mOnCustomizedPickCompleteListener != null) {
                    mOnCustomizedPickCompleteListener.onCustomizedPickComplete();
                }
            }
        });
    }

    public int getAdditionalLoaderId() {
        return mAdditionalLoaderId;
    }

    public interface OnCustomizedPickCompleteListener {
        public void onCustomizedPickComplete();
    }

    public void setOnCustomizedPickCompleteListener(OnCustomizedPickCompleteListener l) {
        mOnCustomizedPickCompleteListener = l;
    }

    public void setLoaderId(int loaderId) {
        if (loaderId == LOAD_FROM_PROGRESS || loaderId == LOAD_CUSTOMIZED) {
            mAdditionalLoaderId = loaderId;
        }
        mLoaderId = loaderId;
    }

    private ArrayList<WordData> mWordList = null;

    public void setLoadIdList(ArrayList<WordData> wordList) {
        mWordList = wordList;
    }

    private String getSelectionFromIdList() {
        String selection = WordDbHelper.TABLE_WORD._ID + " IN (";
        for (int i = 0; i < mWordList.size() - 1; i++) {
            selection += mWordList.get(i).id + ",";
        }
        selection += mWordList.get(mWordList.size() - 1).id + ")";
        return selection;
    }

    public String getSelection() {
        String selection = null;
        String FAIL_CLAUSE = "(" + WordDbHelper.TABLE_WORD.EN_PASS + " = 0" + " OR "
                + WordDbHelper.TABLE_WORD.CN_PASS + " = 0)";
        String SENTENCE_CLAUSE = WordDbHelper.TABLE_WORD.DIRECTION + " = 1";
        if (mLoaderId == LOAD_ADDITIONAL) {
            mLoaderId = mAdditionalLoaderId;
        }
        switch (mLoaderId) {
        case LOAD_FROM_PROGRESS:
            selection = getSelectionFromIdList();
            break;
        case LOAD_CUSTOMIZED:
        case LOAD_IN_CUSTOMIZED:
            selection = getCustomizedSelection();
            break;
        case LOAD_SOURCES:
            selection = getBrowseSourcesSelection();
            break;
        case LOAD_1_DAY_FAIL:
            selection = DateFilter.getLatestDayClause(1) + " AND " + FAIL_CLAUSE;
            break;
        case LOAD_2_DAY_FAIL:
            selection = DateFilter.getLatestDayClause(2) + " AND " + FAIL_CLAUSE;
            break;
        case LOAD_3_DAY_FAIL:
            selection = DateFilter.getLatestDayClause(3) + " AND " + FAIL_CLAUSE;
            break;
        case LOAD_1_WEEK_FAIL:
            selection = DateFilter.getLatestDayClause(7) + " AND " + FAIL_CLAUSE;
            break;
        case LOAD_ALL_FAIL:
            selection = FAIL_CLAUSE;
            break;
        case LOAD_1_DAY_SENTENCE:
            selection = DateFilter.getLatestDayClause(1) + " AND " + SENTENCE_CLAUSE;
            break;
        case LOAD_3_DAY_SENTENCE:
            selection = DateFilter.getLatestDayClause(3) + " AND " + SENTENCE_CLAUSE;
            break;
        case LOAD_1_WEEK_SENTENCE:
            selection = DateFilter.getLatestDayClause(7) + " AND " + SENTENCE_CLAUSE;
            break;
        case LOAD_ALL_SENTENCE:
            selection = SENTENCE_CLAUSE;
            break;
        case LOAD_REVIEW:
            selection = WordDbHelper.TABLE_WORD.REVIEW_FLAG + " = 1";
            break;
        case LOAD_ALL:
        default:
            break;
        }
        return selection;
    }

    public String[] getLoadArrayIncludeCustomized() {
        String array[] = new String[LOAD_ARRAY.length + 1];
        for (int i = 0; i < LOAD_ARRAY.length; i++) {
            array[i] = LOAD_ARRAY[i];
        }
        array[LOAD_ARRAY.length] = mPickData.description;
        return array;
    }

    public String[] getLoadArrayForProgress() {
        String array[] = new String[LOAD_ARRAY.length + 1];
        for (int i = 0; i < LOAD_ARRAY.length; i++) {
            array[i] = LOAD_ARRAY[i];
        }
        array[LOAD_ARRAY.length] = "Last time progress";
        return array;
    }

    private String getCustomizedSelection() {
        String selection = "";
        if (mPickData.dayFrom > 0) {
            selection += DateFilter.getLatestDayClause(mPickData.dayFrom, mPickData.dayTo);
        } else {
            selection += DateFilter.getLatestDayClause(mPickData.dayTo);
        }

        if (mPickData.wordOrSentence == PickerFragment.WORD_ONLY) {
            selection += " AND " + WordDbHelper.TABLE_WORD.DIRECTION + " IN (0,2)";
        } else if (mPickData.wordOrSentence == PickerFragment.SENTENCE_ONLY) {
            selection += " AND " + WordDbHelper.TABLE_WORD.DIRECTION + " = 1";
        }

        if (mPickData.passOrFail == PickerFragment.PASS_ONLY) {
            String subClause = WordDbHelper.TABLE_WORD.EN_PASS + " = 1 OR " + WordDbHelper.TABLE_WORD.CN_PASS
                    + " = 1";
            selection += " AND (" + subClause + ")";
        } else if (mPickData.passOrFail == PickerFragment.FAIL_ONLY) {
            String subClause = WordDbHelper.TABLE_WORD.EN_PASS + " = 0 OR " + WordDbHelper.TABLE_WORD.CN_PASS
                    + " = 0";
            selection += " AND (" + subClause + ")";
        }

        selection += " AND " + WordDbHelper.TABLE_WORD.IMPORTANCE + " >= " + mPickData.importanceAbove;
        return selection;
    }

    private String getBrowseSourcesSelection() {
        String selection = "";
        selection += WordDbHelper.TABLE_WORD.SOURCE + " = '" + mPickData.description + "'";
        return selection;
    }

    public int getPassOrFailRule() {
        if (null == mPickData) {
            return PickerFragment.BOTH;
        }
        return mPickData.passOrFail;
    }
}
