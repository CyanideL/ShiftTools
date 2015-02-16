 /*   
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 */
package mobi.cyann.shifttools;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

public class InfoFragment extends PreferenceListFragment implements OnPreferenceClickListener {

    public InfoFragment() {
		super(R.layout.info);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	SysCommand sysCommand = SysCommand.getInstance();
        Preference pref = findPreference("kernel_version");
	if(sysCommand.readSysfs("/proc/version") > 0) {
           pref.setSummary(sysCommand.getLastResult(0));
	}
        
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long totalMegs = mi.totalMem / 1048576L;

        pref = findPreference("system_memory");
        pref.setSummary(String.valueOf(totalMegs) + " MB");

		
	Preference about = findPreference(getString(R.string.key_about));
	about.setOnPreferenceClickListener(this);
	about.setTitle(getString(R.string.app_name)+ " " + getString(R.string.app_version));

	Preference donation = findPreference(getString(R.string.key_donation));
	donation.setOnPreferenceClickListener(this);
        
    }

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(getString(R.string.key_donation))) {
			Intent browse = new Intent();
			browse.setAction(Intent.ACTION_VIEW);
			browse.setData(Uri.parse(getString(R.string.shifttools_donation_url)));
			startActivity(browse);
		}
		return false;
	}

}
