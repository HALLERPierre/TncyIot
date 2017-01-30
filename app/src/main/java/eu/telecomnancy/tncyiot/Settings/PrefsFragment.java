package eu.telecomnancy.tncyiot.Settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import eu.telecomnancy.tncyiot.R;

/**
 * Created by Pierre on 30/01/2017.
 */

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PrefsFragment",
                "PrefsFragment called"
        );
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
