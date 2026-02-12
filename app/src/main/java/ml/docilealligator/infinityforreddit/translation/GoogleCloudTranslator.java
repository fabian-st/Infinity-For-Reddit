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

public class GoogleCloudTranslator implements Translator {
    private static final String API_URL = "https://translation.googleapis.com/language/translate/v2";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient httpClient;
    private final Executor executor;
    private final Gson gson;
    private String apiKey;
    
    public GoogleCloudTranslator(@NonNull OkHttpClient httpClient,
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
            future.complete(TranslationResult.error("Google Cloud Translation API key not configured"));
            return future;
        }
        
        executor.execute(() -> {
            try {
                JsonObject json = new JsonObject();
                json.addProperty("q", text);
                json.addProperty("target", targetLanguage);
                if (!"auto".equals(sourceLanguage)) {
                    json.addProperty("source", sourceLanguage);
                }
                json.addProperty("format", "text");
                json.addProperty("key", apiKey);
                
                String jsonString = gson.toJson(json);
                RequestBody body = RequestBody.create(jsonString, JSON);
                
                Request request = new Request.Builder()
                        .url(API_URL)
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
                            
                            String responseBody = response.body().string();
                            JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
                            
                            if (responseJson.has("data")) {
                                JsonObject data = responseJson.getAsJsonObject("data");
                                JsonArray translations = data.getAsJsonArray("translations");
                                if (translations.size() > 0) {
                                    JsonObject translation = translations.get(0).getAsJsonObject();
                                    String translatedText = translation.get("translatedText").getAsString();
                                    String detectedLanguage = translation.has("detectedSourceLanguage")
                                            ? translation.get("detectedSourceLanguage").getAsString()
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
        return "Google Cloud Translation";
    }
}
