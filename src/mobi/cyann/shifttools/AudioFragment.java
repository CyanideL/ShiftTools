package mobi.cyann.shifttools;

import mobi.cyann.shifttools.PreferenceListFragment.OnPreferenceAttachedListener;
import mobi.cyann.shifttools.EqTuningPreference;
import mobi.cyann.shifttools.preference.IntegerPreference;
import mobi.cyann.shifttools.preference.StatusPreference;
import mobi.cyann.shifttools.SysCommand;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.TextView;


public class AudioFragment extends BasePreferenceFragment implements OnPreferenceAttachedListener{
	//private final static String LOG_TAG = "ShiftTools.AudioActivity";
	
	public AudioFragment() {
		super(R.layout.audio);
		setOnPreferenceAttachedListener(this);
	}

    public static final String[] VOODOO_EQ_KEY = new String[] {
        "key_voodoo_sound_headphone_eq_b1_gain",
        "key_voodoo_sound_headphone_eq_b2_gain",
        "key_voodoo_sound_headphone_eq_b3_gain",
        "key_voodoo_sound_headphone_eq_b4_gain",
        "key_voodoo_sound_headphone_eq_b5_gain"
    };

    public static final String[] VOODOO_FILE_PATH = new String[] {
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b1_gain",
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b2_gain",
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b3_gain",
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b4_gain",
        "/sys/devices/virtual/misc/voodoo_sound/headphone_eq_b5_gain"
    };


	@Override
	public void onPreferenceAttached(PreferenceScreen rootPreference, int xmlId) {

 	Preference button = (Preference)findPreference("key_scoobydoo_sound_enable");
   	if(button != null) 
   	{
        button.setOnPreferenceClickListener(new Preference.
		OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
			MainActivity.restart(getActivity());
                        return true;
                    }
                });     
    	}


	}



}
