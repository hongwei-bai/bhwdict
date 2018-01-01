package bhwWords.input;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import bhwWords.filter.DateFilter;
import bhwWords.provider.WordDbHelper;
import bhwWords.provider.WordProvider;
import bhwWords.test.Rules;

import com.bhw1899.bhwwords.R;

public class WordEditActivity extends Activity implements OnClickListener {
    private int mId;
    private EditText mEnglish;
    private EditText mChinese;
    private TextView mPhonetic;
    private TextView mCount;
    private RatingBar mRatingBar;
    private LinearLayout mExampleContainer;
    private ArrayMap<Integer, EditText> mEditTextMap;
    private Button mSaveButton;
    private CheckBox mCheckBox;
    private CheckBox mBiCheckBox;
    private CheckBox mReviewCheckBox;

    private String mBufferEnglish;
    private String mBufferChinese;
    private int mBufferRating;
    private int mBufferDirection;
    private boolean mBufferReviewFlag;
    private ArrayMap<Integer, String> mBufferExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_edit);
        mEnglish = (EditText) findViewById(R.id.english);
        mChinese = (EditText) findViewById(R.id.chinese);
        mPhonetic = (TextView) findViewById(R.id.phonetic);
        mCount = (TextView) findViewById(R.id.count);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mExampleContainer = (LinearLayout) findViewById(R.id.example_container);
        mSaveButton = (Button) findViewById(R.id.save);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);
        mBiCheckBox = (CheckBox) findViewById(R.id.checkboxbi);
        mReviewCheckBox = (CheckBox) findViewById(R.id.reviewFlag);
        mSaveButton.setOnClickListener(this);
        mEditTextMap = new ArrayMap<>();
        mBufferExample = new ArrayMap<>();

        mId = getIntent().getIntExtra("id", -1);
        Uri uri = ContentUris.withAppendedId(WordProvider.CONTENT_URI, mId);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            mBufferEnglish = cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.ENGLISH));
            mBufferChinese = cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.CHINESE));
            mEnglish.setText(mBufferEnglish);
            mChinese.setText(mBufferChinese);
            String phoneticString = cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.PHONETIC));
            if (phoneticString != null && !phoneticString.isEmpty()) {
                mPhonetic.setText(phoneticString);
            } else {
                mPhonetic.setVisibility(View.GONE);
            }
            int direction = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.DIRECTION));
            mBufferDirection = direction;
            mCheckBox.setChecked(direction == 1);
            mBiCheckBox.setChecked(direction == 2);
            mCheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckBox.isChecked()) {
                        mBiCheckBox.setChecked(false);
                    }
                }
            });
            mBiCheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBiCheckBox.isChecked()) {
                        mCheckBox.setChecked(false);
                    }
                }
            });
            int review = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.REVIEW_FLAG));
            mBufferReviewFlag = review > 0;
            mReviewCheckBox.setChecked(mBufferReviewFlag);

            mBufferRating = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.IMPORTANCE));
            mRatingBar.setRating(mBufferRating);
            int enPass = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.EN_PASS));
            int cnPass = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.CN_PASS));

            String countString = "";
            if (mBufferDirection == 0) {
                countString += "en:";
                countString += "  " + (enPass > 0 ? "pass" : "fail");
            } else if (mBufferDirection == 1) {
                countString += "CN:";
                countString += "  " + (cnPass > 0 ? "pass" : "fail");
            } else if (mBufferDirection == 2) {
                countString += "en:";
                countString += "  " + (enPass > 0 ? "pass" : "fail");
                countString += "/CN:";
                countString += "  " + (cnPass > 0 ? "pass" : "fail");
            }

            long dateAdded = cursor.getLong(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.DATE_ADDED));
            long dateUpdated = cursor.getLong(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.DATE_UPDATED));
            countString += "\n";
            countString += "added " + DateFilter.toString(dateAdded) + ", updated "
                    + DateFilter.toString(dateUpdated);

            mCount.setText(countString);
            cursor.close();
        }
        Uri dataUri = ContentUris.withAppendedId(WordProvider.DATA_URI, mId);
        String selection = WordDbHelper.TABLE_DATA.MIME_TYPE + " = " + WordDbHelper.MIME_TYPE.EXAMPLE;
        Cursor dataCursor = getContentResolver().query(dataUri, null, selection, null, null);
        if (dataCursor != null) {
            dataCursor.moveToPosition(-1);
            while (dataCursor.moveToNext()) {
                String mimeType = dataCursor.getString(dataCursor
                        .getColumnIndex(WordDbHelper.TABLE_DATA.MIME_TYPE));
                if (mimeType.equals(WordDbHelper.MIME_TYPE.EXAMPLE)) {
                    String exampleString = dataCursor.getString(dataCursor
                            .getColumnIndex(WordDbHelper.TABLE_DATA.DATA5));
                    EditText editText = new EditText(this);
                    editText.setText(exampleString);
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    mExampleContainer.addView(editText);
                    int dataId = dataCursor.getInt(dataCursor.getColumnIndex(WordDbHelper.TABLE_DATA._ID));
                    mEditTextMap.put(dataId, editText);
                    mBufferExample.put(dataId, exampleString);
                }
            }
            dataCursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.save == v.getId()) {
            processSave();
        }
    }

    private void processSave() {
        boolean bDiff = false;
        ContentValues values = new ContentValues();
        String afterEnglish = mEnglish.getText().toString();
        String afterChinese = mChinese.getText().toString();
        int afterRating = (int) mRatingBar.getRating();
        int afterDirection = 0;
        if (mCheckBox.isChecked()) {
            afterDirection = 1;
        } else if (mBiCheckBox.isChecked()) {
            afterDirection = 2;
        }
        values = appendCountValues(values, afterDirection);
        if (!afterEnglish.equals(mBufferEnglish)) {
            bDiff = true;
            values.put(WordDbHelper.TABLE_WORD.ENGLISH, afterEnglish);
        }
        if (!afterChinese.equals(mBufferChinese)) {
            bDiff = true;
            values.put(WordDbHelper.TABLE_WORD.CHINESE, afterChinese);
        }
        if (afterRating != mBufferRating) {
            bDiff = true;
            values.put(WordDbHelper.TABLE_WORD.IMPORTANCE, afterRating);
        }
        if (afterDirection != mBufferDirection) {
            bDiff = true;
            values.put(WordDbHelper.TABLE_WORD.DIRECTION, afterDirection);
        }
        boolean afterReviewFlag = mReviewCheckBox.isChecked();
        if (afterReviewFlag != mBufferReviewFlag) {
            bDiff = true;
            values.put(WordDbHelper.TABLE_WORD.REVIEW_FLAG, afterReviewFlag ? 1 : 0);
        }
        int ret = 0;
        if (bDiff) {
            Uri uri = ContentUris.withAppendedId(WordProvider.CONTENT_URI, mId);
            ret = getContentResolver().update(uri, values, null, null);
        }

        Iterator<Entry<Integer, EditText>> it = mEditTextMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, EditText> entry = (Map.Entry<Integer, EditText>) it.next();
            int id = entry.getKey();
            EditText editText = entry.getValue();
            String buffer = mBufferExample.get(id);
            String after = editText.getText().toString();
            String selection = WordDbHelper.TABLE_DATA._ID + " = " + id;
            ContentValues dataValues = new ContentValues();
            dataValues.put(WordDbHelper.TABLE_DATA.DATA5, after);
            if (!buffer.equals(after)) {
                ret += getContentResolver().update(WordProvider.DATA_URI, dataValues, selection, null);
            }
        }
        Toast.makeText(this, "Update complete! " + ret + " fields updated.", Toast.LENGTH_LONG).show();
    }

    private ContentValues appendCountValues(ContentValues values, int newDirection) {
        if (mBufferDirection == Rules.DIRECTION_BI) {
            if (newDirection == Rules.DIRECTION_EN) {
                values.put(WordDbHelper.TABLE_WORD.CN_PASS, -1);
            } else if (newDirection == Rules.DIRECTION_CN) {
                values.put(WordDbHelper.TABLE_WORD.EN_PASS, -1);
            }
        } else if (mBufferDirection == Rules.DIRECTION_EN) {
            if (newDirection == Rules.DIRECTION_CN) {
                values.put(WordDbHelper.TABLE_WORD.EN_PASS, -1);
                values.put(WordDbHelper.TABLE_WORD.CN_PASS, 0);
            } else if (newDirection == Rules.DIRECTION_BI) {
                values.put(WordDbHelper.TABLE_WORD.CN_PASS, 0);
            }
        } else if (mBufferDirection == Rules.DIRECTION_CN) {
            if (newDirection == Rules.DIRECTION_EN) {
                values.put(WordDbHelper.TABLE_WORD.EN_PASS, 0);
                values.put(WordDbHelper.TABLE_WORD.CN_PASS, -1);
            } else if (newDirection == Rules.DIRECTION_BI) {
                values.put(WordDbHelper.TABLE_WORD.EN_PASS, 0);
            }
        }
        return values;
    }
}
