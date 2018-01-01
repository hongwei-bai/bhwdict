package bhwWords.dict.txtparser;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bhwWords.dict.constants.DictConstants.RegexConstants;

import android.util.Log;

public class BracketUtils {
    private static final String TAG = "BracketUtils";
    private String mString;
    private ArrayList<Integer> mBracketPositionList = new ArrayList<>();
    private ArrayList<String> mBracketTypeList = new ArrayList<>();
    private int mBracketPairCount = 0;

    private ArrayList<Integer> mLeftBracketOrginazied = new ArrayList<>();
    private ArrayList<Integer> mRightBracketOrginazied = new ArrayList<>();

    public BracketUtils(String mString) {
        this.mString = mString;
        parse();
    }

    public boolean inBracket(int position) {
        if (0 == mBracketPairCount) {
            return false;
        }

        // Log.d("mString=" + mString);
        // Log.d("mBracketPairCount=" + mBracketPairCount + ", size=" +
        // mLeftBracketOrginazied.size());
        for (int i = 0; i < mBracketPairCount; i++) {
            if (position > mLeftBracketOrginazied.get(i)
                    && position < mRightBracketOrginazied.get(i)) {
                // Log.d("[在括号里] mString=" + mString);
                return true;
            }
        }

        return false;
    }

    private void parse() {
        goThroughAllBrackets();

        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < mBracketPositionList.size(); i++) {
            int position = mBracketPositionList.get(i);
            String type = mBracketTypeList.get(i);
            if (type.equals("(")) {
                stack.push(position);
            } else {
                if (!stack.isEmpty()) {
                    int pairPos = stack.pop();
                    mLeftBracketOrginazied.add(pairPos);
                    mRightBracketOrginazied.add(position);
                    // Log.d("pair:" + pairPos + " ~ " + position);
                } else {
                    if (matchNumberTilePattern(mString, position)) {
                        Log.i(TAG, "legal unmatched bracket found, pos=" + position + ", mString="
                                + mString);
                    } else {
                        Log.e(TAG, "bracket mismatch! mString=" + mString);
                    }
                }
            }
        }
    }

    private void goThroughAllBrackets() {
        for (int from = 0;;) {
            int posL = mString.indexOf("(", from);
            int posR = mString.indexOf(")", from);
            int posMin = -1;
            String type = null;
            if (-1 == posL) {
                posMin = posR;
                type = ")";
            } else if (-1 == posR) {
                posMin = posL;
                type = "(";
            } else {
                if (posL < posR) {
                    posMin = posL;
                    type = "(";
                } else {
                    posMin = posR;
                    type = ")";
                }
            }
            if (posMin > -1) {
                mBracketPositionList.add(posMin);
                mBracketTypeList.add(type);
                from = posMin + 1;
            } else {
                break;
            }
        }
    }

    private boolean matchNumberTilePattern(String fullstring, int curPos) {
        String targetString = null;
        if (curPos >= 2) {
            targetString = fullstring.substring(curPos - 2, curPos + 1);
        } else if (curPos >= 1) {
            targetString = fullstring.substring(curPos - 1, curPos + 1);
        } else {
            return false;
        }
        Pattern pattern = Pattern.compile(RegexConstants.NUMBLE_TILE_APATTERN);
        Matcher matcher = pattern.matcher(targetString);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    private boolean checkBracketPair() {
        if (mBracketPositionList.size() % 2 == 1) {
            Log.d(TAG, "mBracketPositionList.size = " + mBracketPositionList.size());
            return false;
        }

        mBracketPairCount = mBracketPositionList.size() / 2;
        int i = 0;
        for (String s : mBracketTypeList) {
            if (s.equals("(")) {
                i++;
            }
        }
        if (i != mBracketPairCount) {
            Log.d(TAG, "mBracketPairCount = " + mBracketPairCount + ", i = " + i);
        }
        return i == mBracketPairCount;
    }
}
