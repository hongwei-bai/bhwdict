package bhwWords.dict.txtparser;

import java.util.ArrayList;

public class WordData {
    public int _id;
    public ArrayList<String> originallist;
    public String english;
    public String reference;
    public String phonetic;
    public String chinese;
    public String file;
    public ArrayList<WordProperty> properties;

    public WordData() {
        this._id = -1;
        this.originallist = null;
        this.english = null;
        this.reference = null;
        this.phonetic = null;
        this.chinese = null;
        this.properties = null;
    }

    public WordData(WordData orginal) {
        this._id = orginal._id;
        this.originallist = orginal.originallist;
        this.english = orginal.english;
        this.reference = orginal.reference;
        this.phonetic = orginal.phonetic;
        this.chinese = orginal.chinese;
        this.properties = orginal.properties;
    }
}
