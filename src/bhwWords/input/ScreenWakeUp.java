package bhwWords.input;

import bhwWords.dict.constants.Constants;
import android.app.Service;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class ScreenWakeUp {
    private static final String TAG = Constants.TAG;
    private PowerManager mPowerManager = null;
    private WakeLock mWakeLock = null;

    @SuppressWarnings("deprecation")
    public ScreenWakeUp(Context context) {
        mPowerManager = (PowerManager) context.getSystemService(Service.POWER_SERVICE);
        mWakeLock = this.mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Lock");
        mWakeLock.setReferenceCounted(false);
    }

    public void enableWakeLock() {
        Log.d(TAG, "+enableWakeLock");
        mWakeLock.acquire();
    }

    public void disableWakeLock() {
        Log.d(TAG, "-disableWakeLock");
        mWakeLock.release();
    }

}
