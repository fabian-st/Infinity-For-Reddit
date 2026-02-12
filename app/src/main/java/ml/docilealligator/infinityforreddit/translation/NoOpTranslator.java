package ml.docilealligator.infinityforreddit.translation;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * No-op translator used when no translation backend is configured
 */
public class NoOpTranslator implements Translator {
    
    @Override
    public CompletableFuture<TranslationResult> translate(@NonNull String text,
                                                          @NonNull String sourceLanguage,
                                                          @NonNull String targetLanguage) {
        return CompletableFuture.completedFuture(
                TranslationResult.error("No translation backend configured")
        );
    }
    
    @Override
    public boolean isConfigured() {
        return false;
    }
    
    @Override
    public String getName() {
        return "None";
    }
}
