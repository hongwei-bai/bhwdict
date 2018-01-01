package bhwWords.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class IrrevertableButton extends Button implements OnClickListener {
    private OnClickListener mOnClickListener;
    private boolean mStatus = false;
    private String mStatusTrueString;
    private String mStatusFalseString;

    public IrrevertableButton(Context context) {
        super(context);
    }

    public IrrevertableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IrrevertableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IrrevertableButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setButtonString(String trueString, String falseString) {
        mStatusTrueString = trueString;
        mStatusFalseString = falseString;
    }

    public boolean getStatus() {
        return mStatus;
    }

    public void setStatus(boolean status) {
        mStatus = status;
        updateButtonString();
    }

    private void updateButtonString() {
        if (mStatus) {
            setText(mStatusTrueString);
        } else {
            setText(mStatusFalseString);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
        super.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(v);
        }
        if (!mStatus) {
            mStatus = true;
            updateButtonString();
        }
    }
}
