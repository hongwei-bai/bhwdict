package bhwWords.dict.model;

import java.util.ArrayList;

import bhwWords.dbadapter.DbAdaInterface;
import bhwWords.dbadapter.DbAdaTable;
import bhwWords.dbadapter.SQLiteImpl;
import bhwWords.dict.constants.Constants;
import bhwWords.dict.txtparser.Hierarchy;
import bhwWords.dict.txtparser.WordItem;
import bhwWords.dict.txtparser.WordProperty;

public class LookupModel implements Constants, Constants.DB {
    private DbAdaInterface mDbAdaInterface;

    public LookupModel(Object dbWrapper) {
        mDbAdaInterface = new SQLiteImpl(dbWrapper);
    }

    public ArrayList<DisplayItem> lookupDisplayItems(String english) {
        ArrayList<DisplayItem> list = new ArrayList<>();
        if (null == english) {
            return list;
        }

        ArrayList<WordProperty> wordProperties = lookupWordProperties(english);
        if (null == wordProperties) {
            return list;
        }
        ArrayList<Hierarchy> wordItems = new ArrayList<>();
        for (WordProperty wp : wordProperties) {
            ArrayList<Hierarchy> wordItemPerProperty = lookupWordItems(wp._id);
            for (Hierarchy hierarchy : wordItemPerProperty) {
                hierarchy.item.pid = wp._id;
                wordItems.add(hierarchy);
            }
        }

        if (null == wordProperties || 0 == wordProperties.size()) {
            return list;
        }

        for (WordProperty property : wordProperties) {
            if (property.pronunciation != null && !NULL.equals(property.pronunciation)) {
                list.add(new DisplayItem(VIEW_TYPE_PHONTIC, property.pronunciation));
                break;
            }
        }
        list.add(new DisplayItem(VIEW_TYPE_RATING));

        for (WordProperty property : wordProperties) {
            String propertyInfo = "";
            if (property.property != null && !NULL.equals(property.property)) {
                propertyInfo = property.property;
                if (!propertyInfo.endsWith(".") && !propertyInfo.endsWith(":")) {
                    propertyInfo += ".";
                }
            }
            if (property.content != null && !NULL.equals(property.content)) {
                list.add(new DisplayItem(VIEW_TYPE_EXPLAIN, property.content));
            }
            int pid = property._id;
            String upperLevelTag = null;
            for (Hierarchy wordItem : wordItems) {
                if (wordItem.item.pid == pid) {
                    if (wordItem.containInfo) {
                        String infomation = "";
                        infomation += propertyInfo + " ";
                        if (upperLevelTag != null && !NULL.equals(upperLevelTag)) {
                            infomation += upperLevelTag;
                            upperLevelTag = null;
                        }
                        if (wordItem.index != null && !NULL.equals(wordItem.index)) {
                            infomation += wordItem.index + " ";
                        }
                        infomation += wordItem.item.explaination;
                        list.add(new DisplayItem(VIEW_TYPE_EXPLAIN, infomation));
                    } else {
                        if (1 == wordItem.level) {
                            upperLevelTag = wordItem.index;
                        }
                    }
                }
            }
        }
        return list;
    }

    private ArrayList<WordProperty> lookupWordProperties(String word) {
        int id = getWordId(word);
        if (-1 == id) {
            return null;
        }
        return getWordProperty(id);
    }

    private ArrayList<Hierarchy> lookupWordItems(int propertyId) {
        return getWordItem(propertyId);
    }

    @SuppressWarnings("unused")
    private ArrayList<String> lookupWordExamples(int itemId) {
        return getExamples(itemId);
    }

    private int getWordId(String english) {
        String selection = DbAdaTable.TABLE_WORD.ENGLISH + "='" + english + "'";
        return mDbAdaInterface.queryId(DbAdaTable.TABLE_WORD.TABLE, selection);
    }

    private ArrayList<WordProperty> getWordProperty(int wordId) {
        ArrayList<WordProperty> result = new ArrayList<>();
        String condition = DbAdaTable.TABLE_PROPERTY.PARENT_ID + "=" + wordId;
        ArrayList<String> fieldlist = new ArrayList<>();
        fieldlist.add(DbAdaTable.TABLE_PROPERTY._ID);
        fieldlist.add(DbAdaTable.TABLE_PROPERTY.PROPERTY);
        fieldlist.add(DbAdaTable.TABLE_PROPERTY.PRONUNCIATION);
        fieldlist.add(DbAdaTable.TABLE_PROPERTY.CONTENT);

        ArrayList<ArrayList<String>> list2d = mDbAdaInterface.queryFieldsList(
                DbAdaTable.TABLE_PROPERTY.TABLE, fieldlist, condition);
        for (ArrayList<String> data : list2d) {
            WordProperty property = new WordProperty();
            property._id = Integer.valueOf(data.get(0));
            property.property = data.get(1);
            property.pronunciation = data.get(2);
            property.content = data.get(3);
            result.add(property);
        }
        return result;
    }

    private ArrayList<Hierarchy> getWordItem(int propertyId) {
        ArrayList<Hierarchy> result = new ArrayList<>();
        String condition = DbAdaTable.TABLE_ITEM.PARENT_ID + "=" + propertyId;
        ArrayList<String> fieldlist = new ArrayList<>();
        fieldlist.add(DbAdaTable.TABLE_ITEM._ID);
        fieldlist.add(DbAdaTable.TABLE_ITEM.ITEM);
        fieldlist.add(DbAdaTable.TABLE_ITEM.LEVELNO);
        fieldlist.add(DbAdaTable.TABLE_ITEM.SEQUENCE);
        fieldlist.add(DbAdaTable.TABLE_ITEM.CONTAIN_INFORMATION);
        fieldlist.add(DbAdaTable.TABLE_ITEM.EXPLAINATION);
        fieldlist.add(DbAdaTable.TABLE_ITEM.EXPLAINATION_ENG);
        fieldlist.add(DbAdaTable.TABLE_ITEM.EXPLAINATION_CHN);

        ArrayList<ArrayList<String>> list2d = mDbAdaInterface.queryFieldsList(
                DbAdaTable.TABLE_ITEM.TABLE, fieldlist, condition);

        for (ArrayList<String> data : list2d) {
            Hierarchy hierarchy = new Hierarchy();
            hierarchy.item = new WordItem();
            hierarchy.item._id = Integer.valueOf(data.get(0));
            hierarchy.index = data.get(1);
            hierarchy.level = Integer.valueOf(data.get(2));
            hierarchy.sequence = Integer.valueOf(data.get(3));
            hierarchy.containInfo = data.get(4).equals("1");
            hierarchy.item.explaination = data.get(5);
            hierarchy.item.explainationEng = data.get(6);
            hierarchy.item.explainationChn = data.get(7);
            result.add(hierarchy);
        }

        return result;
    }

    private ArrayList<String> getExamples(int itemId) {
        String condition = DbAdaTable.TABLE_EXAMPLE.PARENT_ID + "=" + itemId;
        ArrayList<String> listExample = mDbAdaInterface.queryFieldList(
                DbAdaTable.TABLE_EXAMPLE.TABLE, DbAdaTable.TABLE_EXAMPLE.EXAMPLE, condition);
        return listExample;
    }
}
