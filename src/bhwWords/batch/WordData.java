package bhwWords.batch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.support.v4.util.ArrayMap;

public class WordData {
    public int id;
    public String english;
    public String chinese;
    public String phonetic;
    public String importance;
    public String direction;
    public String source;
    public String dateAdded;
    public ArrayMap<String, String> listProperty;
    public ArrayList<String> listExample;
    public ArrayList<String> listTopic;
    public ArrayList<String> listCoherence;
    public boolean newWord = true;

    public static final String keyId = "id";
    public static final String keyEnglish = "eng";
    public static final String keyChinese = "chn";
    public static final String keyPhonetic = "phonetic";
    public static final String keyImportance = "importance";
    public static final String keyDirection = "testChn";
    public static final String keyProperty = "property";
    public static final String keyDateAdded = "date";
    public static final String keyExample = "example";
    public static final String keyTopic = "topic";
    public static final String keyCoherence = "coherence";
    public static final String keySource = "source";

    public boolean parse(String bufferin) {
        boolean atleast = false;
        String lines[] = bufferin.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String pair[] = lines[i].split("=");
            if (pair.length >= 2) {
                switch (pair[0]) {
                case keyId:
                    id = Integer.valueOf(pair[1]);
                    newWord = false;
                    break;
                case keyEnglish:
                    english = pair[1];
                    atleast = true;
                    break;
                case keyChinese:
                    String tmp = pair[1];
                    for (int j = i + 1; j < lines.length; j++) {
                        if (isWrappedLine(lines[j])) {
                            tmp += "\n" + lines[j];
                        } else {
                            break;
                        }
                    }
                    chinese = tmp;
                    atleast = true;
                    break;
                case keyPhonetic:
                    phonetic = pair[1];
                    atleast = true;
                    break;
                case keyImportance:
                    importance = pair[1];
                    atleast = true;
                    break;
                case keyDirection:
                    direction = pair[1];
                    atleast = true;
                    break;
                case keySource:
                    source = pair[1];
                    atleast = true;
                    break;
                case keyDateAdded:
                    dateAdded = pair[1];
                    atleast = true;
                    break;
                case keyExample:
                    if (listExample == null) {
                        listExample = new ArrayList<>();
                    }
                    listExample.add(pair[1]);
                    atleast = true;
                    break;
                case keyTopic:
                    if (listTopic == null) {
                        listTopic = new ArrayList<>();
                    }
                    listTopic.add(pair[1]);
                    atleast = true;
                    break;
                case keyCoherence:
                    if (listCoherence == null) {
                        listCoherence = new ArrayList<>();
                    }
                    listCoherence.add(pair[1]);
                    atleast = true;
                    break;
                default:
                    break;
                }
            } else if (pair.length == 3) {
                switch (pair[0]) {
                case keyProperty:
                    if (listProperty == null) {
                        listProperty = new ArrayMap<>();
                    }
                    listProperty.put(pair[1], pair[2]);
                    atleast = true;
                    break;
                default:
                    break;
                }

            }
        }
        return atleast;
    }

    private boolean isWrappedLine(String line) {
        if (line.trim().isEmpty()) {
            return false;
        }
        String pair[] = line.split("=");
        if (pair.length == 2) {
            switch (pair[0]) {
            case keyId:
            case keyEnglish:
            case keyPhonetic:
            case keyImportance:
            case keyDirection:
            case keySource:
            case keyDateAdded:
            case keyExample:
            case keyTopic:
            case keyCoherence:
            case keyProperty:
                return false;
            default:
            }
        }
        return true;
    }

    public String toBuffer() {
        String buffer = "";
        buffer += keyId + "=" + id + "\n";
        if (english != null) {
            buffer += keyEnglish + "=" + english + "\n";
        }
        if (chinese != null) {
            buffer += keyChinese + "=" + chinese + "\n";
        }
        if (phonetic != null) {
            buffer += keyPhonetic + "=" + phonetic + "\n";
        }
        if (importance != null) {
            buffer += keyImportance + "=" + importance + "\n";
        }
        if (direction != null) {
            buffer += keyDirection + "=" + direction + "\n";
        }
        if (source != null) {
            buffer += keySource + "=" + source + "\n";
        }
        if (dateAdded != null) {
            buffer += keyDateAdded + "=" + dateAdded + "\n";
        }
        if (listProperty != null) {
            Iterator<Entry<String, String>> it = listProperty.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                buffer += keyProperty + "=" + key + "=" + value + "\n";
            }
        }
        if (listExample != null) {
            for (String string : listExample) {
                buffer += keyExample + "=" + string + "\n";
            }
        }
        if (listTopic != null) {
            for (String string : listTopic) {
                buffer += keyTopic + "=" + string + "\n";
            }
        }
        if (listCoherence != null) {
            for (String string : listCoherence) {
                buffer += keyCoherence + "=" + string + "\n";
            }
        }
        buffer += "\n";
        return buffer;
    }
}
