package bhwWords.test;

public class WordData {
    public static final int TEST_LANG_EN = 0;
    public static final int TEST_LANG_CN = 1;
    public int position;
    public boolean isCN;
    public long id;

    public WordData(int position, int testLanguage, long id) {
        this.position = position;
        this.isCN = testLanguage == TEST_LANG_CN;
        this.id = id;
    }
}
