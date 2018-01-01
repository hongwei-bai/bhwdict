package bhwWords.dict.constants;

public interface MultiLanguageInterface {
    public final static String EXE_TITLE_BASE = "Muskrat dict";

    public final static String SEARCH = "Search";

    public final static String ADD = "Add";

    public final static String PHONETIC = "Phonetic ";
    public final static String CHINESE = "Chinese  ";
    public final static String EXAMPLES = "Example ";
    public final static String TOPIC = "Topic       ";

    public final static String RESERVED = "--Reserved--";

    public interface MENU {
        public final static String FILE = "      File      ";
        public final static String EXIT = " Exit           ";

        public final static String VIEW = "      View      ";
        public final static String EXAMPLE = "Show examples   ";
        public final static String CHINESE = "Show only chinese";
        public final static String STUDY = "Study Mode      ";

        public final static String SYSTEM = "     System     ";
        public final static String INSTALL = "Install Dict    ";
        public final static String UNINSTALL = "Uninstall Dict  ";

        public final static String HELP = "      Help      ";
        public final static String HELP_TOPIC = " Help Topic     ";
        public final static String ABOUT = " About          ";
    }

    public interface MSG {
        public final static String ADD_NO_ENGLISH = "Add failed! no english word.";
        public final static String ADD_NO_CHINESE = "Add failed! no chinese.";
        public final static String ADD_SUCC = "Word added: ";
    }

}
