package eu.telecomnancy.tncyiot.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.List;

import eu.telecomnancy.tncyiot.R;

/**
 * Created by Pierre on 30/01/2017.
 */

public class PrefsActivity extends PreferenceActivity {

    PrefsFragment pf = new PrefsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, pf)
                .commit();

        //Get SP & add listener to it
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(spChanged);

        initPrefs(prefs);
    }

    @Override
    public void onBuildHeaders(List<Header> target)
    {
        //loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName)
    {
        return PrefsFragment.class.getName().equals(fragmentName);
    }

    public void initPrefs(SharedPreferences sp){
        getFragmentManager().executePendingTransactions();
        pf.findPreference("email").setSummary(sp.getString("email", "Please provide email"));
        pf.findPreference("username").setSummary(sp.getString("username", "Please provide username"));
    }

    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sp,
                                                      String key) {

                    Preference pref = pf.findPreference(key);
                    pref.setSummary(sp.getString(key, "Info"));
                }
            };
}
