package bhwWords.dict.txtparser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bhwWords.dict.constants.Constants;
import bhwWords.dict.constants.DictConstants;

import android.util.Log;

public class OxfordEnglish2ChineseDictTxtParser extends DictTxtParser implements
        DictConstants.RegexConstants, DictConstants {
    private static final String TAG = Constants.TAG;

    private ArrayList<String> mSupportProperties;
    private ArrayList<String> mSupportPropertiesBegin;
    private ArrayList<String> mSupportPropertiesExt;
    private ArrayList<String> mSupportPropertiesRepeat;
    private ArrayList<String> mSupportNumbers;
    private ArrayList<String> mSupportAlphabetas;

    public OxfordEnglish2ChineseDictTxtParser() {
        initProperty();
        initNumbers();
        initAlphabetas();
    }

    private void initProperty() {
        ArrayList<String> basiclist = new ArrayList<>();
        basiclist = new ArrayList<>();
        basiclist.add(WordProperty.noun);
        basiclist.add(WordProperty.pronoun);
        basiclist.add(WordProperty.adjective);
        basiclist.add(WordProperty.adverb);
        basiclist.add(WordProperty.verb);
        basiclist.add(WordProperty.numeral);
        basiclist.add(WordProperty.article);
        basiclist.add(WordProperty.indefinite_article);
        basiclist.add(WordProperty.preposition);
        basiclist.add(WordProperty.conjunction);
        basiclist.add(WordProperty.interjection);

        ArrayList<String> useagelist = new ArrayList<>();
        useagelist.add(NOTE_ON_USAGE);
        useagelist.add(prefix);
        useagelist.add(abbrevation);
        useagelist.add(symbol);

        mSupportPropertiesBegin = new ArrayList<>();
        mSupportProperties = new ArrayList<>();
        mSupportPropertiesRepeat = new ArrayList<>();
        for (String b : basiclist) {
            mSupportPropertiesBegin.add(b);
            mSupportProperties.add(" " + b);
            mSupportPropertiesRepeat.add(b + ".");
        }
        for (String u : useagelist) {
            mSupportPropertiesBegin.add(u);
            mSupportProperties.add(" " + u);
        }

        mSupportPropertiesExt = new ArrayList<>();
        mSupportPropertiesExt.add(" ");
        mSupportPropertiesExt.add("[");
        mSupportPropertiesExt.add(":");
    }

    private void initNumbers() {
        mSupportNumbers = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            mSupportNumbers.add(" " + i + " ");
        }
    }

    private void initAlphabetas() {
        mSupportAlphabetas = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) {
            char cArray[] = new char[1];
            cArray[0] = c;
            String cString = new String(cArray);
            mSupportAlphabetas.add("(" + cString + ")");
        }
    }

    @Override
    protected ArrayList<WordData> parseFile(String filename, ArrayList<String> list) {
        HashMap<String, WordData> hashMap = new HashMap<>();
        ArrayList<WordData> datalist = new ArrayList<>();

        if (list.size() <= 0 || list.size() / 2 == 1) {
            Log.e(TAG, "OxfordEnglish2ChineseDictTxtParser parseFile Invalid list size! size = "
                    + list.size());
            return datalist;
        }

        for (int i = 0; i < list.size() - 1; i += 2) {
            String key = list.get(i).trim();
            String info = list.get(i + 1);

            String previousInfo = null;
            if (i >= 2) {
                previousInfo = list.get(i - 1);
            }

            WordData tmpDate = null;
            if (hashMap.containsKey(key)) {
                tmpDate = parseWordOriginalInfo(hashMap.get(key), key, info, previousInfo);
                hashMap.remove(key);
                hashMap.put(key, tmpDate);
            } else {
                tmpDate = parseWordOriginalInfo(key, info, previousInfo);
                tmpDate.file = filename;
                hashMap.put(key, tmpDate);
            }
        }

        Iterator<Entry<String, WordData>> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WordData> entry = (Map.Entry<String, WordData>) iterator.next();
            datalist.add(entry.getValue());
        }

        for (WordData data : datalist) {
            data.properties = new ArrayList<>();
            if (data.reference != null) {
                if (1 == data.originallist.size()) {
                    data.properties
                            .add(parseWordProperty(data.reference, data.originallist.get(0)));
                } else {
                    if (!isLegalCompoundWord(data)) {
                        Log.e(TAG,
                                "OxfordEnglish2ChineseDictTxtParser unexception structure, word<"
                                        + data.english + ">, ref=" + data.reference);
                    }
                }
            } else {
                for (String original : data.originallist) {
                    data.properties.add(parseWordProperty(null, original));
                }
            }
        }

        // for (WordData d : datalist) {
        // Log.d("key = " + d.english);
        // for (WordProperty p : d.properties) {
        // Log.d("<<" + p.property + ">>");
        // Log.d(" PP" + p.pronunciationRaw);
        // }
        // Log.d("-------");
        // }

        return datalist;
    }

    private boolean isLegalCompoundWord(WordData data) {
        if (!data.reference.equals(WordProperty.adverb_dot)) {
            return false;
        }
        for (String orginal : data.originallist) {
            WordProperty wordPropertyTmp = parseWordProperty(null, orginal);
            if (null == wordPropertyTmp.property) {
                continue;
            }
            if (wordPropertyTmp.property.equals(WordProperty.adjective)) {
                if (data.english.endsWith("ly")) {
                    return true;
                }
            } else if (wordPropertyTmp.property.equals(WordProperty.verb)) {
                if (data.english.endsWith("ingly")) {
                    return true;
                }
            }
        }
        return false;
    }

    private WordData parseWordOriginalInfo(WordData cur, String key, String info,
            String previousInfo) {
        cur.originallist.add(info);
        return cur;
    }

    private WordData parseWordOriginalInfo(String key, String info, String previousInfo) {
        WordData originalInfo = new WordData();
        originalInfo.english = key.trim();
        originalInfo.originallist = new ArrayList<>();

        for (String support : mSupportPropertiesRepeat) {
            if (info.trim().equals(support)) {
                originalInfo.reference = support;
                originalInfo.originallist.add(previousInfo);
            }
        }

        if (null == originalInfo.reference) {
            originalInfo.originallist.add(info);
        }

        return originalInfo;
    }

    private WordProperty parseWordProperty(String property, String orininal) {
        WordProperty wordProperty = new WordProperty();
        wordProperty.pronunciationRaw = trimPronunciation(orininal);
        wordProperty.pronunciation = PhoneticParser.parse(wordProperty.pronunciationRaw);

        String pending = null;
        if (wordProperty.pronunciationRaw != null) {
            pending = orininal.replace(wordProperty.pronunciationRaw, "");
            wordProperty.pronunciationRaw = wordProperty.pronunciationRaw.trim();
        } else {
            pending = orininal;
        }

        if (property != null) {
            wordProperty.property = property;
            wordProperty.property = wordProperty.property.trim();
        } else {
            wordProperty.property = trimWordProperty(pending);
            if (wordProperty.property != null) {
                pending = pending.replaceFirst(wordProperty.property, "");
                wordProperty.property = wordProperty.property.trim();
            }
        }

        WordProperty items = parseWordItems(pending);
        wordProperty.items = items.items;

        return wordProperty;
    }

    private WordProperty parseWordItems(String info) {
        WordProperty parent = new WordProperty();
        // Log.d(">>info=" + info);
        ArrayList<Integer> positionlist = new ArrayList<>();
        HashMap<Integer, String> hashMap = new HashMap<>();

        BracketUtils bracketUtils = new BracketUtils(info);
        for (String number : mSupportNumbers) {
            int position = -1;
            for (int from = 0;;) {
                position = info.indexOf(number, from);
                if (-1 == position) {
                    break;
                }
                if (!bracketUtils.inBracket(position)) {
                    break;
                }
                from = position + 1;
            }
            if (position > -1) {
                positionlist.add(position);
                hashMap.put(position, number);
            }
        }
        for (String alpha : mSupportAlphabetas) {
            int position = info.indexOf(alpha);
            if (position > -1) {
                positionlist.add(position);
                hashMap.put(position, alpha);
            }
        }
        validateNumbers(positionlist, hashMap, info);

        if (positionlist.isEmpty()) {
            Hierarchy item = new Hierarchy();
            item.isleaf = true;
            item.index = null;
            item.list = null;
            item.item = new WordItem(info);
            parent.items = item;
            parent = parseWordItemSegments(parent, null, info);
        } else {
            // positionlist.sort(integerComparator);
            positionlist = sort(positionlist);
            // Log.d("info = " + info);
            // for (Integer s : positionlist) {
            // Log.d("idx=" + s + ", symbol=" + hashMap.get(s));
            // }
            // Log.d("----------");
            if (positionlist.get(0) > 1) {
                parent.content = info.substring(0, positionlist.get(0) - 1);
            } else {
                parent.content = null;
            }
            parent.items = organiseHierarchy(positionlist, hashMap);
            ArrayList<Hierarchy> DLRlist = generateDLRWithNextStartIdxInfo(parent.items);
            parent = parseWordItemSegments(parent, DLRlist, info);
        }

        return parent;
    }

    private void validateNumbers(ArrayList<Integer> positionlist, HashMap<Integer, String> hashMap,
            String info) {
        ArrayList<Integer> illigelPosList = new ArrayList<>();
        int lastNumberIdx = -1;
        for (Integer pos : positionlist) {
            String itemheader = hashMap.get(pos);
            if (mSupportNumbers.contains(itemheader)) {
                int curNumberIdx = mSupportNumbers.indexOf(itemheader);
                if (lastNumberIdx > -1) {
                    if (curNumberIdx != lastNumberIdx + 1 || lastNumberIdx == 0) {
                        illigelPosList.add(pos);
                    }
                }
                lastNumberIdx = curNumberIdx;
            }
        }
        if (!illigelPosList.isEmpty()) {
            @SuppressWarnings("unused")
            String msg = "1st parsed items:";
            for (Integer pos : positionlist) {
                String itemheader = hashMap.get(pos);
                msg += itemheader + ", ";
            }
            msg += "illigel:";
            for (Integer pos : illigelPosList) {
                String itemheader = hashMap.get(pos);
                msg += itemheader + ", ";
                positionlist.remove(pos);
                hashMap.remove(pos);
            }
            // Log.i("info=" + info);
            // Log.i(msg);
        }
    }

    private ArrayList<Hierarchy> generateDLRWithNextStartIdxInfo(Hierarchy top) {
        ArrayList<Hierarchy> DLRlist = new ArrayList<>();
        DLRlist = recursionConvertToDLR(DLRlist, top);
        for (int i = 0; i < DLRlist.size() - 1; i++) {
            DLRlist.get(i).nextIndexStart = DLRlist.get(i + 1).indexStart;
        }
        DLRlist.get(DLRlist.size() - 1).nextIndexStart = -1;

        // for (Hierarchy h : DLRlist) {
        // Log.d("idx=" + h.index + ", st= " + (h.indexEnd+1) + ", end=" +
        // h.nextIndexStart);
        // }

        return DLRlist;
    }

    private ArrayList<Hierarchy> recursionConvertToDLR(ArrayList<Hierarchy> result, Hierarchy node) {
        result.add(node);

        if (node.isleaf) {
            // Log.d("recursionConvertToDLR leaf=" + node.index);
            return result;
        } else {
            for (Hierarchy child : node.list) {
                result = recursionConvertToDLR(result, child);
            }
        }
        return result;
    }

    private WordProperty parseWordItemSegments(WordProperty wordProperty,
            ArrayList<Hierarchy> DLRlist, String info) {
        HashMap<String, String> indexSegmentMap = new HashMap<>();

        if (null == DLRlist) {
            wordProperty.items.item = parseWordItemInternal(wordProperty.items.item);
            return wordProperty;
        }

        try {
            for (Hierarchy h : DLRlist) {
                int endIndex = h.nextIndexStart != -1 ? h.nextIndexStart : info.length();
                String segment = info.substring(h.indexEnd + 1, endIndex);
                // Log.d(">>put idx=" + h.index + ": segment=" + segment);
                indexSegmentMap.put(h.index, segment);
            }
        } catch (StringIndexOutOfBoundsException e) {
            Log.d(TAG, "OxfordEnglish2ChineseDictTxtParser info=" + info);
            e.printStackTrace();
        }

        Hierarchy top = wordProperty.items;
        for (int i = 0; i < top.list.size(); i++) {
            Hierarchy level1Node = top.list.get(i);
            String original = indexSegmentMap.get(level1Node.index);
            if (!original.isEmpty() && !original.trim().equals("")) {
                level1Node.item = new WordItem();
                level1Node.item.original = original;
                top.list.set(i, level1Node);
            }
            if (!level1Node.isleaf) {
                for (int j = 0; j < level1Node.list.size(); j++) {
                    Hierarchy level2Node = level1Node.list.get(j);
                    if (level2Node.isleaf) {
                        String original2 = indexSegmentMap.get(level2Node.index);
                        if (!original2.isEmpty() && !original2.trim().equals("")) {
                            level2Node.item = new WordItem();
                            level2Node.item.original = original2;
                            level1Node.list.set(j, level2Node);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < top.list.size(); i++) {
            Hierarchy level1Node = top.list.get(i);
            if (level1Node.item != null) {
                // Log.d("idx=" + level1Node.index + ": segment=" +
                // level1Node.item.original);
                level1Node.item = parseWordItemInternal(level1Node.item);
            }
            level1Node.index = level1Node.index.trim();
            top.list.set(i, level1Node);

            if (!level1Node.isleaf) {
                for (int j = 0; j < level1Node.list.size(); j++) {
                    Hierarchy level2Node = level1Node.list.get(j);
                    // Log.d(" idx=" + level2Node.index + ": segment=" +
                    // level2Node.item.original);
                    level2Node.index = level2Node.index.trim();
                    level2Node.item = parseWordItemInternal(level2Node.item);
                    level1Node.list.set(j, level2Node);
                }
            }
        }

        return wordProperty;
    }

    private WordItem parseWordItemInternal(WordItem item) {
        String original = item.original;
        int position = original.indexOf(DictConstants.SEPARATOR_EXPLAINATION);
        if (-1 == position) {
            item.explaination = original;
            String explainationSplit[] = separateEnglishAndChinese(item.explaination);
            item.explainationEng = explainationSplit[0];
            item.explainationChn = explainationSplit[1];
            item.examples = null;
            return item;
        }

        item.explaination = original.substring(0, position);
        String explainationSplit[] = separateEnglishAndChinese(item.explaination);
        item.explainationEng = explainationSplit[0];
        item.explainationChn = explainationSplit[1];

        String pending = original.substring(position
                + DictConstants.SEPARATOR_EXPLAINATION.length());
        // Log.d("explaination=" + item.explaination);
        // Log.d("pending=" + pending);

        item.examples = new ArrayList<>();
        String exampleArray[] = pending.split(DictConstants.SEPARATOR_EXAMPLE);
        for (String example : exampleArray) {
            /*
             * (idm 习语) + idom xxx. ,idom2... to be parsed in the future. just
             * truncate at the moment.
             */
            if (example.length() >= 600) {
                // Log.e(TAG, "example too long, len=" + example.length() +
                // ", example=" + example);
                example = example.substring(0, 550) + "...(idom cut by bhw1899)";
            }
            item.examples.add(example);
            // Log.d("example=" + example);
        }

        return item;
    }

    private String[] separateEnglishAndChinese(String original) {
        char charArray[] = original.toCharArray();
        int firstChineseCharacterPosition = -1;
        for (int i = 0; i < charArray.length; i++) {
            if (PinyinUtil.isChinese(charArray[i])) {
                boolean skip = false;
                int skipLength = -1;

                int position = original.indexOf(DictConstants.EXPLAINATION_SKIP_PATTERN, i);
                if (position > -1) {
                    skip = true;
                    skipLength = position - i;
                }

                if (skip) {
                    i += skipLength;
                } else {
                    firstChineseCharacterPosition = i;
                    break;
                }
            }
        }

        // Log.d("info=" + original);
        String result[] = new String[2];
        if (-1 == firstChineseCharacterPosition || 0 == firstChineseCharacterPosition) {
            result[0] = original;
            result[1] = null;
            return result;
        }

        result[0] = original.substring(0, firstChineseCharacterPosition - 1);
        result[1] = original.substring(firstChineseCharacterPosition);
        // Log.d("eng=" + result[0]);
        // Log.d("chn=" + result[1]);
        // Log.d("--------");
        return result;
    }

    @SuppressWarnings("unused")
    private Comparator<Integer> integerComparator = new Comparator<Integer>() {

        @Override
        public int compare(Integer o1, Integer o2) {
            if (o1 > o2) {
                return 1;
            } else if (o1 < o2) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    private ArrayList<Integer> sort(ArrayList<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < i; j++) {
                if (list.get(i) < list.get(j)) {
                    int tmp = list.get(j);
                    list.set(j, list.get(i));
                    list.set(i, tmp);
                }
            }
        }
        return list;
    }

    private Hierarchy organiseHierarchy(ArrayList<Integer> positionlist,
            HashMap<Integer, String> hashMap) {
        Hierarchy hierarchy = new Hierarchy();
        hierarchy.isleaf = false;
        hierarchy.index = null;
        hierarchy.indexStart = -1;
        hierarchy.indexEnd = -1;
        hierarchy.item = null;
        hierarchy.list = new ArrayList<>();

        int currentIdx = -1;
        for (Integer position : positionlist) {
            String flag = hashMap.get(position);
            if (mSupportNumbers.contains(flag)) {
                Hierarchy numberHierarchy = new Hierarchy();
                numberHierarchy.isleaf = true;
                numberHierarchy.index = flag;
                numberHierarchy.indexStart = position;
                numberHierarchy.indexEnd = position + flag.length() - 1;
                numberHierarchy.item = null;
                numberHierarchy.list = null;
                hierarchy.list.add(numberHierarchy);
                currentIdx = hierarchy.list.size() - 1;
            } else if (mSupportAlphabetas.contains(flag)) {
                Hierarchy alphaHierarchy = new Hierarchy();
                alphaHierarchy.isleaf = true;
                alphaHierarchy.index = flag;
                alphaHierarchy.indexStart = position;
                alphaHierarchy.indexEnd = position + flag.length() - 1;
                alphaHierarchy.item = new WordItem();
                alphaHierarchy.list = null;

                if (-1 == currentIdx) {
                    hierarchy.list.add(alphaHierarchy);
                } else {
                    Hierarchy numberHierarchy = hierarchy.list.get(currentIdx);
                    numberHierarchy.isleaf = false;
                    numberHierarchy.item = null;
                    if (null == numberHierarchy.list) {
                        numberHierarchy.list = new ArrayList<>();
                    }
                    numberHierarchy.list.add(alphaHierarchy);
                }
            }
        }
        return hierarchy;
    }

    private String trimPronunciation(String info) {
        Pattern p = Pattern.compile(PRONUNCIATION_REGEX_TWINS);
        // Log.d("info=" + info);
        Matcher m = p.matcher(info);
        if (m.find()) {
            // Log.d("findex=" + m.group(0));
            return m.group(0);
        }
        p = Pattern.compile(PRONUNCIATION_REGEX);
        m = p.matcher(info);
        if (m.find()) {
            // Log.d("find=" + m.group(0));
            return m.group(0);
        }
        return null;
    }

    private String trimWordProperty(String info) {
        final int MAX_PARSE_PROPERTY_LEN = 15;
        boolean bUseSubString = info.length() > MAX_PARSE_PROPERTY_LEN;
        for (String ext : mSupportPropertiesExt) {
            for (String support : mSupportPropertiesBegin) {
                if (info.startsWith(support + ext)) {
                    if (ext.equals(" ")) {
                        return support;
                    }
                    return support.replace(ext, "");
                }
            }
            for (String support : mSupportProperties) {
                if (info.startsWith(support)) {
                    if (ext.equals(" ")) {
                        return support;
                    }
                    return support.replace(ext, "");
                }
            }
            for (String support : mSupportProperties) {
                int position = -1;
                if (bUseSubString) {
                    position = info.substring(0, MAX_PARSE_PROPERTY_LEN).indexOf(support);
                } else {
                    position = info.indexOf(support);
                }
                if (position > -1) {
                    if (ext.equals(" ")) {
                        return support;
                    }
                    return support.replace(ext, "");
                }
            }
        }
        return null;
    }
}
