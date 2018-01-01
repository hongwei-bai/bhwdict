package bhwWords.input;

import java.util.ArrayList;

import com.bhw1899.bhwwords.R;

import bhwWords.dict.model.DictInstall;
import bhwWords.dict.model.DictInstall.ReportProgressListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DictInstallActivity extends Activity implements OnClickListener,
        ReportProgressListener {
    private Button installButton;
    private TextView textViewMsg;
    private ArrayList<String> listMsg;
    private DictInstall dictInstall;
    private static final int MSGID = 78712;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSGID) {
                textViewMsg.setText(getAllMsg());
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dict_install);
        // listTxtButton = (Button) findViewById(R.id.button_listtxt);
        installButton = (Button) findViewById(R.id.button_install);
        // verifyButton = (Button) findViewById(R.id.button_verify);
        // listView = (ListView) findViewById(R.id.listDb);
        textViewMsg = (TextView) findViewById(R.id.textViewMsg);
        installButton.setOnClickListener(this);
        listMsg = new ArrayList<>();
        dictInstall = new DictInstall(this);
        dictInstall.setReportProgressListener(this);
    }

    public class InstallDictThread implements Runnable {
        @Override
        public void run() {
            dictInstall.installDict();
        }
    }

    public void initDictInBackgroundThread() {
        new Thread(new InstallDictThread()).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button_install:
            initDictInBackgroundThread();
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
    public void onReportProgress(int progress, String msg) {
        addMsg(msg);
        handler.sendEmptyMessage(MSGID);
    }
}
