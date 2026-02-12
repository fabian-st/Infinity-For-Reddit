package ml.docilealligator.infinityforreddit.translation;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;

@Singleton
public class TranslatorProvider {
    private static final String PREF_TRANSLATOR_TYPE = "translator_type";
    private static final String PREF_GOOGLE_API_KEY = "google_translation_api_key";
    private static final String PREF_DEEPL_API_KEY = "deepl_api_key";
    private static final String PREF_TARGET_LANGUAGE = "translation_target_language";
    
    public static final String TYPE_NONE = "none";
    public static final String TYPE_GOOGLE = "google";
    public static final String TYPE_DEEPL = "deepl";
    
    private final SharedPreferences sharedPreferences;
    private final OkHttpClient httpClient;
    private final Executor executor;
    private final Gson gson;
    
    private Translator currentTranslator;
    
    @Inject
    public TranslatorProvider(@Named("default") SharedPreferences sharedPreferences,
                             @Named("base") OkHttpClient httpClient,
                             Executor executor,
                             Gson gson) {
        this.sharedPreferences = sharedPreferences;
        this.httpClient = httpClient;
        this.executor = executor;
        this.gson = gson;
        this.currentTranslator = createTranslator();
    }
    
    @NonNull
    public synchronized Translator getTranslator() {
        if (currentTranslator == null) {
            currentTranslator = createTranslator();
        }
        return currentTranslator;
    }

    public synchronized void refreshTranslator() {
        currentTranslator = createTranslator();
    }
    
    private Translator createTranslator() {
        String type = sharedPreferences.getString(PREF_TRANSLATOR_TYPE, TYPE_NONE);
        
        switch (type) {
            case TYPE_GOOGLE:
                GoogleCloudTranslator googleTranslator = new GoogleCloudTranslator(httpClient, executor, gson);
                String googleKey = sharedPreferences.getString(PREF_GOOGLE_API_KEY, "");
                googleTranslator.setApiKey(googleKey);
                return googleTranslator;
                
            case TYPE_DEEPL:
                DeepLTranslator deeplTranslator = new DeepLTranslator(httpClient, executor, gson);
                String deeplKey = sharedPreferences.getString(PREF_DEEPL_API_KEY, "");
                deeplTranslator.setApiKey(deeplKey);
                return deeplTranslator;
                
            case TYPE_NONE:
            default:
                return new NoOpTranslator();
        }
    }
    
    public String getTargetLanguage() {
        return sharedPreferences.getString(PREF_TARGET_LANGUAGE, "en");
    }
    
    public void setTargetLanguage(String language) {
        sharedPreferences.edit().putString(PREF_TARGET_LANGUAGE, language).apply();
    }
    
    public String getTranslatorType() {
        return sharedPreferences.getString(PREF_TRANSLATOR_TYPE, TYPE_NONE);
    }
    
    public void setTranslatorType(String type) {
        sharedPreferences.edit().putString(PREF_TRANSLATOR_TYPE, type).apply();
        refreshTranslator();
    }
    
    public String getGoogleApiKey() {
        return sharedPreferences.getString(PREF_GOOGLE_API_KEY, "");
    }
    
    public void setGoogleApiKey(String apiKey) {
        sharedPreferences.edit().putString(PREF_GOOGLE_API_KEY, apiKey).apply();
        refreshTranslator();
    }
    
    public String getDeepLApiKey() {
        return sharedPreferences.getString(PREF_DEEPL_API_KEY, "");
    }
    
    public void setDeepLApiKey(String apiKey) {
        sharedPreferences.edit().putString(PREF_DEEPL_API_KEY, apiKey).apply();
        refreshTranslator();
    }
}
