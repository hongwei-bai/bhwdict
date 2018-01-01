package bhwWords.progresstmp;

import java.util.ArrayList;

import bhwWords.test.WordData;

public class ProgressTmpData {
    public boolean mSaved;
    public long mInterruptTime;
    public ArrayList<WordData> mListWord;
    public int mCurrentPosition;
    private static final String TOKEN = ",";

    public static final String keySaved = "saved";
    public static final String keyWordId = "wordid";
    public static final String keyWordPosition = "wordpos";
    public static final String keyWordTestLanguage = "wordlang";
    public static final String keyInterruptTime = "time";
    public static final String keyCurrentPosition = "currentpos";

    public boolean parse(String bufferin) {
        String lines[] = bufferin.split("\n");
        String idListString = null;
        String posListString = null;
        String langListString = null;
        for (String line : lines) {
            String pair[] = line.split("=");
            if (pair.length == 2) {
                switch (pair[0]) {
                case keySaved:
                    mSaved = Integer.valueOf(pair[1]) > 0;
                    break;
                case keyInterruptTime:
                    mInterruptTime = Long.valueOf(pair[1]);
                    break;
                case keyWordId:
                    idListString = pair[1];
                    break;
                case keyWordPosition:
                    posListString = pair[1];
                    break;
                case keyWordTestLanguage:
                    langListString = pair[1];
                    break;
                case keyCurrentPosition:
                    mCurrentPosition = Integer.valueOf(pair[1]);
                    break;
                default:
                    break;
                }
            }
        }
        mListWord = parseToArray(idListString, posListString, langListString);
        return mSaved;
    }

    public String toBuffer() {
        String buffer = "";
        buffer += keySaved + "=" + (mSaved ? 1 : 0) + "\n";
        buffer += keyInterruptTime + "=" + mInterruptTime + "\n";
        if (mSaved) {
            buffer += keyWordId + "=" + getListWordIdString(mListWord) + "\n";
            buffer += keyWordPosition + "=" + getListWordPositionString(mListWord) + "\n";
            buffer += keyWordTestLanguage + "=" + getListWordTestLanguageString(mListWord) + "\n";
            buffer += keyCurrentPosition + "=" + mCurrentPosition + "\n";
            buffer += "\n";
        }
        return buffer;
    }

    private String getListWordIdString(ArrayList<WordData> list) {
        String string = "";
        int i = 0;
        for (; i < list.size() - 1; i++) {
            string += list.get(i).id + TOKEN;
        }
        string += list.get(list.size() - 1).id;
        return string;
    }

    private String getListWordPositionString(ArrayList<WordData> list) {
        String string = "";
        int i = 0;
        for (; i < list.size() - 1; i++) {
            string += list.get(i).position + TOKEN;
        }
        string += list.get(list.size() - 1).position;
        return string;
    }

    private String getListWordTestLanguageString(ArrayList<WordData> list) {
        String string = "";
        int i = 0;
        for (; i < list.size() - 1; i++) {
            string += (list.get(i).isCN ? "1" : "0") + TOKEN;
        }
        string += list.get(list.size() - 1).isCN ? "1" : "0";
        return string;
    }

    private ArrayList<WordData> parseToArray(String idlistString, String posListString, String langListString) {
        ArrayList<WordData> resultList = new ArrayList<>();
        if (null == idlistString || null == posListString || null == langListString) {
            return resultList;
        }
        String[] idStrings = idlistString.split(TOKEN);
        String[] posStrings = posListString.split(TOKEN);
        String[] langStrings = langListString.split(TOKEN);
        for (int i = 0; i < idStrings.length; i++) {
            resultList.add(new WordData(Integer.valueOf(posStrings[i]), Integer.valueOf(langStrings[i]), Long
                    .valueOf(idStrings[i])));
        }
        return resultList;
    }
}
