package ml.docilealligator.infinityforreddit.translation;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeepLTranslator implements Translator {
    private static final String API_URL = "https://api-free.deepl.com/v2/translate";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient httpClient;
    private final Executor executor;
    private final Gson gson;
    private String apiKey;
    
    public DeepLTranslator(@NonNull OkHttpClient httpClient,
                          @NonNull Executor executor,
                          @NonNull Gson gson) {
        this.httpClient = httpClient;
        this.executor = executor;
        this.gson = gson;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    @Override
    public CompletableFuture<TranslationResult> translate(@NonNull String text,
                                                          @NonNull String sourceLanguage,
                                                          @NonNull String targetLanguage) {
        CompletableFuture<TranslationResult> future = new CompletableFuture<>();
        
        if (!isConfigured()) {
            future.complete(TranslationResult.error("DeepL API key not configured"));
            return future;
        }
        
        executor.execute(() -> {
            try {
                JsonObject json = new JsonObject();
                JsonArray textArray = new JsonArray();
                textArray.add(text);
                json.add("text", textArray);
                json.addProperty("target_lang", targetLanguage.toUpperCase());
                if (!"auto".equalsIgnoreCase(sourceLanguage)) {
                    json.addProperty("source_lang", sourceLanguage.toUpperCase());
                }
                
                String jsonString = gson.toJson(json);
                RequestBody body = RequestBody.create(jsonString, JSON);
                
                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Authorization", "DeepL-Auth-Key " + apiKey)
                        .post(body)
                        .build();
                
                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        future.complete(TranslationResult.error("Network error: " + e.getMessage()));
                    }
                    
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        try {
                            if (!response.isSuccessful()) {
                                future.complete(TranslationResult.error("API error: " + response.code()));
                                return;
                            }
                            
                            if (response.body() == null) {
                                future.complete(TranslationResult.error("Empty response body"));
                                return;
                            }
                            
                            String responseBody = response.body().string();
                            JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
                            
                            if (responseJson.has("translations")) {
                                JsonArray translations = responseJson.getAsJsonArray("translations");
                                if (translations.size() > 0) {
                                    JsonObject translation = translations.get(0).getAsJsonObject();
                                    String translatedText = translation.get("text").getAsString();
                                    String detectedLanguage = translation.has("detected_source_language")
                                            ? translation.get("detected_source_language").getAsString()
                                            : null;
                                    future.complete(TranslationResult.success(translatedText, detectedLanguage));
                                } else {
                                    future.complete(TranslationResult.error("No translation returned"));
                                }
                            } else {
                                future.complete(TranslationResult.error("Invalid response format"));
                            }
                        } catch (Exception e) {
                            future.complete(TranslationResult.error("Parse error: " + e.getMessage()));
                        } finally {
                            response.close();
                        }
                    }
                });
            } catch (Exception e) {
                future.complete(TranslationResult.error("Request error: " + e.getMessage()));
            }
        });
        
        return future;
    }
    
    @Override
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
    
    @Override
    public String getName() {
        return "DeepL";
    }
}
