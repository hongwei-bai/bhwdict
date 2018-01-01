package bhwWords.dict.txtparser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class PhoneticParser {
    private static HashMap<String, String> pronunciationMap;

    static {
        pronunciationMap = new HashMap<>();
        pronunciationMap.put("U", "ʊ");
        pronunciationMap.put("V", "ʒ");
        pronunciationMap.put("A", "æ");
        pronunciationMap.put("E", "ə");
        pronunciationMap.put("I", "ɪ");
        pronunciationMap.put("R", "ɔ");
        pronunciationMap.put("F", "ʃ");
        pronunciationMap.put("N", "ŋ");
        pronunciationMap.put("B", "ɑ");
        pronunciationMap.put("Q", "ʌ");
        pronunciationMap.put("C", "ɔ");// ɒ
        pronunciationMap.put("W", "θ");
        pronunciationMap.put("\\", "ɜ");
        pronunciationMap.put("5", "ˈ");
        pronunciationMap.put("9", "ˌ");
		pronunciationMap.put("T", "ð");

        // American
        pronunciationMap.put("o", "oʊ");
        pronunciationMap.put("J", "ʊ");
        pronunciationMap.put("L", "ər");
        pronunciationMap.put("^", "ɡ");
        pronunciationMap.put("Z", "ɛ");
        pronunciationMap.put("[", "ɜr");
        
    }

    public PhoneticParser() {
        // TODO Auto-generated constructor stub
    }

    public static String parse(String txtCode) {
        if (null == txtCode) {
            return null;
        }
        String result = new String(txtCode);
        Iterator<Entry<String, String>> iterator = pronunciationMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            result = result.replace(key, value);
        }
        return result;
    }
}
