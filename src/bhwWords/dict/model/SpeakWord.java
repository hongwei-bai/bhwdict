package bhwWords.dict.model;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

public class SpeakWord {
    private TextToSpeech mTTSEnglish;
    private TextToSpeech mTTSChinese;
    private Context mContext;
    private String mWord;
    private boolean mOn = false;
    private boolean mHasChineseEngine = false;

    private static volatile SpeakWord mInstance = null;

    private SpeakWord() {
    }

    public static SpeakWord getInstance() {
        if (null == mInstance) {
            synchronized (SpeakWord.class) {
                if (null == mInstance) {
                    mInstance = new SpeakWord();
                }
            }
        }
        return mInstance;
    }

    // private static final String ENGINE_GOOGLE = "com.google.android.tts";
    private static final String ENGINE_IFLYTEK = "com.iflytek.tts";

    // public static final String DEFAULT_ENGLISH_ENGINE = ENGINE_GOOGLE;
    public static final String DEFAULT_CHINESE_ENGINE = ENGINE_IFLYTEK;

    public static final float CHINESE_SPEECH_RATE = 1.2f;

    public void init(Context contextIn) {
        init(contextIn, false);
    }

    public void init(Context contextIn, boolean onlyStartEnglishEngine) {
        mContext = contextIn;
        if (mTTSEnglish != null) {
            return;
        }
        mTTSEnglish = new TextToSpeech(mContext, new OnInitListener() {

            @Override
            public void onInit(int status) {
                if (TextToSpeech.SUCCESS != status) {
                    Toast.makeText(mContext, "TTS English init failed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int result = mTTSEnglish.setLanguage(Locale.UK);
                if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                        && result != TextToSpeech.LANG_AVAILABLE) {
                    Toast.makeText(mContext, "Language:UK NOT support!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mOn = true;
            }
        });// , DEFAULT_ENGLISH_ENGINE);
        if (onlyStartEnglishEngine) {
            return;
        }
        mTTSChinese = new TextToSpeech(mContext, new OnInitListener() {

            @Override
            public void onInit(int status) {
                if (TextToSpeech.SUCCESS != status) {
                    Toast.makeText(mContext, "TTS Chinese init failed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int result = mTTSEnglish.setLanguage(Locale.UK);
                if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                        && result != TextToSpeech.LANG_AVAILABLE) {
                    Toast.makeText(mContext, "Language:UK NOT support!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mOn = true;
                mHasChineseEngine = true;
            }
        }, DEFAULT_CHINESE_ENGINE);
        mTTSChinese.setSpeechRate(CHINESE_SPEECH_RATE);
    }

    @SuppressWarnings("deprecation")
    public void speakEnglishNow(String englishOnly) {
        if (mOn) {
            mWord = englishOnly;
            mTTSEnglish.speak(mWord, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @SuppressWarnings("deprecation")
    public void speakEnglish(String englishOnly) {
        if (mOn) {
            mWord = englishOnly;
            mTTSEnglish.speak(mWord, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @SuppressWarnings("deprecation")
    public void speakChineseNow(String string) {
        if (mOn && mHasChineseEngine) {
            mWord = string;
            mTTSChinese.speak(mWord, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @SuppressWarnings("deprecation")
    public void speakChinese(String string) {
        if (mOn && mHasChineseEngine) {
            mWord = string;
            mTTSChinese.speak(mWord, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public int stopSpeaking() {
        return mTTSEnglish.stop();
    }

    public void shutdown() {
        mTTSEnglish.shutdown();
        if (mHasChineseEngine) {
            mTTSChinese.shutdown();
        }
        if (speakOnceThread != null) {
            speakOnceThread.interrupt();
        }
        mOn = false;
    }

    private ArrayList<String> speakOnceList = new ArrayList<>();
    private ArrayList<String> speakOnceListB;
    private int speakRepeatTimes = 1;
    private Thread speakOnceThread;
    private SpeakAllCompleteListener listener;
    private static final int _1_SECOND = 1000;
    private static final int _2_SECOND = 2000;
    private static final int _4_SECOND = 4000;
    private int currentWordIdx = -1;
    private ArrayList<String> savedWordlist;
    private ArrayList<String> savedExampleList;

    private void resetCurrentWordIdx() {
        currentWordIdx = -1;
    }

    private void resetSpeakAllSession() {
        savedWordlist = null;
        savedExampleList = null;
        resetCurrentWordIdx();
    }

    private void setCurrentWordIdx(int idx) {
        currentWordIdx = idx;
    }

    public interface SpeakAllCompleteListener {
        public void onComplete();
    }

    public void speakAll(ArrayList<String> wordlist, ArrayList<String> exampleList,
            int repeatTimes, SpeakAllCompleteListener l) {
        speakRepeatTimes = repeatTimes;
        savedWordlist = wordlist;
        savedExampleList = exampleList;
        listener = l;
        synchronized (speakOnceList) {
            speakOnceList = wordlist;
            speakOnceListB = exampleList;
            if (mOn) {
                speakOnceThread = new Thread(new Runnable() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void run() {
                        try {
                            if (currentWordIdx < 0) {
                                currentWordIdx = 0;
                                String introduction = "There are " + speakOnceList.size()
                                        + " words for this time's study, now start";
                                mTTSEnglish.speak(introduction, TextToSpeech.QUEUE_ADD, null);
                                Thread.sleep(_2_SECOND);
                            }

                            // main body
                            for (int i = Math.max(0, currentWordIdx); i < speakOnceList.size(); i++) {
                                setCurrentWordIdx(i);
                                String mWord = speakOnceList.get(i);
                                String exampleString = speakOnceListB.get(i);
                                String examples[] = null;
                                if (exampleString != null && !exampleString.isEmpty()) {
                                    examples = exampleString.split("\n");
                                }

                                // speak 1st time
                                Thread.sleep(_4_SECOND);
                                mTTSEnglish.speak(mWord, TextToSpeech.QUEUE_ADD, null);
                                Thread.sleep(estimateSpeakingDelay(mWord));

                                if (examples != null) {
                                    for (int k = 0; k < examples.length; k++) {
                                        Thread.sleep(_2_SECOND);
                                        mTTSEnglish
                                                .speak(examples[k], TextToSpeech.QUEUE_ADD, null);
                                        Thread.sleep(estimateSpeakingDelay(examples[k]));
                                    }
                                }

                                // speak 2-n times
                                for (int j = 1; j < speakRepeatTimes; j++) {
                                    Thread.sleep(_2_SECOND);
                                    mTTSEnglish.speak(mWord, TextToSpeech.QUEUE_ADD, null);
                                    Thread.sleep(estimateSpeakingDelay(mWord));

                                    if (examples != null) {
                                        for (int k = 0; k < examples.length; k++) {
                                            Thread.sleep(_2_SECOND);
                                            mTTSEnglish.speak(examples[k], TextToSpeech.QUEUE_ADD,
                                                    null);
                                            Thread.sleep(estimateSpeakingDelay(examples[k]));
                                        }
                                    }
                                }
                            }

                            // end
                            Thread.sleep(_2_SECOND);
                            String ending = "Study complete!";
                            mTTSEnglish.speak(ending, TextToSpeech.QUEUE_ADD, null);
                            Thread.sleep(_2_SECOND);
                            resetSpeakAllSession();
                            savedWordlist = null;
                            savedExampleList = null;
                            if (listener != null) {
                                listener.onComplete();
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                });
                speakOnceThread.start();
            }
        }
    }

    private int estimateSpeakingDelay(String content) {
        final int length = content.length();
        int unit = length / 40;
        boolean halfUnit = (length % 40) >= 20;
        int extraDelay = _2_SECOND * unit + (halfUnit ? _1_SECOND : 0);
        return extraDelay;
    }

    public void suspendSpeakOnce() {
        stopSpeaking();
        if (speakOnceThread != null) {
            speakOnceThread.interrupt();
        }
    }

    public boolean resumeSpeakOnce() {
        if (currentWordIdx < 0) {
            return false;
        }
        speakAll(savedWordlist, savedExampleList, speakRepeatTimes, listener);
        return true;
    }

    public void stopSpeakAllOnce() {
        suspendSpeakOnce();
        resetSpeakAllSession();
        if (listener != null) {
            listener.onComplete();
        }
    }

    public int getSpeakAllCurrentIdx() {
        return currentWordIdx;
    }

    @Override
    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }
}
