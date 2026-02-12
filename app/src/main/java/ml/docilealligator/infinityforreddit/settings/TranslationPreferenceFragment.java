package ml.docilealligator.infinityforreddit.settings;

import android.os.Bundle;
import android.text.InputType;

import androidx.preference.EditTextPreference;

import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.customviews.preference.CustomFontPreferenceFragmentCompat;

public class TranslationPreferenceFragment extends CustomFontPreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.translation_preferences, rootKey);
        
        // Set API key fields to password mode
        EditTextPreference googleApiKeyPref = findPreference("google_translation_api_key");
        if (googleApiKeyPref != null) {
            googleApiKeyPref.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setSelection(editText.getText().length());
            });
            googleApiKeyPref.setSummaryProvider(preference -> {
                String value = ((EditTextPreference) preference).getText();
                if (value == null || value.isEmpty()) {
                    return getString(R.string.settings_google_translation_api_key_summary);
                }
                return "••••••••";
            });
        }
        
        EditTextPreference deeplApiKeyPref = findPreference("deepl_api_key");
        if (deeplApiKeyPref != null) {
            deeplApiKeyPref.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setSelection(editText.getText().length());
            });
            deeplApiKeyPref.setSummaryProvider(preference -> {
                String value = ((EditTextPreference) preference).getText();
                if (value == null || value.isEmpty()) {
                    return getString(R.string.settings_deepl_api_key_summary);
                }
                return "••••••••";
            });
        }
    }
}
