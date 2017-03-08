package net.kjulio.torch.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import net.kjulio.torch.R;
import net.kjulio.torch.app.App;

public class SettingsActivity extends PreferenceActivity {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Enable notification
        ((App) getApplication()).setNotify(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Raise notification if pref_background is true and torch is actually on
        if (sharedPreferences.getBoolean("pref_background", true) &&
                sharedPreferences.getBoolean("mTorchButton", true)) {
            ((App) getApplication()).sendNotif();
        }
    }
}
