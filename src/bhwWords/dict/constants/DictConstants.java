package bhwWords.dict.constants;

public interface DictConstants {
    public final static String NOTE_ON_USAGE = "NOTE ON USAGE 用法:";
    public final static String prefix = "pref 前缀";
    public final static String abbrevation = "abbr 缩写";
    public final static String symbol = "symb 符号";
    public final static String IDIOM = "(idm 习语)";

    public final static String SEPARATOR_EXPLAINATION = ": ";
    public final static String SEPARATOR_EXAMPLE = "\\s\\*\\s";

    // public final static String EXPLAINATION_SKIP_CHINESE_WORDS[] = { "口)",
    // "习语)", "古或修辞)",
    // "文, 尤用於法律)", "文)", "用以加强语气)", "与名词结合构成形容词)", "与名词, 形容词, 副词结合)" };
    public final static String EXPLAINATION_SKIP_PATTERN = ") ";

    public interface RegexConstants {
        public interface DEFINE {
            public String UNIT = "[\\w,;:\\-\\s`@\\?\\^\\[\\\\-强读式]";
            public String PHONETIC_UINT = "/" + UNIT + "*" + "(\\(r\\))*" + UNIT + "*?/";
        }

        public String PRONUNCIATION_REGEX = DEFINE.PHONETIC_UINT;
        public String PRONUNCIATION_REGEX_TWINS = DEFINE.PHONETIC_UINT + "*" + "/\\s*\\([\\s\\w]*"
                + DEFINE.PHONETIC_UINT + "*" + "/\\)";

        public String NUMBLE_TILE_APATTERN = "[0-9]+\\)";
    }
}
