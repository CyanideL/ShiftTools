/**
 * IntegerPreference.java
 * Jan 21, 2012 10:47:26 PM

 */
package mobi.cyann.shifttools.preference.normal;

import mobi.cyann.shifttools.R;
import mobi.cyann.shifttools.SeekbarDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * @author arif
 *
 */
public class IntegerPreference extends Preference implements DialogInterface.OnClickListener {

	private Context context;
	private int minValue, maxValue, step;
	private String metrics;
	private String description;
	private int value;
	
	public IntegerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.mobi_cyann_shifttools_preference_IntegerPreference, defStyle, 0);
		minValue = a.getInt(R.styleable.mobi_cyann_shifttools_preference_IntegerPreference_minValue, 0);
		maxValue = a.getInt(R.styleable.mobi_cyann_shifttools_preference_IntegerPreference_maxValue, 100);
		step = a.getInt(R.styleable.mobi_cyann_shifttools_preference_IntegerPreference_step, 1);
		metrics = a.getString(R.styleable.mobi_cyann_shifttools_preference_IntegerPreference_metrics);
		description = a.getString(R.styleable.mobi_cyann_shifttools_preference_IntegerPreference_description);
		a.recycle();
		
		this.context = context;
	}

	public IntegerPreference(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IntegerPreference(Context context) {
		this(context, null);
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		// Sync the summary view
        TextView summaryView = (TextView) view.findViewById(android.R.id.summary);
        if (summaryView != null) {
        	if(value < 0) {
        		summaryView.setText(R.string.status_not_available);
        	}else if(metrics != null) {
			if(description != null)
        		summaryView.setText(description + "\n" + value + " " + metrics);
			else
        		summaryView.setText(value + " " + metrics);
        	}else {
			if(description != null)
        		summaryView.setText(description + "\n" + value);
			else
        		summaryView.setText(String.valueOf(value));
        	}
        }
	}
	
	@Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return  a.getInt(index, 0);
    }
    
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
    	if(restoreValue) {
    		value = getPersistedInt(-1);
    	}
    }
    
	@Override
	protected void onClick() {
		SeekbarDialog dialog = new SeekbarDialog(context, this, this);
		
		dialog.setMin(minValue);
		dialog.setMax(maxValue);
		dialog.setStep(step);
		dialog.setTitle(getTitle());
		dialog.setMetrics(metrics);
		dialog.setDescription(description);
		dialog.setValue(value);
		dialog.show();
	}
	
	@Override
	public void onClick(DialogInterface d, int which) {
		if(which == DialogInterface.BUTTON_POSITIVE) {
			int newValue = ((SeekbarDialog)d).getValue();
			if (!callChangeListener(newValue)) {
	            return;
	        }
			value = newValue;
			persistInt(newValue);
			
			notifyDependencyChange(shouldDisableDependents());
            notifyChanged();
		}
	}
	
	public void setMetrics(String metrics) {
		this.metrics = metrics;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	
	public void setStep(int step) {
		this.step = step;
	}

	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
	}
}
