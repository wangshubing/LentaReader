package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import com.xeppaka.lentareader.R;

/**
 * Created by kacpa01 on 1/3/14.
 */
public class NewsDeleteIntervalPreference extends DialogPreference {
    private static final int DEFAULT_DAYS_VALUE = 5;
    private NumberPicker daysPicker;
    private int currentValue;

    public NewsDeleteIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.news_delete_interval_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);

        if (currentValue == 1) {
            setSummary(String.format(R.string.pref_news_deletion_interval_summary_day, currentValue));
        } else {
            setSummary(R.string.pref_news_deletion_interval_summary_days);
        }
    }

    @Override
    protected View onCreateDialogView() {
        final View view = super.onCreateDialogView();
        daysPicker = (NumberPicker)view.findViewById(R.id.news_delete_number_picker);
        daysPicker.setMaxValue(365);
        daysPicker.setMinValue(1);
        daysPicker.setValue(currentValue);

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(daysPicker.getValue());
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            currentValue = this.getPersistedInt(DEFAULT_DAYS_VALUE);
        } else {
            // Set default state from the XML attribute
            currentValue = (Integer) defaultValue;
            persistInt(currentValue);
        }
    }
}
