package xyz.realms.mgit.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import androidx.core.app.TaskStackBuilder;

import java.io.File;

import xyz.realms.mgit.MGitApplication;
import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.RepoListActivity;
import xyz.realms.mgit.ui.preference.PreferenceHelper;
import xyz.realms.mgit.ui.utils.BasicFunctions;

public class SettingsFragment extends PreferenceFragment {
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to set as for historical reasons SGit uses custom prefs file
        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(getString(R.string.preference_file_key));
        prefMgr.setSharedPreferencesMode(Context.MODE_PRIVATE);

        // /storage/emulated/0/Documents
        PreferenceHelper prefs =
            ((MGitApplication) this.getContext().getApplicationContext()).getPrefenceHelper();
        if (prefs.getRepoRoot() == null || prefs.getRepoRoot().toString().isEmpty()) {
            File documentsDir = new File(Environment.getExternalStorageDirectory(), "Documents");
            Repo.setLocalRepoRoot(this.getContext(), documentsDir);
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final String themePrefKey = getString(R.string.pref_key_use_theme_id);
        final String gravatarPrefKey = getString(R.string.pref_key_use_gravatar);
        final String useEnglishPrefKey = getString(R.string.pref_key_use_english);

        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (themePrefKey.equals(key) || useEnglishPrefKey.equals(key)) {
                    // nice trick to recreate the back stack, to ensure existing activities onCreate() are
                    // called to set new theme, courtesy of: http://stackoverflow.com/a/28799124/85472
                    TaskStackBuilder.create(getActivity())
                        .addNextIntent(new Intent(getActivity(), RepoListActivity.class))
                        .addNextIntent(getActivity().getIntent())
                        .startActivities();
                } else if (gravatarPrefKey.equals(key)) {
                    BasicFunctions.getImageLoader().clearMemoryCache();
                    BasicFunctions.getImageLoader().clearDiskCache();
                }
            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
    }
}
