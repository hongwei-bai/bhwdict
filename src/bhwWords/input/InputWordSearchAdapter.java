package bhwWords.input;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import bhwWords.dict.model.SearchHintModel;

import com.bhw1899.bhwwords.R;

public class InputWordSearchAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<String> list;

    private SearchHintModel searchHintModel;

    public InputWordSearchAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchHintModel = new SearchHintModel(context);
    }

    public void updateWord(String word) {
        if (word.trim().isEmpty()) {
            list = null;
        } else {
            list = searchHintModel.getSuggestionWordList(word);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public TextView primary;
    }

    @Override
    public int getCount() {
        if (null == list) {
            return 0;
        }
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
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.input_word_suggestion, null);
            viewHolder.primary = (TextView) convertView.findViewById(R.id.primary);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.primary.setText(list.get(position));
        return convertView;
    }
}
