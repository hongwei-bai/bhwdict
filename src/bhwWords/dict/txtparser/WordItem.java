package bhwWords.dict.txtparser;

import java.util.ArrayList;

public class WordItem {
    public int _id;
    public String original;
    public String explaination;
    public String explainationEng;
    public String explainationChn;
    public int pid;
    public ArrayList<String> examples;

    public WordItem() {
        _id = -1;
        original = null;
        explaination = null;
        examples = null;
        pid = -1;
    }

    public WordItem(String org) {
        _id = -1;
        original = org;
        explaination = null;
        examples = null;
        pid = -1;
    }
}
