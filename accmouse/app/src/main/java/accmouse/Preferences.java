package accmouse;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceScreen().findPreference("serverIP").setOnPreferenceClickListener(serverIPClickListener);
		getPreferenceScreen().findPreference("serverPort").setOnPreferenceClickListener(serverPortClickListener);
	}
	
	OnPreferenceClickListener serverIPClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {

			return true;
		}
	};

	OnPreferenceClickListener serverPortClickListener = new OnPreferenceClickListener() {
		public boolean onPreferenceClick(Preference preference) {

			return true;
		}
	};
}