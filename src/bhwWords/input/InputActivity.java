package bhwWords.input;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import bhwWords.dict.constants.Constants;
import bhwWords.dict.model.SpeakWord;
import bhwWords.provider.WordDbHelper;
import bhwWords.provider.WordProvider;
import bhwWords.sourcepicker.view.SourcePickerModel;

import com.bhw1899.bhwwords.R;

public class InputActivity extends Activity implements OnClickListener {
    private EditText topicEditText;
    private Spinner topicSpinner;
    private EditText mEnglishTv;
    private EditText chineseText;
    private ImageButton speakButton;
    private Button addButton;
    private ListView listView;
    private InputListAdapter adapter;
    private InputWordSearchAdapter searchAdapter;
    public static final String NEW_TOPIC_LABEL = "<New>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);
        SpeakWord.getInstance().init(this);
        topicEditText = (EditText) findViewById(R.id.topic);
        topicEditText.setHint("New topic");
        topicSpinner = (Spinner) findViewById(R.id.topic_spinner);
        mEnglishTv = (EditText) findViewById(R.id.english);
        chineseText = (EditText) findViewById(R.id.chinese);
        addButton = (Button) findViewById(R.id.add);
        addButton.setOnClickListener(this);
        speakButton = (ImageButton) findViewById(R.id.speak);
        speakButton.setEnabled(false);
        speakButton.setOnClickListener(this);

        adapter = new InputListAdapter(this);
        searchAdapter = new InputWordSearchAdapter(this);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(searchAdapter);
        adapter.setData(null);
        registerSearchTextWatcher();
        registerSearchListOnClickListener();
        topicSpinner.setAdapter(buildTopicAdapter());
        registerTopicOperation();
        mEnglishTv.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void saveWord(String en, String cn, String phonetic, int rating,
            String topic, boolean isPhrase) {
        long date = new Date().getTime();
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        ContentValues values = new ContentValues();
        values.put(WordDbHelper.TABLE_WORD.ENGLISH, en);
        values.put(WordDbHelper.TABLE_WORD.CHINESE, cn);
        values.put(WordDbHelper.TABLE_WORD.PHONETIC, phonetic);
        values.put(WordDbHelper.TABLE_WORD.DATE_ADDED, date);
        values.put(WordDbHelper.TABLE_WORD.EN_PASS, 0);
        values.put(WordDbHelper.TABLE_WORD.CN_PASS, 0);
        values.put(WordDbHelper.TABLE_WORD.REVIEW_FLAG, 0);
        values.put(WordDbHelper.TABLE_WORD.IMPORTANCE, rating);
        values.put(WordDbHelper.TABLE_WORD.DIRECTION, isPhrase ? 1 : 2);
        if (topic != null) {
            values.put(WordDbHelper.TABLE_WORD.SOURCE, topicEditText.getText()
                    .toString());
        }
        getContentResolver().insert(WordProvider.CONTENT_URI, values);
        Toast.makeText(this, "new word saved.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.add:
            add();
            break;
        case R.id.speak:
            speak();
            break;
        default:
            break;
        }
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
        MenuAction.onOptionsItemSelected(this, item);
        return super.onOptionsItemSelected(item);
    }

    private void search(String word) {
        mEnglishTv.setText(word);
        listView.setAdapter(adapter);
        adapter.setData(word);
    }

    private void registerSearchTextWatcher() {
        mEnglishTv.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (!(listView.getAdapter() instanceof InputWordSearchAdapter)) {
                    listView.setAdapter(searchAdapter);
                }
                searchAdapter.updateWord(s.toString());
                speakButton.setEnabled(s.length() > 0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void registerSearchListOnClickListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (listView.getAdapter() instanceof InputWordSearchAdapter) {
                    String word = (String) searchAdapter.getItem(position);
                    search(word);
                } else {
                    int viewType = adapter.getItemViewType(position);
                    switch (viewType) {
                    case Constants.VIEW_TYPE_EXPLAIN:
                        adapter.toggleCheck(position);
                        break;
                    default:
                        break;
                    }
                }
            }
        });
    }

    public void add() {
        String english = mEnglishTv.getText().toString();
        int wordCount = english.split(" ").length;
        String chinese = adapter.getChinese();
        String chineseEx = chineseText.getText().toString().trim();
        if (chinese.isEmpty() && chineseEx.isEmpty()) {
            Toast.makeText(this, "choose at least 1 explaination!",
                    Toast.LENGTH_LONG).show();
            return;
        }
        String phonetic = adapter.getPhonetic();
        int rating = adapter.getRating();
        String combined = "";
        if (chineseEx.isEmpty()) {
            combined = chinese;
        } else if (chinese.isEmpty()) {
            combined = chineseEx;
        } else {
            combined += chineseEx + "\n" + chinese;
        }
        boolean isPhrase = wordCount > 1 && !chineseEx.isEmpty();

        String topicString = null;
        boolean needRebuildTopic = false;
        if (topicSpinner.getVisibility() == View.VISIBLE) {
            topicString = topicSpinner.getSelectedItem().toString();
        } else {
            topicString = topicEditText.getText().toString();
            if (topicString.isEmpty()) {
                topicString = null;
            } else {
                needRebuildTopic = true;
            }
        }
        topicSpinner.setVisibility(View.VISIBLE);
        topicEditText.setVisibility(View.GONE);
        saveWord(english, combined, phonetic, rating, topicString, isPhrase);
        mEnglishTv.getText().clear();
        chineseText.getText().clear();
        adapter.setData(null);
        adapter.notifyDataSetChanged();
        mEnglishTv.requestFocus();
        if (needRebuildTopic) {
            topicSpinner.setAdapter(buildTopicAdapter());
        }
    }

    public void speak() {
        String english = mEnglishTv.getText().toString();
        Log.d("aaaa", "english = " + english);
        SpeakWord.getInstance().speakEnglishNow(english);
    }

    private ArrayAdapter<String> buildTopicAdapter() {
        ArrayList<String> list = SourcePickerModel.getTopicList(this, null);

        if (list.isEmpty()) {
            list.add(NEW_TOPIC_LABEL);
        } else {
            list.add(1, NEW_TOPIC_LABEL);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        return arrayAdapter;
    }

    private void registerTopicOperation() {
        topicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                String string = ((TextView) view).getText().toString();
                if (NEW_TOPIC_LABEL.equals(string)) {
                    switchTopicToSpinner(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void switchTopicToSpinner(boolean toSpinner) {
        if (toSpinner) {
            topicSpinner.setVisibility(View.VISIBLE);
            topicEditText.setVisibility(View.GONE);
            mEnglishTv.requestFocus();
        } else {
            topicSpinner.setVisibility(View.GONE);
            topicEditText.setVisibility(View.VISIBLE);
            topicEditText.requestFocus();
        }
    }
}
