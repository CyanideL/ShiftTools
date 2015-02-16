/**
 * SettingsManager.java
 * Nov 27, 2011 11:19:28 AM
 */
package mobi.cyann.shifttools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.lang.Integer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author arif
 *
 */
public class SettingsManager {
	private final static String LOG_TAG = "ShiftTools.SettingsManager";
	
	public final static int SUCCESS = 0;
	public final static int ERR_SET_ON_BOOT_FALSE = -1;
	public final static int ERR_DIFFERENT_KERNEL = -2;
	
		private static String buildCommand(Context c, SharedPreferences preferences) {
		StringBuilder command = new StringBuilder();
		
		String status = null;
		String filepath = null;
		int value = -1;
		if(!preferences.getBoolean(c.getString(R.string.key_default_voltage), true)) {
			// restore voltage setting if and only if key_default_voltage is false
			
			// customvoltage
			// -----------------
			// arm voltages
			value = preferences.getInt(c.getString(R.string.key_max_arm_volt), -1000);
			if(value > -1000) {
				String armvolts = preferences.getString(c.getString(R.string.key_arm_volt_pref), "0");
				command.append("echo " + value + " > " + "/sys/class/misc/customvoltage/max_arm_volt\n");
				command.append("echo " + armvolts + " > " + "/sys/class/misc/customvoltage/arm_volt\n");
			}else {
				// uv_mv_table
				status = preferences.getString(c.getString(R.string.key_uvmvtable_pref), "-1000");
				if(!status.equals("-1000")) {
					command.append("echo " + status + " > " + "/sys/devices/system/cpu/cpu0/cpufreq/UV_mV_table\n");
				}
			}
			// int voltages
			value = preferences.getInt(c.getString(R.string.key_max_int_volt), -1000);
			if(value > -1000) {
				String armvolts = preferences.getString(c.getString(R.string.key_int_volt_pref), "0");
				command.append("echo " + value + " > " + "/sys/class/misc/customvoltage/max_int_volt\n");
				command.append("echo " + armvolts + " > " + "/sys/class/misc/customvoltage/int_volt\n");
			}
		}
		// Audio
		value = preferences.getInt(c.getString(R.string.key_boeffla_sound_enable), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/boeffla_sound/boeffla_sound\n");
		}

		value = preferences.getInt(c.getString(R.string.key_boeffla_sound_dac_oversampling), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/boeffla_sound/dac_oversampling\n");
		}
		
		value = preferences.getInt(c.getString(R.string.key_boeffla_sound_speaker_tuning), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/boeffla_sound/speaker_tuning\n");
		}
		value = preferences.getInt(c.getString(R.string.key_boeffla_sound_headphone_volume), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/boeffla_sound/headphone_volume\n");
		}
		
		value = preferences.getInt(c.getString(R.string.key_boeffla_sound_speaker_volume), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/boeffla_sound/speaker_volume\n");
		}
		
		value = preferences.getInt(c.getString(R.string.key_boeffla_sound_fll_tuning), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/boeffla_sound/fll_tuning\n");
		}
		value = preferences.getInt(c.getString(R.string.key_boeffla_sound_dac_direct), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/boeffla_sound/dac_direct\n");
		}
		value = preferences.getInt(c.getString(R.string.key_boeffla_sound_mono_downmix), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/boeffla_sound/mono_downmix\n");
		}


		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_speaker_tuning), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/speaker_tuning\n");
		}
		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_speaker_offset), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/speaker_offset\n");
		}
		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_headphone_amplifier_level), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/headphone_amplifier_level\n");
		}
		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_stereo_expansion), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/stereo_expansion\n");
		if(value > 0) {
		    value = preferences.getInt(c.getString(R.string.key_voodoo_sound_stereo_expansion_gain), -1000);
		    if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/stereo_expansion_gain\n");
		    }
		}
		}
		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_headphone_eq), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/headphone_eq\n");
		   if(value > 0) {
        		for (int i = 0; i < AudioFragment.VOODOO_FILE_PATH.length; i++) {
            	   	value = preferences.getInt(AudioFragment.VOODOO_EQ_KEY[i], 0);
		   	command.append("echo " + value + " > " + AudioFragment.VOODOO_FILE_PATH[i] + "\n");
			}
        	   } else {
        		for (int i = 0; i < AudioFragment.VOODOO_FILE_PATH.length; i++) {
            	   	value = 0;
		   	command.append("echo " + value + " > " + AudioFragment.VOODOO_FILE_PATH[i] + "\n");
			}
		   }
		}
		
		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_fll_tuning), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/fll_tuning\n");
		}
		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_dac_osr128), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/dac_osr128\n");
		}
		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_adc_osr128), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/adc_osr128\n");
		}
		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_dac_direct), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/dac_direct\n");
		}
		value = preferences.getInt(c.getString(R.string.key_voodoo_sound_mono_downmix), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/virtual/misc/voodoo_sound/mono_downmix\n");
		}

		// BLD
		value = preferences.getInt(c.getString(R.string.key_bld_status), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/backlightdimmer/enabled\n");
			value = preferences.getInt(c.getString(R.string.key_bld_delay), -1000);
			if(value > -1000)
				command.append("echo " + value + " > " + "/sys/class/misc/backlightdimmer/delay\n");
		}
		
		// BLN
		value = preferences.getInt(c.getString(R.string.key_bln_status), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/backlightnotification/enabled\n");
			value = preferences.getInt(c.getString(R.string.key_bln_blink), -1000);
			if(value > -1000)
				command.append("echo " + value + " > " + "/sys/class/misc/backlightnotification/in_kernel_blink\n");
			value = preferences.getInt(c.getString(R.string.key_bln_blink_interval), -1000);
			if(value > -1000)
				command.append("echo " + value + " > " + "/sys/class/misc/backlightnotification/blink_interval\n");
			value = preferences.getInt(c.getString(R.string.key_bln_blink_count), -1000);
			if(value > -1000)
				command.append("echo " + value + " > " + "/sys/class/misc/backlightnotification/max_blink_count\n");
		}

		// BLX
		value = preferences.getInt(c.getString(R.string.key_blx_charging_limit), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/batterylifeextender/charging_limit\n");
		}
		
		// Deepidle
		value = preferences.getInt(c.getString(R.string.key_deepidle_status), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/deepidle/enabled\n");
		}

		// Idle Mode
		value = preferences.getInt(c.getString(R.string.key_idle_mode), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/module/cpuidle_exynos4/parameters/enable_mask\n");
		}

		// Smooth Ui
		value = preferences.getInt(c.getString(R.string.key_smooth_ui_enabled), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/shift_tweaks/smooth_ui_enabled\n");
		}

		// Dyn Fsync
		value = preferences.getInt(c.getString(R.string.key_dyn_fsync), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/kernel/dyn_fsync/Dyn_fsync_active\n");
		}

		// led control
		value = preferences.getInt(c.getString(R.string.key_led_fade), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/led/led_fade\n");
		}
		value = preferences.getInt(c.getString(R.string.key_led_intensity), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/led/led_intensity\n");
		}
		value = preferences.getInt(c.getString(R.string.key_led_speed), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/led/led_speed\n");
		}
		value = preferences.getInt(c.getString(R.string.key_led_slope_up_1), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/led/led_slope_up_1\n");
		}
		value = preferences.getInt(c.getString(R.string.key_led_slope_up_2), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/led/led_slope_up_2\n");
		}
		value = preferences.getInt(c.getString(R.string.key_led_slope_down_1), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/led/led_slope_down_1\n");
		}
		value = preferences.getInt(c.getString(R.string.key_led_slope_down_2), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/led/led_slope_down_2\n");
		}


		// backlight control
		value = preferences.getInt(c.getString(R.string.key_touch_led_handling), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/sec_touchkey/touch_led_handling\n");
		}
		value = preferences.getInt(c.getString(R.string.key_touch_led_on_screen_touch), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/sec_touchkey/touch_led_on_screen_touch\n");
		}

		// vibrator
		value = preferences.getInt(c.getString(R.string.key_vibration_kernelcontrol), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/pwm_duty/kernelcontrol\n");
		}
		if(value > 0) {
		    value = preferences.getInt(c.getString(R.string.key_vibration_intensity), -1000);
		    if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/pwm_duty/pwm_kernel_duty\n");
		    }
		}

		value = preferences.getInt(c.getString(R.string.key_vibration_intensity_smdk4412), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/vibrator/pwm_val\n");
		}
		
		// Touchwake
		value = preferences.getInt(c.getString(R.string.key_touchwake_status), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/touchwake/enabled\n");
			value = preferences.getInt(c.getString(R.string.key_touchwake_delay), -1000);
			if(value > -1000)
				command.append("echo " + value + " > " + "/sys/class/misc/touchwake/delay\n");
		}

		/* TouchBoost
		value = preferences.getInt(c.getString(R.string.key_touch_boost_enabled), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/touchboost/input_boost_enabled\n");
			value = preferences.getInt(c.getString(R.string.key_touch_boost_freq), -1000);
			if(value > -1000)
				command.append("echo " + value + " > " + "/sys/class/misc/touchboost/input_boost_freq\n");
		}*/
		
		// governor
		status = preferences.getString(c.getString(R.string.key_governor), "-1000");
		if(!status.equals("-1000")) {
			command.append("echo " + status + " > " + "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor\n");
		}
		// governor's parameters
		if(status.equals("lazy")) { // set this parameter only if active governor = lazy
			// lazy screenoff max freq
			value = preferences.getInt(c.getString(R.string.key_screenoff_maxfreq), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lazy/screenoff_maxfreq\n");
			}
		}else if(status.equals("ondemand")) { // set this parameter only if active governor = ondemand
			value = preferences.getInt(c.getString(R.string.key_ondemand_sampling_rate), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/ondemand/sampling_rate\n");
			}
			value = preferences.getInt(c.getString(R.string.key_ondemand_up_threshold), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/ondemand/up_threshold\n");
			}
			value = preferences.getInt(c.getString(R.string.key_ondemand_sampling_down_factor), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/ondemand/sampling_down_factor\n");
			}
			value = preferences.getInt(c.getString(R.string.key_ondemand_powersave_bias), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/ondemand/powersave_bias\n");
			}
			value = preferences.getInt(c.getString(R.string.key_ondemand_early_demand), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/ondemand/early_demand\n");
				if (value == 1) {
				value = preferences.getInt(c.getString(R.string.key_ondemand_grad_up_threshold), -1000);
				   if(value > -1000) {
					command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/ondemand/grad_up_threshold\n");
				   }
				}	
			}
			value = preferences.getInt(c.getString(R.string.key_ondemand_sleep_up_threshold), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/ondemand/sleep_up_threshold\n");
			}
			value = preferences.getInt(c.getString(R.string.key_ondemand_sleep_sampling_rate), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/ondemand/sleep_sampling_rate\n");
			}

		}else if(status.equals("conservative")) { // set this parameter only if active governor = conservative
		    value = preferences.getInt(c.getString(R.string.key_conservative_sampling_rate), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/sampling_rate\n");
			}
			value = preferences.getInt(c.getString(R.string.key_conservative_down_threshold), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/down_threshold\n");
			}
			value = preferences.getInt(c.getString(R.string.key_conservative_up_threshold), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/up_threshold\n");
			}
			value = preferences.getInt(c.getString(R.string.key_conservative_sampling_down_factor), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/sampling_down_factor\n");
			}
			value = preferences.getInt(c.getString(R.string.key_conservative_freq_step), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/freq_step\n");
			}
			value = preferences.getInt(c.getString(R.string.key_conservative_ignore_nice_load), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/ignore_nice_load\n");
			}

			value = preferences.getInt(c.getString(R.string.key_conservative_smooth_up_enabled), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/smooth_up_enabled\n");
				if (value == 1) {
				value = preferences.getInt(c.getString(R.string.key_conservative_smooth_up), -1000);
				   if(value > -1000) {
					command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/smooth_up\n");
				   }
				}	
			}
			value = preferences.getInt(c.getString(R.string.key_conservative_sleep_up_threshold), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/sleep_up_threshold\n");
			}
			value = preferences.getInt(c.getString(R.string.key_conservative_sleep_sampling_rate), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/conservative/sleep_sampling_rate\n");
			}

		}else if(status.equals("smartassV2")) { // set this parameter only if active governor = smartass2
			value = preferences.getInt(c.getString(R.string.key_smartass_awake_ideal_freq), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/smartass/awake_ideal_freq\n");
			}
			value = preferences.getInt(c.getString(R.string.key_smartass_sleep_ideal_freq), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/smartass/sleep_ideal_freq\n");
			}
			value = preferences.getInt(c.getString(R.string.key_smartass_sleep_wakeup_freq), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/smartass/sleep_wakeup_freq\n");
			}
			value = preferences.getInt(c.getString(R.string.key_smartass_min_cpu_load), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/smartass/min_cpu_load\n");
			}
			value = preferences.getInt(c.getString(R.string.key_smartass_max_cpu_load), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/smartass/max_cpu_load\n");
			}
			value = preferences.getInt(c.getString(R.string.key_smartass_ramp_down_step), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/smartass/ramp_down_step\n");
			}
			value = preferences.getInt(c.getString(R.string.key_smartass_ramp_up_step), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/smartass/ramp_up_step\n");
			}
			value = preferences.getInt(c.getString(R.string.key_smartass_down_rate_us), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/smartass/down_rate_us\n");
			}
			value = preferences.getInt(c.getString(R.string.key_smartass_up_rate_us), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/smartass/up_rate_us\n");
			}
		}else if(status.equals("interactive")) { // set this parameter only if active governor = interactive
			value = preferences.getInt(c.getString(R.string.key_interactive_go_hispeed_load), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/interactive/go_hispeed_load\n");
			}
			value = preferences.getInt(c.getString(R.string.key_interactive_target_loads), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/interactive/target_loads\n");
			}
			value = preferences.getInt(c.getString(R.string.key_interactive_hispeed_freq), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/interactive/hispeed_freq\n");
			}
			value = preferences.getInt(c.getString(R.string.key_interactive_min_sample_time), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/interactive/min_sample_time\n");
			}
			value = preferences.getInt(c.getString(R.string.key_interactive_timer_rate), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/interactive/timer_rate\n");
			}
		}else if(status.equals("lulzactive")) { // set this parameter only if active governor = lulzactive
			// lulzactive inc_cpu_load
			value = preferences.getInt(c.getString(R.string.key_lulzactive_inc_cpu_load), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactive/inc_cpu_load\n");
			}
	
			// lulzactive pump_up_step
			value = preferences.getInt(c.getString(R.string.key_lulzactive_pump_up_step), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactive/pump_up_step\n");
			}
	
			// lulzactive pump_down_step
			value = preferences.getInt(c.getString(R.string.key_lulzactive_pump_down_step), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactive/pump_down_step\n");
			}
			
			// lulzactive screen_off_min_step
			value = preferences.getInt(c.getString(R.string.key_lulzactive_screen_off_min_step), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactive/screen_off_min_step\n");
			}
			
			// lulzactive up_sample_time
			value = preferences.getInt(c.getString(R.string.key_lulzactive_up_sample_time), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactive/up_sample_time\n");
			}
			
			// lulzactive down_sample_time
			value = preferences.getInt(c.getString(R.string.key_lulzactive_down_sample_time), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactive/down_sample_time\n");
			}
		}else if(status.equals("lulzactiveq")) { // set this parameter only if active governor = lulzactiveq
			// lulzactiveq hispeed_freq
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hispeed_freq), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hispeed_freq\n");
			}

			// lulzactiveq inc_cpu_load
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_inc_cpu_load), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/inc_cpu_load\n");
			}
	
			// lulzactiveq pump_up_step
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_pump_up_step), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/pump_up_step\n");
			}
	
			// lulzactiveq pump_down_step
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_pump_down_step), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/pump_down_step\n");
			}
			
			// lulzactiveq screen_off_max_step
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_screen_off_max_step), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/screen_off_max_step\n");
			}
			
			// lulzactiveq up_sample_time
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_up_sample_time), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/up_sample_time\n");
			}
			
			// lulzactiveq down_sample_time
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_down_sample_time), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/down_sample_time\n");
			}

			// lulzactiveq cpu_up_rate
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_cpu_up_rate), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/cpu_up_rate\n");
			}

			// lulzactiveq cpu_down_rate
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_cpu_down_rate), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/cpu_down_rate\n");
			}

			// lulzactiveq ignore_nice_load
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_ignore_nice_load), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/ignore_nice_load\n");
			}

			// lulzactiveq max_cpu_lock
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_max_cpu_lock), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/max_cpu_lock\n");
			}

			// lulzactiveq min_cpu_lock
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_min_cpu_lock), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/min_cpu_lock\n");
			}

			// lulzactiveq hotplug_freq_1_1
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_freq_1_1), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_freq_1_1\n");
			}

			// lulzactiveq hotplug_freq_2_0
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_freq_2_0), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_freq_2_0\n");
			}

			// lulzactiveq hotplug_freq_2_1
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_freq_2_1), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_freq_2_1\n");
			}

			// lulzactiveq hotplug_freq_3_0
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_freq_3_0), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_freq_3_0\n");
			}

			// lulzactiveq hotplug_freq_3_1
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_freq_3_1), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_freq_3_1\n");
			}

			// lulzactiveq hotplug_freq_4_0
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_freq_4_0), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_freq_4_0\n");
			}

			// lulzactiveq hotplug_rq_1_1
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_rq_1_1), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_rq_1_1\n");
			}

			// lulzactiveq hotplug_rq_2_0
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_rq_2_0), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_rq_2_0\n");
			}

			// lulzactiveq hotplug_rq_2_1
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_rq_2_1), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_rq_2_1\n");
			}

			// lulzactiveq hotplug_rq_3_0
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_rq_3_0), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_rq_3_0\n");
			}

			// lulzactiveq hotplug_rq_3_1
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_rq_3_1), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_rq_3_1\n");
			}

			// lulzactiveq hotplug_rq_4_0
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_rq_4_0), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_rq_4_0\n");
			}

			// lulzactiveq hotplug_sampling_rate
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_sampling_rate), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_sampling_rate\n");
			}

			// lulzactiveq up_nr_cpus
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_up_nr_cpus), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/up_nr_cpus\n");
			}

			// lulzactiveq hotplug_lock
			value = preferences.getInt(c.getString(R.string.key_lulzactiveq_hotplug_lock), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/lulzactiveq/hotplug_lock\n");
			}

		}else if(status.equals("hotplug")) { // set this parameter only if active governor = hotplug
			value = preferences.getInt(c.getString(R.string.key_hotplug_up_threshold), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/hotplug/up_threshold\n");
			}

			value = preferences.getInt(c.getString(R.string.key_hotplug_down_threshold), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/hotplug/down_threshold\n");
			}

			value = preferences.getInt(c.getString(R.string.key_hotplug_sampling_rate), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/hotplug/sampling_rate\n");
			}

			value = preferences.getInt(c.getString(R.string.key_hotplug_ignore_nice_load), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/hotplug/ignore_nice_load\n");
			}

			value = preferences.getInt(c.getString(R.string.key_hotplug_io_is_busy), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/hotplug/io_is_busy\n");
			}

			value = preferences.getInt(c.getString(R.string.key_hotplug_down_differential), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/hotplug/down_differential\n");
			}

			value = preferences.getInt(c.getString(R.string.key_hotplug_in_sampling_periods), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/hotplug/hotplug_in_sampling_periods\n");
			}

			value = preferences.getInt(c.getString(R.string.key_hotplug_out_sampling_periods), -1000);
			if(value > -1000) {
				command.append("echo " + value + " > " + "/sys/devices/system/cpu/cpufreq/hotplug/hotplug_out_sampling_periods\n");
			}
		}

		// battery
		value = preferences.getInt(c.getString(R.string.key_dcp_ac_input_curr), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/platform/samsung-battery/dcp_ac_input_curr\n");
		}

		value = preferences.getInt(c.getString(R.string.key_dcp_ac_chrg_curr), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/platform/samsung-battery/dcp_ac_chrg_curr\n");
		}

		value = preferences.getInt(c.getString(R.string.key_sdp_input_curr), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/platform/samsung-battery/sdp_input_curr\n");
		}

		value = preferences.getInt(c.getString(R.string.key_sdp_chrg_curr), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/platform/samsung-battery/sdp_chrg_curr\n");
		}

		value = preferences.getInt(c.getString(R.string.key_cdp_input_curr), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/platform/samsung-battery/cdp_input_curr\n");
		}

		value = preferences.getInt(c.getString(R.string.key_cdp_chrg_curr), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/platform/samsung-battery/cdp_chrg_curr\n");
		}

		value = preferences.getInt(c.getString(R.string.key_batt_chrg_soft_volt), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/platform/samsung-battery/batt_chrg_soft_volt\n");
		}

		value = preferences.getInt(c.getString(R.string.key_ignore_stable_margin), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/platform/samsung-battery/ignore_stable_margin\n");
		}

		value = preferences.getInt(c.getString(R.string.key_ignore_unstable_power), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/devices/platform/samsung-battery/ignore_unstable_power\n");
		}


		// cmled
		value = preferences.getInt(c.getString(R.string.key_cmled_bltimeout), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/misc/notification/bl_timeout\n");
		}
		// cmled blink
		value = preferences.getInt(c.getString(R.string.key_cmled_blink), -1000);
		if(value > -1000) {
			// we must write blinktimeout before blink status
			// coz if we write blinktimeout it will reset blink status to enabled
			int timeout = preferences.getInt(c.getString(R.string.key_cmled_blinktimeout), -1000);
			if(timeout > -1000)
				command.append("echo " + timeout + ">" + "/sys/class/misc/notification/blinktimeout\n");
			
			command.append("echo " + value + " > " + "/sys/class/misc/notification/blink\n");
		}

		// gpu
        	for (String filePath : PerformanceFragment.GPU_CLOCK_FILE_PATH) {
            	status = preferences.getString(filePath, "-1000");
		if(!status.equals("-1000"))
		command.append("echo " + status + " > " + filePath + "\n");
        	}

		status = preferences.getString(c.getString(R.string.key_malivolt_pref), "-1000");
		if(!status.equals("-1000")) {
			command.append("echo " + status + " > " + "/sys/class/misc/mali_control/voltage_control\n");
		}
		
		// io scheduler
		status = preferences.getString(c.getString(R.string.key_iosched), "-1000");
		if(!status.equals("-1000")) {
			String[] ioscheds = c.getResources().getStringArray(R.array.iosched_interfaces);
			for(String i: ioscheds) {
				command.append("echo " + status + " > " + i + "\n");	
			}
		}
		
		// Liveoc target low
		int ocTargetLow = preferences.getInt(c.getString(R.string.key_liveoc_target_low), -1000);
		if(ocTargetLow > -1000) {
			command.append("echo " + ocTargetLow + " > " + "/sys/class/misc/liveoc/oc_target_low\n");
		}
		// Liveoc target high
		int ocTargetHigh = preferences.getInt(c.getString(R.string.key_liveoc_target_high), -1000);
		if(ocTargetHigh > -1000) {
			command.append("echo " + ocTargetHigh + " > " + "/sys/class/misc/liveoc/oc_target_high\n");
		}
		// Liveoc
		value = preferences.getInt(c.getString(R.string.key_liveoc), -1000);
		if(value > -1000 && value != 100) {
			// first make sure live oc at 100 then set cpu min and max freq
			command.append("echo 100 > " + "/sys/class/misc/liveoc/oc_value\n");
			// cpu minfreq
			int minFreq = preferences.getInt(c.getString(R.string.key_min_cpufreq), -1000);
			if(minFreq > -1000) {
				if(minFreq >= ocTargetLow) {
					minFreq = minFreq * 100 / value;
				}
				command.append("echo " + minFreq + " > " + "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			}
			// cpu maxfreq
			int maxFreq = preferences.getInt(c.getString(R.string.key_max_cpufreq), -1000);
			if(maxFreq > -1000) {
				if(minFreq <= ocTargetHigh) {
					maxFreq = maxFreq * 100 / value;
				}
				command.append("echo " + maxFreq + " > " + "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			}
			// now set liveoc value
			command.append("echo " + value + " > " + "/sys/class/misc/liveoc/oc_value\n");
		}else {
			// make sure liveoc at 100
			command.append("echo 100 > " + "/sys/class/misc/liveoc/oc_value\n");
			// cpu minfreq
			int minFreq = preferences.getInt(c.getString(R.string.key_min_cpufreq), -1000);
			if(minFreq > -1000) {
				command.append("echo " + minFreq + " > " + "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq\n");
			}
			
			// cpu maxfreq
			int maxFreq = preferences.getInt(c.getString(R.string.key_max_cpufreq), -1000);
			if(maxFreq > -1000) {
				command.append("echo " + maxFreq + " > " + "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq\n");
			}
		}

		// Touchkey backlight
		value = preferences.getInt(c.getString(R.string.key_touch_led_handling), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/sec_touchkey/touch_led_handling\n");
		}
		value = preferences.getInt(c.getString(R.string.key_touch_led_on_screen_touch), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/sec_touchkey/touch_led_on_screen_touch\n");
		}

		value = preferences.getInt(c.getString(R.string.key_touch_led_force_disable), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/sec_touchkey/force_disable\n");
		}
		value = preferences.getInt(c.getString(R.string.key_touch_led_timeout), -1000);
		if(value > -1000) {
			command.append("echo " + value + " > " + "/sys/class/sec/sec_touchkey/timeout\n");
		}

		return command.toString();
	}
	
	public static void deleteSettings(Context c, String preferenceName) {
		File destination = new File(c.getString(R.string.SETTINGS_DIR), preferenceName);
		destination.delete();
	}
	
	public static boolean saveSettings(Context c, String preferenceName) {
		boolean ret = false;
		File destDir = new File(c.getString(R.string.SETTINGS_DIR));
		if(!destDir.exists())
			destDir.mkdirs(); // create dir if not exists
		File destination = new File(destDir, preferenceName);

		if(!destination.exists()) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
	
			String command = buildCommand(c, preferences);
			
			try {
				FileWriter fw = new FileWriter(destination);
				fw.write("# v" + c.getString(R.string.app_version) + "\n");
				fw.write(command);
				fw.close();
				ret = true;
			}catch(IOException e) {
				Log.e(LOG_TAG, "", e);
			}
		}
		return ret;
	}
	
	private static void checkOldSavedFile(Context c, File source) {
		try {
			FileReader fr = new FileReader(source);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			String lastLine = null;
			String versionString = "# v" + c.getString(R.string.app_version);
			StringBuilder command = new StringBuilder(versionString);
			command.append("\n");
			boolean rewrite = false;
			if(line != null && !line.equals(versionString)) {
				rewrite = true;
				do {
					if(line.contains("scaling_min_freq") && !lastLine.contains("oc_value")) {
						command.append("echo 100 > " + "/sys/class/misc/liveoc/oc_value\n");
					}
					if(line != null) {
						command.append(line);
						command.append("\n");
					}
					lastLine = line;
					line = br.readLine();
				}while(line != null);
			}
			br.close();
			fr.close();
			if(rewrite) {
				FileWriter fw = new FileWriter(source);
				fw.write(command.toString());
				fw.close();
			}
		}catch(Exception ex) {
			Log.e(LOG_TAG, "", ex);
		}
	}
	
	/**
	 * 
	 * @param c
	 * @param preferenceName
	 * @return
	 */
	public static void loadSettings(Context c, String preferenceName) {
		File source = new File(c.getString(R.string.SETTINGS_DIR), preferenceName);
		checkOldSavedFile(c, source); // check old saved file
		StringBuilder command = new StringBuilder();
		try {
			FileReader fr = new FileReader(source);
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while(line != null) {
				command.append(line);
				command.append("\n");
				line = br.readLine();
			}
			br.close();
			fr.close();
		}catch(IOException e) {
			Log.e(LOG_TAG, "", e);
		}
		if(command.length() > 0) {
			SysCommand.getInstance().suRun(command.toString());
		}
	}
	
	/**
	 * this method called on boot completed
	 * 
	 * @param c
	 * @return
	 */
	public static int loadSettingsOnBoot(Context c) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
		
		// check 'set on boot' preference
		boolean restoreOnBoot = preferences.getBoolean(c.getString(R.string.key_restore_on_boot), true);
		boolean forceRestore = preferences.getBoolean(c.getString(R.string.key_force_restore_on_boot), false);
		if(!restoreOnBoot) {
			return ERR_SET_ON_BOOT_FALSE;
		}

		// now check current kernel version with saved value
		restoreOnBoot = false;
		SysCommand sysCommand = SysCommand.getInstance();
		if(sysCommand.readSysfs("/proc/version") > 0) {
			String kernel = sysCommand.getLastResult(0);
			String savedKernelVersion = preferences.getString(c.getString(R.string.key_kernel_version), null);
			if(kernel.equals(savedKernelVersion) || forceRestore) {
				restoreOnBoot = true;
			}
		}
		if(!restoreOnBoot) {
			return ERR_DIFFERENT_KERNEL;
		}
		String command = buildCommand(c, preferences);
		SysCommand.getInstance().suRun(command);
		SpeakerOffsetPreference.restore(c);
		return SUCCESS;
	}
}
