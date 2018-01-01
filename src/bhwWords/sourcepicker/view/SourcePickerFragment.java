package bhwWords.sourcepicker.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import bhwWords.test.PickData;
import bhwWords.test.PickerFragment;

import com.bhw1899.bhwwords.R;

public class SourcePickerFragment extends DialogFragment implements OnClickListener, TextWatcher,
        OnItemClickListener {

    private TextView searchTextView;
    private ListView listView;
    private SourcePickerAdapter adapter;
    private SourcePickerModel model;
    private PickListener listener;

    public SourcePickerFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        adapter = new SourcePickerAdapter(activity);
        model = new SourcePickerModel(activity, adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.source_picker_fragment, container);
        searchTextView = (TextView) view.findViewById(R.id.search);
        searchTextView.addTextChangedListener(this);
        listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return view;
    }

    public interface PickListener {
        public void onPick(PickData pickData);
    }

    public void setOnPickListener(PickListener l) {
        listener = l;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            PickData data = new PickData();
            data.passOrFail = PickerFragment.BOTH;
            data.description = (String) adapter.getItem(position);
            listener.onPick(data);
            dismiss();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
        String keyword = s.toString();
        model.load(getActivity(), adapter, keyword);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }
}
