package ml.docilealligator.infinityforreddit.savedpost;

import java.util.concurrent.Executor;

import ml.docilealligator.infinityforreddit.RedditDataRoomDatabase;

public class SavedPostUtils {
    
    public static void insertSavedPost(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                       String username, String postId, int type) {
        executor.execute(() -> {
            SavedPost savedPost = new SavedPost(username, postId, type);
            redditDataRoomDatabase.savedPostDao().insert(savedPost);
        });
    }

    public static void deleteSavedPost(RedditDataRoomDatabase redditDataRoomDatabase, Executor executor,
                                       String username, String postId) {
        executor.execute(() -> {
            redditDataRoomDatabase.savedPostDao().deleteSavedPost(postId, username);
        });
    }

    public static boolean isSavedLocally(RedditDataRoomDatabase redditDataRoomDatabase, String username, String postId) {
        SavedPost savedPost = redditDataRoomDatabase.savedPostDao().getSavedPost(postId, username);
        return savedPost != null;
    }
}
