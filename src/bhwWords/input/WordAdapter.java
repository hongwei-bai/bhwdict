package bhwWords.input;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import bhwWords.provider.WordDbHelper;
import bhwWords.provider.WordProvider;

import com.bhw1899.bhwwords.R;

public class WordAdapter extends BaseAdapter {
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private boolean mIsEng = true;
    private ArrayList<String> list;
    private ArrayList<String> exampleList;
    private Context context;

    public WordAdapter(Context context) {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list = new ArrayList<>();
        exampleList = new ArrayList<>();
    }

    public void setLanguage(boolean bEng) {
        mIsEng = bEng;
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
        saveCursorToLocalList();
        notifyDataSetChanged();
    }

    private void saveCursorToLocalList() {
        if (null == mCursor || mCursor.isClosed()) {
            return;
        }
        list.clear();
        exampleList.clear();
        HashMap<Integer, String> exampleMap = new HashMap<>();

        Cursor dataCursor = context.getContentResolver().query(WordProvider.DATA_URI, null, null,
                null, null);
        try {
            dataCursor.moveToPosition(-1);
            while (dataCursor.moveToNext()) {
                String mimeType = dataCursor.getString(dataCursor
                        .getColumnIndex(WordDbHelper.TABLE_DATA.MIME_TYPE));
                if (mimeType.equals(WordDbHelper.MIME_TYPE.EXAMPLE)) {
                    int wordId = dataCursor.getInt(dataCursor
                            .getColumnIndex(WordDbHelper.TABLE_DATA.WORD_ID));
                    String example = dataCursor.getString(dataCursor
                            .getColumnIndex(WordDbHelper.TABLE_DATA.DATA5));
                    exampleMap.put(wordId, example);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dataCursor != null) {
                dataCursor.close();
            }
        }

        mCursor.moveToPosition(-1);
        while (mCursor.moveToNext()) {
            int wordId = mCursor.getInt(mCursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
            String english = mCursor.getString(mCursor
                    .getColumnIndex(WordDbHelper.TABLE_WORD.ENGLISH));
            list.add(english);
            String example = "";
            if (exampleMap.containsKey(wordId)) {
                example = exampleMap.get(wordId);
            }
            exampleList.add(example);
        }
    }

    public ArrayList<String> getEnglishList() {
        return list;
    }

    public ArrayList<String> getExampleList() {
        return exampleList;
    }

    @Override
    public int getCount() {
        return mCursor != null && !mCursor.isClosed() ? mCursor.getCount() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(mCursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
        }
        return -1;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.word_item, null);
            viewHolder.primary = (TextView) convertView.findViewById(R.id.primary);
            viewHolder.secondary = (TextView) convertView.findViewById(R.id.secondary);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.moveToPosition(position);
            int primaryCol;
            if (mIsEng) {
                primaryCol = mCursor.getColumnIndex(WordDbHelper.TABLE_WORD.ENGLISH);
            } else {
                primaryCol = mCursor.getColumnIndex(WordDbHelper.TABLE_WORD.CHINESE);
            }
            int importance = mCursor.getInt(mCursor
                    .getColumnIndex(WordDbHelper.TABLE_WORD.IMPORTANCE));
            int direction = mCursor.getInt(mCursor
                    .getColumnIndex(WordDbHelper.TABLE_WORD.DIRECTION));
            int engPass = mCursor.getInt(mCursor.getColumnIndex(WordDbHelper.TABLE_WORD.EN_PASS));
            int chnPass = mCursor.getInt(mCursor.getColumnIndex(WordDbHelper.TABLE_WORD.CN_PASS));
            float alpha = ItemPresenter.getImportanceAlpha(importance);
            convertView.setAlpha(alpha);

            String primaryString = "";
            String secondaryString = "";
            primaryString += mCursor.getString(primaryCol);

            for (int i = 0; i < importance; i++) {
                secondaryString += "*";
            }
            secondaryString += "  ";
            if (direction == 0) {
                secondaryString += "en:";
                secondaryString += "  " + (engPass > 0 ? "pass" : "fail");
            } else if (direction == 1) {
                secondaryString += "CN:";
                secondaryString += "  " + (chnPass > 0 ? "pass" : "fail");
            } else if (direction == 2) {
                secondaryString += "en:";
                secondaryString += "  " + (engPass > 0 ? "pass" : "fail");
                secondaryString += "/CN:";
                secondaryString += "  " + (chnPass > 0 ? "pass" : "fail");
            }

            int textSize = mIsEng && primaryString.length() < 20 ? 24 : 18;
            viewHolder.primary.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            viewHolder.primary.setText(primaryString);
            viewHolder.secondary.setText(secondaryString);

            int review = mCursor
                    .getInt(mCursor.getColumnIndex(WordDbHelper.TABLE_WORD.REVIEW_FLAG));
            if (review > 0) {
                convertView.setBackgroundColor(Color.RED);
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView primary;
        TextView secondary;
    }

    @Override
    protected void finalize() throws Throwable {
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        super.finalize();
    }
}
