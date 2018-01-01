package bhwWords.dict.model;

public class DisplayItem {
    public int viewType;
    public String string;
    public boolean selected = false;

    public DisplayItem(int type) {
        viewType = type;
    }

    public DisplayItem(int type, String s) {
        viewType = type;
        string = s;
    }

    public DisplayItem(int type, boolean sel) {
        viewType = type;
        selected = sel;
    }

    public String getString() {
        return string;
    }

    public int getViewType() {
        return viewType;
    }
}
