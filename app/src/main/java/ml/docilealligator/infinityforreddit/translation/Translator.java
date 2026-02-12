package ml.docilealligator.infinityforreddit.translation;

import androidx.annotation.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Abstract interface for translation services
 */
public interface Translator {
    
    /**
     * Translate text from source language to target language
     * @param text Text to translate
     * @param sourceLanguage Source language code (e.g., "en", "es", "auto")
     * @param targetLanguage Target language code (e.g., "en", "es")
     * @return CompletableFuture with translation result
     */
    CompletableFuture<TranslationResult> translate(
            @NonNull String text,
            @NonNull String sourceLanguage,
            @NonNull String targetLanguage
    );
    
    /**
     * Check if the translator is configured and ready to use
     * @return true if configured, false otherwise
     */
    boolean isConfigured();
    
    /**
     * Get the name of this translator
     * @return Translator name
     */
    String getName();
}
