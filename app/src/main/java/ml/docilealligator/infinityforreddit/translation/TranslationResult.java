package ml.docilealligator.infinityforreddit.translation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TranslationResult {
    @Nullable
    private final String translatedText;
    @Nullable
    private final String detectedSourceLanguage;
    @Nullable
    private final String errorMessage;
    private final boolean success;
    
    private TranslationResult(@Nullable String translatedText,
                             @Nullable String detectedSourceLanguage,
                             @Nullable String errorMessage,
                             boolean success) {
        this.translatedText = translatedText;
        this.detectedSourceLanguage = detectedSourceLanguage;
        this.errorMessage = errorMessage;
        this.success = success;
    }
    
    public static TranslationResult success(@NonNull String translatedText, @Nullable String detectedSourceLanguage) {
        return new TranslationResult(translatedText, detectedSourceLanguage, null, true);
    }
    
    public static TranslationResult error(@NonNull String errorMessage) {
        return new TranslationResult(null, null, errorMessage, false);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    @Nullable
    public String getTranslatedText() {
        return translatedText;
    }
    
    @Nullable
    public String getDetectedSourceLanguage() {
        return detectedSourceLanguage;
    }
    
    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }
}
