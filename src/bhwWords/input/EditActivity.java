package bhwWords.input;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import bhwWords.dict.model.SpeakWord;
import bhwWords.dict.model.SpeakWord.SpeakAllCompleteListener;
import bhwWords.provider.WordDbHelper;
import bhwWords.provider.WordProvider;
import bhwWords.test.StatusButton;
import bhwWords.test.WordLoader;
import bhwWords.test.WordLoader.OnCustomizedPickCompleteListener;

import com.bhw1899.bhwwords.R;

public class EditActivity extends Activity implements OnClickListener, OnItemSelectedListener,
        OnItemLongClickListener, OnMenuItemClickListener, OnItemClickListener,
        android.content.DialogInterface.OnClickListener, OnCustomizedPickCompleteListener {
    private Spinner mSpinner;
    private Button mShiftLanguageButton;
    private Button allocateButton;
    private StatusButton speakAllButton;
    private StatusButton pauseButton;
    private ArrayAdapter<String> mArrayAdapter;
    private boolean bLangEng = true;
    private ListView mListView;
    private WordAdapter mAdapter;
    private TextView mCountTv;
    private boolean bQuery = false;
    private String mQueryString;
    private WordLoader mWordLoader;
    private ScreenWakeUp screenWakeUp;
    private boolean bPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        SpeakWord.getInstance().init(this);
        screenWakeUp = new ScreenWakeUp(this);
        mShiftLanguageButton = (Button) findViewById(R.id.shiftLanguage);
        speakAllButton = (StatusButton) findViewById(R.id.speakAll);
        speakAllButton.setButtonString("停", "听");
        speakAllButton.setStatus(false);
        pauseButton = (StatusButton) findViewById(R.id.pause);
        pauseButton.setButtonString(">", "||");
        pauseButton.setStatus(false);
        allocateButton = (Button) findViewById(R.id.allocate);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mShiftLanguageButton.setOnClickListener(this);
        allocateButton.setOnClickListener(this);
        speakAllButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                WordLoader.LOAD_ARRAY);
        mSpinner.setAdapter(mArrayAdapter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bQuery = false;
                return false;
            }
        });
        mWordLoader = new WordLoader();
        mWordLoader.setOnCustomizedPickCompleteListener(this);
        mAdapter = new WordAdapter(this);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mCountTv = (TextView) findViewById(R.id.count);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);
        doSearchQuery(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        doSearchQuery(intent);
    }

    private void doSearchQuery(Intent intent) {
        if (intent == null) {
            return;
        }
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQueryString = intent.getStringExtra(SearchManager.QUERY);
            bQuery = true;
            reload();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bPlaying) {
            screenWakeUp.enableWakeLock();
        }
        reload();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("aaaa", "onPause disableWakeLock--");
        screenWakeUp.disableWakeLock();
    }

    private void reload() {
        String selection;
        if (bQuery) {
            selection = WordDbHelper.TABLE_WORD.ENGLISH + "  like '%" + mQueryString + "%'";
            selection += " or " + WordDbHelper.TABLE_WORD.CHINESE + "  like '%" + mQueryString
                    + "%'";
        } else {
            selection = mWordLoader.getSelection();
        }
        Cursor cursor = getContentResolver().query(WordProvider.CONTENT_URI, null, selection, null,
                null);
        mAdapter.setCursor(cursor);
        mCountTv.setText("(" + (cursor != null ? cursor.getCount() : 0) + ")");
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
        reload();
    }

    @Override
    public void onCustomizedPickComplete() {
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                mWordLoader.getLoadArrayIncludeCustomized());
        mSpinner.setAdapter(mArrayAdapter);
        mArrayAdapter.notifyDataSetChanged();
        mSpinner.setSelection(WordLoader.LOAD_IN_CUSTOMIZED);
        reload();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.shiftLanguage:
            bLangEng = !bLangEng;
            mShiftLanguageButton.setText(bLangEng ? "英" : "中");
            mAdapter.setLanguage(bLangEng);
            mAdapter.notifyDataSetChanged();
            break;
        case R.id.speakAll:
            if (speakAllButton.getStatus()) {
                bPlaying = true;
                screenWakeUp.enableWakeLock();
                showPauseButton();
                showAllocateButton();
                SpeakWord.getInstance().speakAll(mAdapter.getEnglishList(),
                        mAdapter.getExampleList(), 2, new SpeakAllCompleteListener() {

                            @Override
                            public void onComplete() {
                                Log.d("aaaa", "onComplete disableWakeLock--");
                                bPlaying = false;
                                screenWakeUp.disableWakeLock();
                                handler.sendEmptyMessage(MSGID_REFRESH_SPEAKALL_BUTTON);
                            }
                        });
            } else {
                SpeakWord.getInstance().stopSpeakAllOnce();
            }
            break;
        case R.id.pause:
            if (pauseButton.getStatus()) {
                SpeakWord.getInstance().suspendSpeakOnce();
            } else {
                boolean result = SpeakWord.getInstance().resumeSpeakOnce();
                if (!result) {
                    pauseButton.setStatus(false);
                    Toast.makeText(this, "Cannot resume, no session on going.", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
            break;
        case R.id.allocate:
            int idx = SpeakWord.getInstance().getSpeakAllCurrentIdx();
            if (idx >= 0) {
                mListView.smoothScrollToPosition(idx);
            }
            break;
        default:
            break;
        }
    }

    private void showPauseButton() {
        pauseButton.setVisibility(View.VISIBLE);
    }

    private void hidePauseButton() {
        pauseButton.setVisibility(View.GONE);
    }

    private void showAllocateButton() {
        allocateButton.setVisibility(View.VISIBLE);
    }

    private void hideAllocateButton() {
        allocateButton.setVisibility(View.GONE);
    }

    private static final int MSGID_REFRESH_SPEAKALL_BUTTON = 0x892;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (MSGID_REFRESH_SPEAKALL_BUTTON == msg.what) {
                speakAllButton.setStatus(false);
                hidePauseButton();
                hideAllocateButton();
            }
        };
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.word_menu, menu);
        MenuAction.initSearchbar(this, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuAction.onOptionsItemSelected(this, item);
        return super.onOptionsItemSelected(item);
    }

    private int mSelectedId = -1;

    // private int mSelectedPosition = -1;

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.item_longclick_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
        mSelectedId = (int) id;
        // mSelectedPosition = position;
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setAction(MenuAction.INTENT_WORD_EDIT);
        intent.putExtra("id", (int) id);
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.test:
            // Intent intentTest = new Intent();
            // intentTest.setAction(MenuAction.INTENT_TEST);
            // intentTest.putExtra("position", mSelectedPosition);
            // intentTest.putExtra("loaderid", m);
            // startActivity(intentTest);
            // mSelectedId = -1;
            // mSelectedPosition = -1;
            break;
        case R.id.edit:
            Intent intent = new Intent();
            intent.setAction(MenuAction.INTENT_WORD_EDIT);
            intent.putExtra("id", mSelectedId);
            startActivity(intent);
            mSelectedId = -1;
            // mSelectedPosition = -1;
            break;
        case R.id.delete:
            new AlertDialog.Builder(this).setTitle("Delete this word?")
                    .setNegativeButton("Cancel", this).setPositiveButton("Delete", this).show();
            break;
        default:
            break;
        }
        return false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog instanceof AlertDialog) {
            switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                Uri uri = ContentUris.withAppendedId(WordProvider.CONTENT_URI, mSelectedId);
                int ret = getContentResolver().delete(uri, null, null);
                Uri dataUri = ContentUris.withAppendedId(WordProvider.DATA_URI, mSelectedId);
                ret += getContentResolver().delete(dataUri, null, null);
                if (ret > 0) {
                    Toast.makeText(this, "delete finished, " + ret + " records deleted.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "delete failed!", Toast.LENGTH_LONG).show();
                }
                reload();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                break;
            default:
                break;
            }
            mSelectedId = -1;
            // mSelectedPosition = -1;
        }
    }
}
