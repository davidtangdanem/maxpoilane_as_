package com.menadinteractive.maxpoilane;


import com.menadinteractive.maxpoilane.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
public class prefs_authent extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.prefs_authent);
    }   
}