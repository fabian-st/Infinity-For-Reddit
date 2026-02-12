package ml.docilealligator.infinityforreddit.thing;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import ml.docilealligator.infinityforreddit.RedditDataRoomDatabase;
import ml.docilealligator.infinityforreddit.apis.RedditAPI;
import ml.docilealligator.infinityforreddit.savedpost.SavedPostUtils;
import ml.docilealligator.infinityforreddit.utils.APIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SaveThing {
    public static void saveThing(Retrofit oauthRetrofit, String accessToken, String fullname,
                                 SaveThingListener saveThingListener) {
        Map<String, String> params = new HashMap<>();
        params.put(APIUtils.ID_KEY, fullname);
        oauthRetrofit.create(RedditAPI.class).save(APIUtils.getOAuthHeader(accessToken), params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    saveThingListener.success();
                } else {
                    saveThingListener.failed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                saveThingListener.failed();
            }
        });
    }

    public static void unsaveThing(Retrofit oauthRetrofit, String accessToken, String fullname,
                                   SaveThingListener saveThingListener) {
        Map<String, String> params = new HashMap<>();
        params.put(APIUtils.ID_KEY, fullname);
        oauthRetrofit.create(RedditAPI.class).unsave(APIUtils.getOAuthHeader(accessToken), params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    saveThingListener.success();
                } else {
                    saveThingListener.failed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                saveThingListener.failed();
            }
        });
    }

    public static void saveThingLocally(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                        String username, String postId, int type, SaveThingListener saveThingListener) {
        SavedPostUtils.insertSavedPost(redditDataRoomDatabase, executor, username, postId, type);
        saveThingListener.success();
    }

    public static void unsaveThingLocally(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                          String username, String postId, SaveThingListener saveThingListener) {
        SavedPostUtils.deleteSavedPost(redditDataRoomDatabase, executor, username, postId);
        saveThingListener.success();
    }

    public interface SaveThingListener {
        void success();

        void failed();
    }
}
