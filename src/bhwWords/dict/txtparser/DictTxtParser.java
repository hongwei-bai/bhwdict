package bhwWords.dict.txtparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;
import bhwWords.dict.constants.Constants;

abstract public class DictTxtParser {
    private static final String TAG = Constants.TAG;
    private ArrayList<WordData> mData;
    private ArrayList<String> mLines = new ArrayList<>();
    private File mFile = null;

    public boolean parseTxtFile(File mFile) {
        this.mFile = mFile;
        if (parseTxtFile()) {
            mData = parseFile(mFile.getName(), this.mLines);
            if (mData != null && !mData.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    abstract protected ArrayList<WordData> parseFile(String filename, ArrayList<String> mLines);

    private boolean parseTxtFile() {
        mLines.clear();
        try {
            String encoding = Constants.DB.DICT_TXT_ENCODE;
            if (mFile.isFile() && mFile.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(mFile), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    mLines.add(lineTxt);
                }
                read.close();
                return true;
            } else {
                Log.d(TAG, "DictTxtParser File not found!");
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "DictTxtParser Exception reading mFile!");
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<WordData> getData() {
        return mData;
    }

    public void clearData() {
        mData.clear();
    }
}
