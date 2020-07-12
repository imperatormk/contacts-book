package com.p.contactsbook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

import com.p.contactsbook.ui.main.MainFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    public void updateResourcesLegacy(Activity context, String language, String country) {
        Locale locale = new Locale(language, country);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        context.getBaseContext().getResources().updateConfiguration(configuration, context.getBaseContext().getResources().getDisplayMetrics());

        recreate();
    }
}
