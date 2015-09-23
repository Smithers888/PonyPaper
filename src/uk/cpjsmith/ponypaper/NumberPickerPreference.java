package uk.cpjsmith.ponypaper;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

/**
 * A {@link android.preference.Preference} that displays a number picker as a dialog.
 */
public class NumberPickerPreference extends DialogPreference {
    
    private static final String XMLNS_CUSTOM = "http://cpjsmith.uk/ponypaper/custom";
    
    private NumberPicker picker;
    private int value;
    
    private int maxValue;
    private int minValue;
    private boolean wrapSelectorWheel;
    
    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonInit(attrs);
    }
    
    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInit(attrs);
    }
    
    private void commonInit(AttributeSet attrs) {
        minValue = attrs.getAttributeIntValue(XMLNS_CUSTOM, "minValue", 1);
        maxValue = attrs.getAttributeIntValue(XMLNS_CUSTOM, "maxValue", 10);
        wrapSelectorWheel = attrs.getAttributeBooleanValue(XMLNS_CUSTOM, "wrap", true);
        setDialogLayoutResource(R.layout.number_picker_dialog);
    }
    
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        picker = (NumberPicker)view.findViewById(R.id.number_picker);
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setWrapSelectorWheel(wrapSelectorWheel);
        picker.setValue(getValue());
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int newValue = picker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }
    
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, minValue);
    }
    
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(minValue) : (Integer)defaultValue);
    }
    
    private void setValue(int newValue) {
        value = newValue;
        persistInt(value);
        setSummary(Integer.toString(value));
    }
    
    private int getValue() {
        return value;
    }
    
}
