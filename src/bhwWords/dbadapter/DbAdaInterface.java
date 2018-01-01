package bhwWords.dbadapter;

import java.util.ArrayList;

public interface DbAdaInterface {
    public boolean getConnection(Object dbWrapper);

    public int applyBatch(ArrayList<Object> list);

    public int insert(String table, String[] keys, String[] values);

    public Object buildInsertOperation(String table, String[] keys, String[] values);

    public Object buildDeleteOperation(String table, String condition);

    public int getLastIndex(String table);

    public int queryId(String table, String condition);

    public String queryField(String table, String field, String condition);

    public ArrayList<Integer> queryIdList(String table, String condition);

    public ArrayList<String> queryFieldList(String table, String field, String condition);

    public ArrayList<ArrayList<String>> queryFieldsList(String table, ArrayList<String> fields,
            String condition);

    public boolean isTableExist(String table);

    public boolean createTableIfNotExist(String table, String[] columns);

    public int executeSql(String sql);

    public int delete(String table, String condition);
}
