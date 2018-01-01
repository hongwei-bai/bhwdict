package bhwWords.dict.constants;

import java.io.File;

public interface Constants {
    public interface Debug {
        public boolean DB_DEBUG = true;
    }

    public interface APP {
        public final static String EXE_VERSION = "0.1";
        public final static String PLATFORM = "Android";
        public final static String TITLE = MultiLanguageInterface.EXE_TITLE_BASE + " v"
                + EXE_VERSION + "(" + PLATFORM + ")";
    }

    public final static String TAG = "bhwword";
    public final static int SEARCH_MAX_LIMIT = 10;

    public static final int VIEW_TYPE_RATING = 0;
    public static final int VIEW_TYPE_PHONTIC = 1;
    public static final int VIEW_TYPE_EXPLAIN = 2;
    public static final int VIEW_TYPE_COUNT = VIEW_TYPE_EXPLAIN + 1;

    public static class PHONE_DIR {
        public static final String USER_FOLDER;
        public static final String USER_PATH1 = "storage/emulated/0";
        public static final String USER_PATH2 = "mnt/sdcard";
        public static final String FOLDER = "bhwword";
        static {
            File dir1 = new File(USER_PATH1, FOLDER);
            File dir2 = new File(USER_PATH2, FOLDER);
            if (dir1 != null && dir1.exists() && dir1.isDirectory()) {
                USER_FOLDER = USER_PATH1 + File.separator + FOLDER;
            } else if (dir2 != null && dir2.exists() && dir2.isDirectory()) {
                USER_FOLDER = USER_PATH2 + File.separator + FOLDER;
            } else {
                USER_FOLDER = USER_PATH1 + File.separator + FOLDER;
            }
        }
        public static final String IMPORT_FILE = "import.txt";
        public static final String EXPORT_FILE = "export.txt";
    }

    public interface DB {
        public final static String DICT_DIR = "dict";
        public final static String DICT_PATH = PHONE_DIR.USER_FOLDER + File.separator + DICT_DIR;
        public final static String DICT_TXT_EXTENSION = "txt";
        public final static String DICT_TXT_ENCODE = "GBK";

        public final static String DB_URL_ROOT = "127.0.0.1";
        public final static String DB_PORT_NO = "3306";
        public final static String DB_NAME = "dict01";
        public final static String DB_USER = "user";
        public final static String DB_PASSWORD = "Td123456";

        public static final String NULL = "null";
        public static final String VOICE = "[Voice]";
    }

    public interface KEYCODE {
        public static final int ENTER = 10;
        public static final int ESC = 27;
        public static final int CTRL = 17;

        public static final int HOME = 35;
        public static final int END = 36;
        public static final int LEFT = 37;
        public static final int DOWN = 38;
        public static final int RIGHT = 39;
        public static final int UP = 40;

        public static abstract interface MOUSE {
            public static final int LEFT = 1;
            public static final int MIDDLE = 2;
            public static final int RIGHT = 3;
        }
    }
}
