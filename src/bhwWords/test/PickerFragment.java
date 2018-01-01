package bhwWords.test;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker;

import com.bhw1899.bhwwords.R;

public class PickerFragment extends DialogFragment implements OnClickListener {
    private NumberPicker mPickerFrom;
    private NumberPicker mPickerTo;
    private NumberPicker mPickerDuration;
    public static final int DURATION_TYPE_DAY = 0;
    public static final int DURATION_TYPE_WEEK = 1;
    public static final int DURATION_TYPE_MONTH = 2;
    private String mDurationValues[] = { "Day", "Week", "Month" };

    private SingleChoiceCheckBoxGroup mWordSentenceOrPickerGroup;
    private SingleChoiceCheckBoxGroup mPassOrFailGroup;
    public static final int WORD_ONLY = 0;
    public static final int SENTENCE_ONLY = 1;
    public static final int BOTH = 2;
    public static final int FAIL_ONLY = 0;
    public static final int PASS_ONLY = 1;

    private RatingBar mRatingBar;
    private Button mButton;
    private PickListener mPickListener;
    private PickData mPickDataStarter;

    public PickerFragment() {
        mPickDataStarter = null;
    }

    public PickerFragment(PickData pickDataStarter) {
        mPickDataStarter = pickDataStarter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picker_fragment, container);
        mPickerFrom = (NumberPicker) view.findViewById(R.id.picker_day_from);
        mPickerTo = (NumberPicker) view.findViewById(R.id.picker_day_to);
        mPickerFrom.setMaxValue(99);
        mPickerFrom.setMinValue(0);
        mPickerTo.setMaxValue(100);
        mPickerTo.setMinValue(1);

        mPickerDuration = (NumberPicker) view.findViewById(R.id.picker_duration);

        mPickerDuration.setMaxValue(mDurationValues.length - 1);
        mPickerDuration.setMinValue(0);
        mPickerDuration.setDisplayedValues(mDurationValues);

        mWordSentenceOrPickerGroup = new SingleChoiceCheckBoxGroup(view);
        mWordSentenceOrPickerGroup.addCheckBox(R.id.word_only);
        mWordSentenceOrPickerGroup.addCheckBox(R.id.sentence_only);
        mWordSentenceOrPickerGroup.addCheckBox(R.id.both_word_sentence);
        mWordSentenceOrPickerGroup.setCheckedBox(BOTH);

        mPassOrFailGroup = new SingleChoiceCheckBoxGroup(view);
        mPassOrFailGroup.addCheckBox(R.id.fail);
        mPassOrFailGroup.addCheckBox(R.id.pass);
        mPassOrFailGroup.addCheckBox(R.id.both_pass_fail);
        mPassOrFailGroup.setCheckedBox(BOTH);

        mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        mButton = (Button) view.findViewById(R.id.pick_button);
        mButton.setOnClickListener(this);

        if (mPickDataStarter != null) {
            initPickData();
        }

        return view;
    }

    private void initPickData() {
        mPickerFrom.setValue(mPickDataStarter.unitFrom);
        mPickerTo.setValue(mPickDataStarter.unitTo);
        mPickerDuration.setValue(mPickDataStarter.durationType);
        mWordSentenceOrPickerGroup.setCheckedBox(mPickDataStarter.wordOrSentence);
        mPassOrFailGroup.setCheckedBox(mPickDataStarter.passOrFail);
        mRatingBar.setRating(mPickDataStarter.importanceAbove);
    }

    public interface PickListener {
        public void onPick(PickData pickdata);
    }

    public void setOnPickListener(PickListener l) {
        mPickListener = l;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.pick_button:
            if (mPickListener != null) {
                mPickListener.onPick(buildPickData());
            }
            dismiss();
            break;
        default:
            break;
        }
    }

    private PickData buildPickData() {
        PickData pickData = new PickData();
        pickData.unitFrom = mPickerFrom.getValue();
        pickData.unitTo = mPickerTo.getValue();
        pickData.durationType = mPickerDuration.getValue();
        int coefficient = 1;
        String durationString = "";
        switch (pickData.durationType) {
        case DURATION_TYPE_DAY:
            coefficient = 1;
            durationString = "d";
            break;
        case DURATION_TYPE_WEEK:
            coefficient = 7;
            durationString = "w";
            break;
        case DURATION_TYPE_MONTH:
            coefficient = 30;
            durationString = "m";
            break;
        default:
            coefficient = 1;
            break;
        }
        pickData.dayFrom = pickData.unitFrom * coefficient;
        pickData.dayTo = pickData.unitTo * coefficient;
        pickData.passOrFail = mPassOrFailGroup.getCheckedBox();
        pickData.wordOrSentence = mWordSentenceOrPickerGroup.getCheckedBox();
        pickData.importanceAbove = (int) mRatingBar.getRating();

        String description = "";
        description += "latest";
        if (pickData.unitFrom > 0) {
            description += " " + pickData.unitFrom;
        }
        description += "-" + pickData.dayTo + durationString;
        if (pickData.passOrFail == FAIL_ONLY) {
            description += "F";
        } else if (pickData.passOrFail == PASS_ONLY) {
            description += "P";
        }

        if (pickData.wordOrSentence == WORD_ONLY) {
            description += "W";
        } else if (pickData.wordOrSentence == SENTENCE_ONLY) {
            description += "S";
        }

        for (int i = 0; i < pickData.importanceAbove; i++) {
            description += "*";
        }
        pickData.description = description;
        return pickData;
    }

    private class SingleChoiceCheckBoxGroup implements OnCheckedChangeListener {
        private ArrayList<CheckBox> group = new ArrayList<>();
        private View view;
        private int selectedBox;

        public SingleChoiceCheckBoxGroup(View view) {
            this.view = view;
        }

        public void addCheckBox(int resId) {
            CheckBox checkBox = (CheckBox) view.findViewById(resId);
            group.add(checkBox);
            checkBox.setOnCheckedChangeListener(this);
        }

        public void setCheckedBox(int boxindex) {
            selectedBox = boxindex;
            update();
        }

        public int getCheckedBox() {
            return selectedBox;
        }

        private void update() {
            for (int i = 0; i < group.size(); i++) {
                if (i == selectedBox) {
                    group.get(i).setChecked(true);
                } else {
                    group.get(i).setChecked(false);
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                update();
                return;
            }
            int id = buttonView.getId();
            for (int i = 0; i < group.size(); i++) {
                if (id == group.get(i).getId()) {
                    selectedBox = i;
                }
            }
            update();
        }
    }
}
