package bhwWords.dbadapter;

public interface DbAdaTable {
    public interface TABLE_WORD {
        public final String TABLE = "tbl_word";
        public final String _ID = "_id";
        public final String ENGLISH = "english";
        public final String FILE = "file";
        public final String CREATER[] = { ENGLISH + " varchar(200) not null",
                FILE + " varchar(100) not null" };
    }

    public interface TABLE_PROPERTY {
        public final String TABLE = "tbl_property";
        public final String _ID = "_id";
        public final String PROPERTY = "property";
        public final String PRONUNCIATION_RAW = "pronunciation_raw";
        public final String PRONUNCIATION = "pronunciation";
        public final String CONTENT = "content";
        public final String PARENT_ID = "pid";
        public final String CREATER[] = { PROPERTY + " varchar(200)",
                PRONUNCIATION_RAW + " varchar(200)", PRONUNCIATION + " varchar(200)",
                CONTENT + " varchar(400)", PARENT_ID + " int not null" };
    }

    public interface TABLE_ITEM {
        public final String TABLE = "tbl_item";
        public final String _ID = "_id";
        public final String ITEM = "item";
        public final String LEVELNO = "levelno";
        public final String SEQUENCE = "sequence";
        public final String CONTAIN_INFORMATION = "info";
        public final String EXPLAINATION = "explaination";
        public final String EXPLAINATION_ENG = "explaination_eng";
        public final String EXPLAINATION_CHN = "explaination_chn";
        public final String PARENT_ID = "pid";
        public final String CREATER[] = { ITEM + " varchar(100)", LEVELNO + " int",
                SEQUENCE + " int", CONTAIN_INFORMATION + " int not null",
                EXPLAINATION + " varchar(2000)", EXPLAINATION_ENG + " varchar(2000)",
                EXPLAINATION_CHN + " varchar(2000)", PARENT_ID + " int not null" };
    }

    public interface TABLE_EXAMPLE {
        public final String TABLE = "tbl_example";
        public final String _ID = "_id";
        public final String EXAMPLE = "example";
        public final String PARENT_ID = "pid";
        public final String CREATER[] = { EXAMPLE + " varchar(600) not null",
                PARENT_ID + " int not null" };
    }

    public interface TABLE_IDIOM {
        public final String TABLE = "tbl_idiom";
        public final String _ID = "_id";
        public final String ORIGINAL = "original";
        public final String PARENT_ID = "pid";
        public final String CREATER[] = { ORIGINAL + " varchar(6000) not null",
                PARENT_ID + " int not null" };
    }

    public interface INDEX {
        public final IndexStru ARRAY[] = { new IndexStru("", TABLE_WORD.TABLE, TABLE_WORD.ENGLISH),
                new IndexStru("", TABLE_PROPERTY.TABLE, TABLE_PROPERTY.PARENT_ID),
                new IndexStru("", TABLE_ITEM.TABLE, TABLE_ITEM.PARENT_ID),
                new IndexStru("", TABLE_EXAMPLE.TABLE, TABLE_EXAMPLE.PARENT_ID), };
    }

    public class IndexStru {
        public String type;
        public String table;
        public String field;

        public IndexStru(String tp, String tbl, String f) {
            type = tp;
            table = tbl;
            field = f;
        }
    }
}
