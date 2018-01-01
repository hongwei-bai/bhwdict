package bhwWords.sourcepicker.view;

import java.util.ArrayList;

import com.bhw1899.bhwwords.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SourcePickerAdapter extends BaseAdapter {
    private ArrayList<String> list = new ArrayList<>();
    private LayoutInflater inflater;

    public SourcePickerAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setCursor(Cursor cursor) {
        ArrayList<String> tmpList = new ArrayList<>();
        try {
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String string = cursor.getString(0);
                if (string != null && !string.isEmpty() && !string.equals("null")) {
                    tmpList.add(string);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        list.clear();
        for (int i = tmpList.size() - 1; i >= 0; i--) {
            list.add(tmpList.get(i));
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sourcepick_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(list.get(position));
        return convertView;
    }

    private class ViewHolder {
        public TextView name;
    }

}
