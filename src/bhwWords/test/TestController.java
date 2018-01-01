package bhwWords.test;

public class TestController {
    private int mCounter;
    private int mRoundCount = 0;
    private RoundFinishListener mOnRoundFinishListener;

    public TestController() {
        mCounter = 0;
    }

    public interface RoundFinishListener {
        public void onRoundFinish();
    }

    public void setOnRoundFinishListener(RoundFinishListener l) {
        mOnRoundFinishListener = l;
    }

    public boolean goToNextWord() {
        mCounter++;
        if (mCounter >= mRoundCount) {
            resetCounter();
            mOnRoundFinishListener.onRoundFinish();
            return false;
        }
        return true;
    }

    public void goToPreviousWord() {
        mCounter--;
        if (mCounter < 0) {
            mCounter = mRoundCount - 1;
        }
    }

    public void setRoundCount(int roundCount) {
        mRoundCount = roundCount;
    }

    public int getCurrentCount() {
        return mCounter;
    }

    public void resetCounter() {
        mCounter = 0;
    }

    public void setCounter(int counter) {
        mCounter = counter;
    }
}
