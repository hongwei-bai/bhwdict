package bhwWords.test;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import bhwWords.dict.constants.Constants;
import bhwWords.dict.model.SpeakWord;
import bhwWords.input.ItemPresenter;
import bhwWords.input.MenuAction;
import bhwWords.progresstmp.ProgressController;
import bhwWords.provider.WordDbHelper;
import bhwWords.provider.WordProvider;
import bhwWords.test.TestController.RoundFinishListener;
import bhwWords.test.WordLoader.OnCustomizedPickCompleteListener;

import com.bhw1899.bhwwords.R;

public class TestActivity extends Activity implements OnClickListener, OnItemSelectedListener,
        android.content.DialogInterface.OnClickListener, RoundFinishListener,
        OnCustomizedPickCompleteListener {
    private TextView mDetailInfomation;
    private Button mPassButton;
    private Button mFailButton;
    private IrrevertableButton mSeeButton;
    private TextView mWordHintTextView;
    private TextView mImportance;
    private Spinner mSpinner;
    private ArrayAdapter<String> mArrayAdapter;
    private Button mEditButton;
    private Button mNextButton;
    private Button mRepeatButton;
    private StatusButton mShiftLanguageButton;
    private StatusButton speakSwitchButton;

    private Rules mRules;
    private ProgressController mProgressController;
    private TestController mTestController;
    private WordLoader mWordLoader;

    public static final String SEE_BUTTON_FALSE_SEE = "See";
    public static final String SEE_BUTTON_TRUE_REPEAT = "Rept";
    public static final String LANGUAGE_BUTTON_TRUE_CN = "中";
    public static final String LANGUAGE_BUTTON_FALSE_EN = "英";
    public static final boolean LANGUAGE_BUTTON_CN = true;
    public static final boolean LANGUAGE_BUTTON_EN = false;

    public static final String SPEAK_ON = "音";
    public static final String SPEAK_OFF = "静";

    public Cursor reload() {
        Cursor cursor = getContentResolver().query(WordProvider.CONTENT_URI, null,
                mWordLoader.getSelection(), null, null);
        return cursor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUIComponent();
        SpeakWord.getInstance().init(this);
        mTestController = new TestController();
        mTestController.setOnRoundFinishListener(this);
        mWordLoader = new WordLoader();
        mWordLoader.setOnCustomizedPickCompleteListener(this);

        mProgressController = new ProgressController(this);
        if (mProgressController.loadTmpData()) {
            String msg = "";
            String time = mProgressController.getFormatedTime();
            int wordNumber = mProgressController.getListWord().size();
            long curCount = mProgressController.getCurPosition() + 1;
            msg += "Continue with " + time + " session " + curCount + " of " + wordNumber
                    + " words?";
            new AlertDialog.Builder(this).setMessage((CharSequence) msg)
                    .setPositiveButton(" Continue ", this).setTitle("Alert")
                    .setNegativeButton("   No   ", this).show();
            Log.d("aaaa", "in onCreate tid = " + Thread.currentThread().getId());
        }
    }

    private void startAllNew() {
        closeCursor();
        Cursor cursor = reload();
        mRules = new RandomRules(mWordLoader.getPassOrFailRule());
        mRules.reset(cursor);
        mTestController.resetCounter();
        mTestController.setRoundCount(mRules.getCount());

        if (mRules.getCount() > 0) {
            navigateWord();
        }
        updateLayout();
    }

    private void recoverProgress() {
        mWordLoader.setLoaderId(WordLoader.LOAD_FROM_PROGRESS);
        mWordLoader.setLoadIdList(mProgressController.getListWord());
        Cursor cursor = reload();
        mRules = new NormalRules();
        mRules.importFromList(mProgressController.getListWord(), cursor);
        mTestController.setRoundCount(mRules.getCount());
        mTestController.setCounter(mProgressController.getCurPosition());

        updateSpinner(mWordLoader.getLoadArrayForProgress());
        navigateWord();
        updateLayout();
    }

    private void navigateNextWord() {
        SpeakWord.getInstance().stopSpeaking();
        if (mTestController.goToNextWord()) {
            navigateWord();
        }
    }

    private void navigatePreviousWord() {
        mTestController.goToPreviousWord();
        navigateWord();
    }

    private void navigateWord() {
        mSeeButton.setStatus(false);
        WordData wordData = mRules.get(mTestController.getCurrentCount());
        mShiftLanguageButton.setStatus(wordData.isCN);
        ShowCurrentWord(mRules.getCursor(), wordData.position);
    }

    private void speakWordRepeat() {
        if (!speakSwitchButton.getStatus()) {
            return;
        }
        Cursor cursor = mRules.getCursor();
        boolean isCN = mShiftLanguageButton.getStatus();
        String en = cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.ENGLISH));
        String cn = cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.CHINESE));
        if (isCN && Constants.DB.VOICE.equals(cn)) {
            SpeakWord.getInstance().speakEnglishNow(en);
        } else {
            SpeakWord.getInstance().speakEnglishNow(en);
        }
    }

    private void navigateShiftLanguage() {
        WordData wordData = mRules.get(mTestController.getCurrentCount());
        ShowCurrentWord(mRules.getCursor(), wordData.position);
    }

    public void updateLayout() {
        if (mRules.getCount() > 0) {
            mPassButton.setVisibility(View.VISIBLE);
            mFailButton.setVisibility(View.VISIBLE);
            mSeeButton.setVisibility(View.VISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
            mImportance.setVisibility(View.VISIBLE);
            mShiftLanguageButton.setVisibility(View.VISIBLE);
        } else {
            mWordHintTextView.setText("No word");
            mPassButton.setVisibility(View.GONE);
            mFailButton.setVisibility(View.GONE);
            mSeeButton.setVisibility(View.GONE);
            mNextButton.setVisibility(View.GONE);
            mImportance.setVisibility(View.GONE);
            mShiftLanguageButton.setVisibility(View.GONE);
            mDetailInfomation.setVisibility(View.GONE);
            mEditButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d("aaaa", "in onClick tid = " + Thread.currentThread().getId());
        if (which == DialogInterface.BUTTON_POSITIVE) {
            recoverProgress();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            mProgressController.clearProgress();
            startAllNew();
        }
    }

    private void initUIComponent() {
        setContentView(R.layout.test);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mDetailInfomation = (TextView) findViewById(R.id.message);
        mDetailInfomation.setMovementMethod(new ScrollingMovementMethod());

        mPassButton = (Button) findViewById(R.id.pass);
        mFailButton = (Button) findViewById(R.id.fail);
        mEditButton = (Button) findViewById(R.id.edit);
        mNextButton = (Button) findViewById(R.id.next);
        mRepeatButton = (Button) findViewById(R.id.repeat);

        mWordHintTextView = (TextView) findViewById(R.id.word_hint);
        mImportance = (TextView) findViewById(R.id.importance);
        mSpinner = (Spinner) findViewById(R.id.spinner);

        mSeeButton = (IrrevertableButton) findViewById(R.id.see);
        mSeeButton.setButtonString(SEE_BUTTON_TRUE_REPEAT, SEE_BUTTON_FALSE_SEE);
        mShiftLanguageButton = (StatusButton) findViewById(R.id.shiftLanguage);
        mShiftLanguageButton.setButtonString(LANGUAGE_BUTTON_TRUE_CN, LANGUAGE_BUTTON_FALSE_EN);

        speakSwitchButton = (StatusButton) findViewById(R.id.speakSwitch);
        speakSwitchButton.setButtonString(SPEAK_ON, SPEAK_OFF);
        speakSwitchButton.setStatus(false);

        mShiftLanguageButton.setOnClickListener(this);
        speakSwitchButton.setOnClickListener(this);
        mPassButton.setOnClickListener(this);
        mFailButton.setOnClickListener(this);
        mSeeButton.setOnClickListener(this);
        mEditButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mRepeatButton.setOnClickListener(this);
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                WordLoader.LOAD_ARRAY);
        mSpinner.setAdapter(mArrayAdapter);
        mSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(new SaveProgressTask()).start();
    }

    private class SaveProgressTask implements Runnable {
        public void run() {
            if (mProgressController != null && mRules != null) {
                mProgressController.buildBasicTmpData(mRules.exportList(),
                        mTestController.getCurrentCount());
                mProgressController.saveTmpData();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // int loaderId = intent.getIntExtra("loaderid", -1);
        // int position = intent.getIntExtra("position", -1);
        // if (loaderId > -1 && position > -1) {
        // if (mLoaderId != loaderId) {
        // // test from editor
        // mLoaderId = loaderId;
        // mIntentPosition = position;
        // mSpinner.setSelection(loaderId);
        // } else if (position > -1) {
        // // test from editor
        // reload(RULE_TYPE_NORMAL);
        // mPosition = mRules.setCurrentPosition(position);
        // ShowCurrentWord();
        // } else {
        // // go test
        // ShowCurrentWord();
        // }
        // }
    }

    public void ShowCurrentWord(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        boolean isCN = mShiftLanguageButton.getStatus();
        updateButtonStatus();
        int id = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
        updateKeyButtonStatus(isCN, id);

        String en = cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.ENGLISH));
        String cn = cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.CHINESE));
        updatePuzzleText(isCN, isCN ? cn : en);

        int importance = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.IMPORTANCE));
        updateWordTestInfo(importance);

        if (speakSwitchButton.getStatus()) {
            if (isCN) {
                if (Constants.DB.VOICE.equals(cn)) {
                    SpeakWord.getInstance().speakEnglishNow(en);
                }
            } else {
                SpeakWord.getInstance().speakEnglishNow(en);
            }
        }
    }

    private void updateButtonStatus() {
        mDetailInfomation.setVisibility(View.INVISIBLE);
        mEditButton.setVisibility(View.GONE);
    }

    private void updateKeyButtonStatus(boolean isCN, int id) {
        String projection[] = { isCN ? WordDbHelper.TABLE_WORD.CN_PASS
                : WordDbHelper.TABLE_WORD.EN_PASS };
        String selection = WordDbHelper.TABLE_WORD._ID + "=" + id;
        Cursor cursor = getContentResolver().query(WordProvider.CONTENT_URI, projection, selection,
                null, null);
        if (null == cursor) {
            return;
        }
        if (cursor.moveToFirst()) {
            int pass = cursor.getInt(0);
            boolean bPass = (pass == 1);
            mPassButton.setEnabled(!bPass);
            mFailButton.setEnabled(bPass);
        }
        cursor.close();
    }

    private void updatePuzzleText(boolean isCN, String text) {
        if (text != null) {
            if (isCN) {
                if (text.length() > 500) {
                    mWordHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                } else if (text.length() > 200) {
                    mWordHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                } else {
                    mWordHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                }
            } else {
                if (text.length() > 50) {
                    mWordHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                } else if (text.length() > 20) {
                    mWordHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                } else {
                    mWordHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                }
            }
            mWordHintTextView.setText(text);
        } else {
            mWordHintTextView.setText("");
        }

    }

    private void updateWordTestInfo(int importance) {
        String text = "";
        for (int i = 0; i < importance; i++) {
            text += "*";
        }
        text += "\n";
        text += (mTestController.getCurrentCount() + 1) + "/" + mRules.getCount()
                + mRules.getType();
        mImportance.setText(text);
        float alpha = ItemPresenter.getImportanceAlpha(importance);
        mWordHintTextView.setAlpha(alpha);
    }

    public void toggleDetail() {
        if (!mSeeButton.getStatus()) {
            seeDetail();
            mDetailInfomation.setVisibility(View.VISIBLE);
            mEditButton.setVisibility(View.VISIBLE);
        }
        speakWord(mSeeButton.getStatus());
    }

    private void speakWord(boolean includeExample) {
        Cursor cursor = mRules.getCursor();
        String englishWord = cursor.getString(cursor
                .getColumnIndex(WordDbHelper.TABLE_WORD.ENGLISH));
        if (speakSwitchButton.getStatus()) {
            SpeakWord.getInstance().speakEnglishNow(englishWord);
        }

        if (!includeExample) {
            return;
        }

        int wordId = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
        Uri uri = ContentUris.withAppendedId(WordProvider.DATA_URI, wordId);
        Cursor dataCursor = getContentResolver().query(uri, null, null, null, null);
        String example = null;
        if (dataCursor != null) {
            dataCursor.moveToPosition(-1);
            while (dataCursor.moveToNext()) {
                String mimeType = dataCursor.getString(dataCursor
                        .getColumnIndex(WordDbHelper.TABLE_DATA.MIME_TYPE));
                if (mimeType.equals(WordDbHelper.MIME_TYPE.EXAMPLE)) {
                    example = dataCursor.getString(dataCursor
                            .getColumnIndex(WordDbHelper.TABLE_DATA.DATA5));
                }
            }
            dataCursor.close();
        }

        if (speakSwitchButton.getStatus()) {
            if (example != null && !example.isEmpty()) {
                SpeakWord.getInstance().speakEnglish(example);
            }
        }
    }

    private void seeDetail() {
        String detail = "";
        Cursor cursor = mRules.getCursor();

        String phonetic = cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.PHONETIC));
        if (phonetic != null) {
            detail += phonetic + "\n";
        }
        boolean isPuzzleCn = mShiftLanguageButton.getStatus();
        if (isPuzzleCn) {
            detail += cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.ENGLISH));
        } else {
            detail += cursor.getString(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.CHINESE));
        }
        detail += "\n";

        int wordId = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
        Uri uri = ContentUris.withAppendedId(WordProvider.DATA_URI, wordId);
        Cursor dataCursor = getContentResolver().query(uri, null, null, null, null);
        if (dataCursor != null) {
            dataCursor.moveToPosition(-1);
            ArrayList<String> listTopic = new ArrayList<>();
            ArrayList<String> listCoherence = new ArrayList<>();
            while (dataCursor.moveToNext()) {
                String mimeType = dataCursor.getString(dataCursor
                        .getColumnIndex(WordDbHelper.TABLE_DATA.MIME_TYPE));
                if (mimeType.equals(WordDbHelper.MIME_TYPE.TOPIC)) {
                    String topic = dataCursor.getString(dataCursor
                            .getColumnIndex(WordDbHelper.TABLE_DATA.DATA3));
                    listTopic.add(topic);
                }
                if (mimeType.equals(WordDbHelper.MIME_TYPE.COHERENCE)) {
                    String coherence = dataCursor.getString(dataCursor
                            .getColumnIndex(WordDbHelper.TABLE_DATA.DATA3));
                    listCoherence.add(coherence);
                }
            }
            detail += "\n";
            if (listTopic.size() > 0) {
                detail += "[topic] ";
                for (int i = 0; i < listTopic.size() - 1; i++) {
                    detail += listTopic.get(i) + "/";
                }
                detail += listTopic.get(listTopic.size() - 1);
                detail += "\n";
            }
            if (listCoherence.size() > 0) {
                detail += "[coherence] ";
                for (int i = 0; i < listCoherence.size() - 1; i++) {
                    detail += listCoherence.get(i) + "/";
                }
                detail += listCoherence.get(listCoherence.size() - 1);
                detail += "\n";
            }
            dataCursor.moveToPosition(-1);
            while (dataCursor.moveToNext()) {
                String mimeType = dataCursor.getString(dataCursor
                        .getColumnIndex(WordDbHelper.TABLE_DATA.MIME_TYPE));
                if (mimeType.equals(WordDbHelper.MIME_TYPE.PROPERTY)) {
                    String key = dataCursor.getString(dataCursor
                            .getColumnIndex(WordDbHelper.TABLE_DATA.DATA3));
                    String value = dataCursor.getString(dataCursor
                            .getColumnIndex(WordDbHelper.TABLE_DATA.DATA4));
                    detail += "[" + key + "] " + value + "\n";
                }
            }
            dataCursor.moveToPosition(-1);
            while (dataCursor.moveToNext()) {
                String mimeType = dataCursor.getString(dataCursor
                        .getColumnIndex(WordDbHelper.TABLE_DATA.MIME_TYPE));
                if (mimeType.equals(WordDbHelper.MIME_TYPE.EXAMPLE)) {
                    String example = dataCursor.getString(dataCursor
                            .getColumnIndex(WordDbHelper.TABLE_DATA.DATA5));
                    detail += "- " + example + "\n";
                    detail += "\n";
                }
            }
        }

        if (detail.length() < 200) {
            mDetailInfomation.setTextSize(18);
        } else if (detail.length() < 500) {
            mDetailInfomation.setTextSize(16);
        } else {
            mDetailInfomation.setTextSize(14);
        }
        mDetailInfomation.setText(detail);
    }

    private void addCountToWord(boolean pass) {

        Cursor cursor = mRules.getCursor();
        int id = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
        Uri uri = ContentUris.withAppendedId(WordProvider.CONTENT_URI, id);
        ContentValues values = new ContentValues();

        boolean isCN = LANGUAGE_BUTTON_CN == mShiftLanguageButton.getStatus();
        if (isCN) {
            values.put(WordDbHelper.TABLE_WORD.CN_PASS, pass ? 1 : 0);
        } else {
            values.put(WordDbHelper.TABLE_WORD.EN_PASS, pass ? 1 : 0);
        }
        int ret = getContentResolver().update(uri, values, null, null);
        if (ret <= 0) {
            Toast.makeText(this, "update point failed!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.fail:
            addCountToWord(false);
            navigateNextWord();
            break;
        case R.id.pass:
            addCountToWord(true);
            navigateNextWord();
            break;
        case R.id.next:
            navigateNextWord();
            break;
        case R.id.see:
            toggleDetail();
            break;
        case R.id.edit:
            Cursor cursor = mRules.getCursor();
            int id = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
            Intent intent = new Intent();
            intent.setAction(MenuAction.INTENT_WORD_EDIT);
            intent.putExtra("id", id);
            startActivity(intent);
            break;
        case R.id.shiftLanguage:
            navigateShiftLanguage();
            break;
        case R.id.speakSwitch:
            toggleSpeak();
            break;
        case R.id.repeat:
            speakWordRepeat();
            break;
        default:
            break;
        }
    }

    private void toggleSpeak() {
        mRepeatButton.setVisibility(speakSwitchButton.getStatus() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.word_menu, menu);
        MenuAction.initSearchbar(this, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.back_a_word == item.getItemId()) {
            navigatePreviousWord();
        } else {
            MenuAction.onOptionsItemSelected(this, item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == WordLoader.LOAD_CUSTOMIZED) {
            mWordLoader.customizedLoader(this);
            return;
        }
        if (position == WordLoader.LOAD_SOURCES) {
            mWordLoader.browseSourcesLoader(this);
            return;
        }
        if (position == WordLoader.LOAD_ADDITIONAL) {
            if (mWordLoader.getAdditionalLoaderId() == WordLoader.LOAD_FROM_PROGRESS) {
                return;
            }
        }
        mWordLoader.setLoaderId(position);
        startAllNew();
    }

    @Override
    public void onCustomizedPickComplete() {
        updateSpinner(mWordLoader.getLoadArrayIncludeCustomized());
        startAllNew();
    }

    private void updateSpinner(String[] newArray) {
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                newArray);
        mSpinner.setAdapter(mArrayAdapter);
        mArrayAdapter.notifyDataSetChanged();
        mSpinner.setSelection(WordLoader.LOAD_ADDITIONAL);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onDestroy() {
        Cursor cursor = mRules.getCursor();
        if (cursor != null) {
            cursor.close();
        }
        super.onDestroy();
    }

    @Override
    public void onRoundFinish() {
        startAllNew();
    }

    private void closeCursor() {
        if (null == mRules) {
            return;
        }
        Cursor cursor = mRules.getCursor();
        if (cursor != null) {
            cursor.close();
        }
    }
}
