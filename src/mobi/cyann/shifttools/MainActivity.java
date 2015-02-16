package mobi.cyann.shifttools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import mobi.cyann.shifttools.services.ObserverService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends FragmentActivity {
	private final static String LOG_TAG = "ShiftTools.MainActivity";
	
	private boolean onCreate = false;
	private TabHost tabHost;
	private TabsAdapter tabsAdapter;
	public static HorizontalScrollView mScrollView;
	public static int suggestedWidth;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darktheme = pref.getBoolean("darktheme", false);
        if (!darktheme)
            setTheme(android.R.style.Theme_Holo_Light);
		super.onCreate(savedInstanceState);
		
		// start our ObserverService
		ObserverService.startService(this, false);
		
		// extract our scripts 
		extractScripts();
		
		// reload tweak
		reloadTweak();
		// flag oncreate
		onCreate = true;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
	
		tabHost = (TabHost)findViewById(android.R.id.tabhost);
		tabHost.setup();
		
		ViewPager viewPager = (ViewPager)findViewById(R.id.pager);

		mScrollView = (HorizontalScrollView)findViewById(R.id.hscroll);
	
		tabsAdapter = new TabsAdapter(this, tabHost, viewPager);
		
		Resources res = getResources();
		
		TabSpec tab1 = tabHost.newTabSpec("nstweak");
		tab1.setIndicator(getString(R.string.ns_tweak));
		tabsAdapter.addTab(tab1, NSTweakFragment.class, null);

		TabSpec tab2 = tabHost.newTabSpec("performance");
		tab2.setIndicator(getString(R.string.label_performance_tweak));
		tabsAdapter.addTab(tab2, PerformanceFragment.class, null);

		TabSpec tab3 = tabHost.newTabSpec("cpu");
		tab3.setIndicator(getString(R.string.label_cpu_tweak));
		tabsAdapter.addTab(tab3, CPUFragment.class, null);

		TabSpec tab4 = tabHost.newTabSpec("volt");
		tab4.setIndicator(getString(R.string.voltage_control));
		tabsAdapter.addTab(tab4, VoltageControlFragment.class, null);

		TabSpec tab5 = tabHost.newTabSpec("chargingcontrol");
		tab5.setIndicator(getString(R.string.label_battery_tweak));
		tabsAdapter.addTab(tab5, BatteryFragment.class, null);

		TabSpec tab6 = tabHost.newTabSpec("audio");
		tab6.setIndicator(getString(R.string.label_audio_tweak));
		tabsAdapter.addTab(tab6, AudioFragment.class, null);
		
		TabSpec tab7 = tabHost.newTabSpec("setting");
		tab7.setIndicator(getString(R.string.label_setting));
		tabsAdapter.addTab(tab7, SettingFragment.class, null);

		TabSpec tab8 = tabHost.newTabSpec("info");
		tab8.setIndicator(getString(R.string.label_info));
		tabsAdapter.addTab(tab8, InfoFragment.class, null);

		suggestedWidth = tabsAdapter.getSuggestedWidth();
       		// set the width of tab 
        	for(int i=0;i<tabHost.getTabWidget().getChildCount();i++){
             	tabHost.getTabWidget().getChildAt(i).getLayoutParams().width = suggestedWidth;
        	}
		
		if (savedInstanceState != null) {
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
	}

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", tabHost.getCurrentTabTag());
    }
	
    @Override
	protected void onResume() {
		super.onResume();
		if(!onCreate) {
			reloadTweak();
		}
		onCreate = false;
	}

    private void reloadTweak() {
		SysCommand sc = SysCommand.getInstance();
		
		// execute our preload script to get values from sys interface
		sc.suRun(getString(R.string.PRELOAD_SCRIPT));
		
		PreloadValues.getInstance().reload();
		
		// save kernel version
		if(sc.readSysfs("/proc/version") > 0) {
			String kernel = sc.getLastResult(0);
			
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			Editor ed = pref.edit();
			ed.putString(getString(R.string.key_kernel_version), kernel);
			ed.commit();
		}
		
		Intent i = new Intent();
		i.setAction("mobi.cyann.shifttools.RELOAD");
		sendBroadcast(i);
    }
    
	private void copyAsset(String srcPath, String dstPath) throws IOException {
		AssetManager assetManager = getApplicationContext().getAssets();
    	InputStream is = assetManager.open(srcPath);
    	FileOutputStream fos = new FileOutputStream(dstPath, false);
    	byte[] buffer = new byte[100];
    	int n = -1;
    	do {
    		n = is.read(buffer);
    		if(n != -1) {
    			fos.write(buffer, 0, n);
    		}
    	}while(n != -1);
    	fos.flush();
    	fos.close();
    	is.close();
    }
    
	private void extractScripts() {
		String scriptDir = getString(R.string.SCRIPT_DIR);
		String scriptVersion = getString(R.string.SCRIPT_VERSION);
		String settingsDir = getString(R.string.SETTINGS_DIR);
		String prefsDir = getString(R.string.PREFS_DIR);
		
		String scriptVersionTagFile = scriptDir + scriptVersion;
		// first check script version (in the future we can change SCRIPT_VERSION constant to overwrite existing scripts)
		if(!new File(scriptVersionTagFile).exists()) {
			try {
				SysCommand sc = SysCommand.getInstance();
				int r = sc.suRun("rm", "-r", prefsDir);
				if(r < 0) {
					Log.e(LOG_TAG, sc.getLastError(0));
				}
				// clean old script dir
				r = sc.suRun("rm", "-r", scriptDir);
				if(r < 0) {
					Log.e(LOG_TAG, sc.getLastError(0));
				}
				// create script dir
				new File(scriptDir).mkdir();
				// copy all scripts
				String[] scripts = getResources().getStringArray(R.array.scripts);
				for(String f: scripts) {
					copyAsset(f, scriptDir + f);
					r = SysCommand.getInstance().suRun("chmod", "0755", scriptDir + f);
					if(r < 0) {
						Log.e(LOG_TAG, sc.getLastError(0));
					}
				}
				// mark script version
				FileWriter fw = new FileWriter(scriptVersionTagFile);
				fw.write(scriptVersion);
				fw.close();

				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				pref.edit().clear().commit();
			}catch(IOException e) {
				Log.e(LOG_TAG, "failed to extract scripts", e);
			}catch(NullPointerException e) {
				Log.e(LOG_TAG, "failed to extract scripts", e);
			}
		}
	}
	
	public static void restart(Context c) {
	    Intent i = new Intent(c, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
	}
}
