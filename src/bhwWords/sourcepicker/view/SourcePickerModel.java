package bhwWords.sourcepicker.view;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import bhwWords.dict.constants.Constants;
import bhwWords.provider.WordDbHelper;
import bhwWords.provider.WordProvider;

public class SourcePickerModel {

    public SourcePickerModel(Context context, SourcePickerAdapter adapter) {
        load(context, adapter);
    }

    private void load(Context context, SourcePickerAdapter adapter) {
        load(context, adapter, null);
    }

    public void load(Context context, SourcePickerAdapter adapter, String keyword) {
        Cursor cursor = getTopicListInternal(context, keyword);

        if (cursor != null) {
            adapter.setCursor(cursor);
            adapter.notifyDataSetChanged();
        }
    }

    public static ArrayList<String> getTopicList(Context context, String keyword) {
        ArrayList<String> list = new ArrayList<>();

        Cursor cursor = getTopicListInternal(context, keyword);
        if (cursor != null) {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
        }
        ArrayList<String> result = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            String string = list.get(i);
            if (string != null && !string.isEmpty() && !string.equalsIgnoreCase(Constants.DB.NULL)) {
                result.add(list.get(i));
            }
        }
        return result;
    }

    private static Cursor getTopicListInternal(Context context, String keyword) {
        String projection[] = { "DISTINCT " + WordDbHelper.TABLE_WORD.SOURCE };
        String selection = null;
        if (keyword != null) {
            selection = WordDbHelper.TABLE_WORD.SOURCE + " LIKE '%" + keyword + "%'";
        }

        Cursor cursor = context.getContentResolver().query(WordProvider.CONTENT_URI, projection,
                selection, null, null);
        return cursor;
    }
}
