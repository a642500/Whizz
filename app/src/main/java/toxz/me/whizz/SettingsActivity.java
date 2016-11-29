package toxz.me.whizz;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices,
 * settings are presented as a single list. On tablets, settings are split by category, with
 * category headers shown to the left of the list of settings. <p> See <a
 * href="http://developer.android.com/design/patterns/settings.html"> Android Design: Settings</a>
 * for design guidelines and the
 * <a href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity implements SharedPreferences
        .OnSharedPreferenceChangeListener {
    private static final String TAG = "SettingsActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setForegroundTintList(ColorStateList.valueOf(Color.WHITE));
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(getTitle());

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        getFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new
                SettingsFragment()).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    public static class SettingsFragment extends PreferenceFragment {
        private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(final Preference preference, final Object
                            newValue) {
                        String stringValue = newValue.toString();

                        if (preference instanceof RingtonePreference) {
                            // For ringtone preferences, look up the correct display value
                            // using RingtoneManager.
                            if (TextUtils.isEmpty(stringValue)) {
                                // Empty values correspond to 'silent' (no ringtone).
                                preference.setSummary(R.string.pref_remind_ringtone_silent);
                            } else {
                                Ringtone ringtone = RingtoneManager.getRingtone(preference
                                        .getContext(), Uri.parse(stringValue));
                                if (ringtone == null) {
                                    // Clear the summary if there was a lookup error.
                                    preference.setSummary(null);
                                } else {
                                    // Set the summary to reflect the new ringtone display name.
                                    String name = ringtone.getTitle(preference.getContext());
                                    preference.setSummary(name);
                                }
                            }

                        }

                        return true;
                    }
                };

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            bindPreferenceSummaryToValue(this.findPreference(getString(R.string
                    .pref_key_remind_ringtone)));


            Preference versionPreference = findPreference(getString(R.string.pref_key_version));
            versionPreference.setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override public boolean onPreferenceClick(final Preference preference) {
                    Toast.makeText(getActivity(), "已经是最新版", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            findPreference(getString(R.string.pref_key_feedback)).setOnPreferenceClickListener
                    (new Preference.OnPreferenceClickListener() {
                        @Override public boolean onPreferenceClick(final Preference preference) {
                            Intent intent = preference.getIntent();
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(getActivity(), R.string
                                        .activity_settings_no_email_app, Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the preference's value
         * is changed, its summary (line of text below the preference title) is updated to reflect
         * the value. The summary is also immediately updated upon calling this method. The exact
         * display format is dependent on the type of preference.
         *
         * @see #sBindPreferenceSummaryToValueListener
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }
}
