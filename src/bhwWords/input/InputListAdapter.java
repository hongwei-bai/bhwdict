package bhwWords.input;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import bhwWords.dict.constants.Constants;
import bhwWords.dict.model.DisplayItem;
import bhwWords.dict.model.LookupModel;

import com.bhw1899.bhwwords.R;

public class InputListAdapter extends BaseAdapter {
    public static final String BUTTON_SEARCH = "Search";
    public static final String BUTTON_ADD = "Add";

    private boolean bSingleExplain = false;
    private LayoutInflater inflater;
    private ArrayList<DisplayItem> list;

    private int ratingBarInt = 3;
    private LookupModel lookupModel;

    public InputListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lookupModel = new LookupModel(context);
    }

    public void setData(String english) {
        list = lookupModel.lookupDisplayItems(english);

        int i = 0;
        for (DisplayItem itemData : list) {
            if (Constants.VIEW_TYPE_EXPLAIN == itemData.viewType) {
                i++;
            }
        }
        bSingleExplain = (i == 1);
        notifyDataSetChanged();
    }

    public class ViewHolder {
        public TextView primary;
        public CheckBox checkBox;
        public RatingBar ratingBar;
        public int position;
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
        return list.get(position).getString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            switch (getItemViewType(position)) {
            case Constants.VIEW_TYPE_PHONTIC:
                convertView = inflater.inflate(R.layout.input_item_phonetic, null);
                viewHolder.primary = (TextView) convertView.findViewById(R.id.primary);
                break;
            case Constants.VIEW_TYPE_RATING:
                convertView = inflater.inflate(R.layout.input_item_rating, null);
                viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating);
                viewHolder.ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        ratingBarInt = (int) rating;
                    }
                });
                break;
            case Constants.VIEW_TYPE_EXPLAIN:
                convertView = inflater.inflate(R.layout.input_item_explain, null);
                viewHolder.primary = (TextView) convertView.findViewById(R.id.primary);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                viewHolder.position = position;
                break;
            default:
                break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        switch (getItemViewType(position)) {
        case Constants.VIEW_TYPE_PHONTIC:
            viewHolder.primary.setText(list.get(position).getString());
            break;
        case Constants.VIEW_TYPE_RATING:
            viewHolder.ratingBar.setRating(3);
            break;
        case Constants.VIEW_TYPE_EXPLAIN:
            viewHolder.primary.setText(list.get(position).getString());
            if (bSingleExplain) {
                viewHolder.checkBox.setChecked(true);
                viewHolder.checkBox.setEnabled(false);
                DisplayItem itemData = list.get(position);
                itemData.selected = true;
                list.set(position, itemData);
            } else {
                viewHolder.checkBox.setEnabled(true);
                viewHolder.checkBox.setChecked(list.get(position).selected);
            }
            break;
        default:
            break;
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return Constants.VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getViewType();
    }

    public String getPhonetic() {
        for (DisplayItem itemData : list) {
            if (Constants.VIEW_TYPE_PHONTIC == itemData.getViewType()) {
                return itemData.getString();
            }
        }
        return null;
    }

    public int getRating() {
        return ratingBarInt;
    }

    public String getChinese() {
        String chinese = "";
        for (DisplayItem itemData : list) {
            if (Constants.VIEW_TYPE_EXPLAIN == itemData.getViewType()) {
                if (itemData.selected) {
                    chinese += itemData.getString() + "\n";
                }
            }
        }
        chinese = trimEmptyLine(chinese);
        return chinese;
    }

    private String trimEmptyLine(String org) {
        String result = "";
        String lines[] = org.split("\n");
        for (int i = 0; i < lines.length - 1; i++) {
            result += lines[i] + "\n";
        }
        result += lines[lines.length - 1];
        return result;
    }

    public void toggleCheck(int position) {
        list.get(position).selected = !list.get(position).selected;
        notifyDataSetChanged();
    }
}
