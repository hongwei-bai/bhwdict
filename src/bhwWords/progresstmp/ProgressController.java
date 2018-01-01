package bhwWords.progresstmp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.media.MediaScannerConnection;
import bhwWords.batch.FileOperation;
import bhwWords.filter.DateFilter;
import bhwWords.test.WordData;

public class ProgressController {
    private static final String FOLDER = "bhwword";
    private static final String TMP_FILE = "tmp.txt";
    private static final String EXTERNAL_PATH_ALTERNATIVE = "storage/emulated/0";
    private Context mContext;
    private ProgressTmpData mTmpData;
    private ProgressTmpData mTmpDataReader;
    private final boolean DEBUG = false;

    public ProgressController(Context context) {
        mContext = context;
    }

    public void buildBasicTmpData(ArrayList<WordData> list, int cur) {
        mTmpData = new ProgressTmpData();
        if (cur > 0) {
            mTmpData.mListWord = list;
            mTmpData.mCurrentPosition = cur;
            mTmpData.mSaved = true;
        } else {
            mTmpData.mSaved = false;
        }
    }

    public void clearProgress() {
        mTmpData = new ProgressTmpData();
        mTmpData.mSaved = false;
        saveTmpData();
    }

    public void saveTmpData() {
        // File path = Environment.getExternalStorageDirectory();
        File wordDir = new File(EXTERNAL_PATH_ALTERNATIVE, FOLDER);
        File file = new File(wordDir, TMP_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOperation fileOperation = new FileOperation();
        mTmpData.mInterruptTime = Calendar.getInstance().getTimeInMillis();
        fileOperation.write(file, mTmpData.toBuffer(), false);
        if (DEBUG) {
            String scanPath[] = { file.toString() };
            MediaScannerConnection.scanFile(mContext, scanPath, null, null);
        }
    }

    public boolean loadTmpData() {
        // File path = Environment.getExternalStorageDirectory();
        File wordDir = new File(EXTERNAL_PATH_ALTERNATIVE, FOLDER);
        File file = new File(wordDir, TMP_FILE);
        if (!file.exists()) {
            return false;
        }
        FileOperation fileOperation = new FileOperation();
        String buffer = fileOperation.read(file);
        mTmpDataReader = new ProgressTmpData();
        mTmpDataReader.parse(buffer);
        return mTmpDataReader.mSaved;
    }

    public String getFormatedTime() {
        return DateFilter.toExportTimeString(mTmpDataReader.mInterruptTime);
    }

    public ArrayList<WordData> getListWord() {
        return mTmpDataReader.mListWord;
    }

    public int getCurPosition() {
        return mTmpDataReader.mCurrentPosition;
    }
}
