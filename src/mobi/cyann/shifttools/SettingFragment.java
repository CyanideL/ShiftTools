/**
 * SettingFragment.java
 * Nov 5, 2011 3:12:07 PM
 */
package mobi.cyann.shifttools;

import mobi.cyann.shifttools.PreferenceListFragment.OnPreferenceAttachedListener;
import mobi.cyann.shifttools.services.ObserverService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.widget.Toast;

/**
 * @author arif
 *
 */
public class SettingFragment extends PreferenceListFragment implements OnPreferenceAttachedListener, OnPreferenceClickListener, OnPreferenceChangeListener {
	public SettingFragment() {
		super(R.layout.setting);
		setOnPreferenceAttachedListener(this);
	}

    private CheckBoxPreference mService;
    private CheckBoxPreference mSecondrom;
    private static final String BLNFILE = "/sys/class/misc/backlightnotification/enabled";

    public static final String SECONDROM_PATH = "/data/media/.secondrom/";
    public static final String SECONDROM_SYSTEM_PATH = "/data/media/.secondrom/system.img";
    public static final String SECONDROM_VERIFY_PATH = "/.secondrom/media";
    public static final String SECONDROM_PRIMARY_PATH = "/data/.secondaryboot";
    public static final String SECONDROM_SECONDARY_PATH = "/.secondrom/.secondaryboot";

    private static boolean BLNisSupported() {
        return Utils.fileExists(BLNFILE);
    }

    private static boolean HasSecondrom() {
        return Utils.fileExists(SECONDROM_SYSTEM_PATH) || IsSecondrom() || 
		Utils.folderNotEmpty(SECONDROM_PATH);
    }

    private static boolean IsSecondrom() {
        return Utils.fileExists(SECONDROM_VERIFY_PATH);
    }

	@Override
	public void onPreferenceAttached(PreferenceScreen rootPreference, int xmlId) {

	SysCommand sc = SysCommand.getInstance();

		mService = (CheckBoxPreference) findPreference(getString(R.string.key_shifttools_service));
        	mService.setEnabled(BLNisSupported());
		if(BLNisSupported()) {
		mService.setOnPreferenceChangeListener(this);
		} else {
            	getPreferenceScreen().removePreference(mService);
		}
		findPreference(getString(R.string.key_load_settings)).setOnPreferenceChangeListener(this);
		findPreference(getString(R.string.key_save_settings)).setOnPreferenceClickListener(this);
		findPreference(getString(R.string.key_save_settings)).setOnPreferenceChangeListener(this);
		findPreference(getString(R.string.key_delete_settings)).setOnPreferenceChangeListener(this);

		mSecondrom = (CheckBoxPreference) findPreference(getString(R.string.key_reboot_preference));
        	mSecondrom.setEnabled(HasSecondrom());
		if(HasSecondrom()) {
		mSecondrom.setOnPreferenceChangeListener(this);
		   if(IsSecondrom()) {
	   		if(sc.readSysfs(SECONDROM_SECONDARY_PATH) > 0)
           	   	   mSecondrom.setChecked(Integer.valueOf(sc.getLastResult(0)) > 0 ? true : false);
		   } else {
	   		if(sc.readSysfs(SECONDROM_PRIMARY_PATH) > 0)
           	   	   mSecondrom.setChecked(Integer.valueOf(sc.getLastResult(0)) > 0 ? true : false);
		   }
		//Toast.makeText(getActivity(), String.valueOf(Integer.valueOf(sc.getLastResult(0)) > 0 ? true : false), Toast.LENGTH_LONG).show();
		}

 	Preference button = (Preference)findPreference("theme_apply");
   	if(button != null) 
   	{
        button.setOnPreferenceClickListener(new Preference.
		OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                boolean darktheme = prefs.getBoolean("darktheme", false);
                Editor edit = prefs.edit();
                edit.putBoolean("darktheme", !darktheme);
                edit.commit();
		MainActivity.restart(getActivity());
                        return true;
                    }
                });     
    	}
		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(getString(R.string.key_save_settings))) {
			((EditTextPreference)preference).getEditText().setText("");
		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getKey().equals(getString(R.string.key_load_settings))) {
			if(newValue != null && newValue.toString().length() > 0) {
				SettingsManager.loadSettings(getActivity(), newValue.toString());
				MainActivity.restart(getActivity());
			}
			return true;
		}else if(preference.getKey().equals(getString(R.string.key_save_settings))) {
			if(newValue != null && newValue.toString().length() > 0) {
				if(SettingsManager.saveSettings(getActivity(), newValue.toString())) {
					Toast.makeText(getActivity(), getString(R.string.save_success_message), Toast.LENGTH_LONG).show();
				}else {
					Toast.makeText(getActivity(), getString(R.string.save_failed_message), Toast.LENGTH_LONG).show();
				}
			}
			return true;
		}else if(preference.getKey().equals(getString(R.string.key_delete_settings))) {
			if(newValue != null && newValue.toString().length() > 0) {
				SettingsManager.deleteSettings(getActivity(), newValue.toString());
			}
			return true;
		}else if(preference == mService) {
			if(newValue != null) {
			    if(!BLNisSupported())
				newValue = false;
				if((Boolean)newValue) {
					ObserverService.startService(getActivity(), true);
				}else {
					ObserverService.stopService(getActivity());
				}
			}
			return true;
		} else if(preference == mSecondrom) {
			if(newValue != null) {
			   SysCommand sc = SysCommand.getInstance();
		   	   sc.writeSysfs(IsSecondrom() ? 
				SECONDROM_SECONDARY_PATH : SECONDROM_PRIMARY_PATH,
				 String.valueOf((Boolean)newValue ? 1 : 0));
			}
			return true;
		}
		return false;
	}
}
