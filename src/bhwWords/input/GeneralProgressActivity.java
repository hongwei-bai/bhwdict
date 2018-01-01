package bhwWords.input;

import java.util.ArrayList;

import bhwWords.batch.WordImport;
import bhwWords.batch.WordImport.ReportProgress;
import com.bhw1899.bhwwords.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GeneralProgressActivity extends Activity implements OnClickListener, ReportProgress {
    private Button importButton;
    private Button importAllButton;
    private Button clearButton;
    private TextView textViewMsg;
    private ArrayList<String> listMsg;
    private static final int MSGID = 2446;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSGID) {
                textViewMsg.setText(getAllMsg());
            }
        };
    };
    private WordImport wordImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_progress);
        importButton = (Button) findViewById(R.id.button_import);
        importAllButton = (Button) findViewById(R.id.button_importall);
        clearButton = (Button) findViewById(R.id.button_clear);
        importButton.setOnClickListener(this);
        importAllButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        textViewMsg = (TextView) findViewById(R.id.textViewMsg);
        listMsg = new ArrayList<>();
        wordImport = new WordImport(this);
        wordImport.setReportProgressListener(this);
    }

    public class ImportAllThread implements Runnable {
        @Override
        public void run() {
            wordImport.importAll();
        }
    }

    public void importAllInBackgroundThread() {
        new Thread(new ImportAllThread()).start();
    }

    public class ImportThread implements Runnable {
        @Override
        public void run() {
            wordImport.importAdd(false);
        }
    }

    public void importInBackgroundThread() {
        new Thread(new ImportThread()).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button_import:
            importInBackgroundThread();
            break;
        case R.id.button_importall:
            importAllInBackgroundThread();
            break;
        case R.id.button_clear:
            wordImport.clearWords(this);
            break;
        default:
            break;
        }
    }

    private void addMsg(String msg) {
        synchronized (this) {
            if (listMsg.size() > 10) {
                listMsg.remove(0);
            }
            listMsg.add(msg);
        }
    }

    private String getAllMsg() {
        synchronized (this) {
            String msg = "";
            for (String string : listMsg) {
                msg += string + "\n";
            }
            return msg;
        }
    }

    @Override
    public void onReport(String msg) {
        addMsg(msg);
        handler.sendEmptyMessage(MSGID);
    }
}
