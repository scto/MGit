package com.xinglan.mgit.activities;

import android.os.Bundle;

import com.xinglan.android.activities.SheimiFragmentActivity;
import com.xinglan.mgit.fragments.SettingsFragment;

/**
 * Activity for user settings
 */
public class UserSettingsActivity extends SheimiFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
