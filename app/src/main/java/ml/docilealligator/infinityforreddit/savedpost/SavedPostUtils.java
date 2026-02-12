package ml.docilealligator.infinityforreddit.savedpost;

import ml.docilealligator.infinityforreddit.RedditDataRoomDatabase;

public class SavedPostUtils {
    
    public static void insertSavedPost(RedditDataRoomDatabase redditDataRoomDatabase,
                                       String username, String postId, int type) {
        SavedPost savedPost = new SavedPost(username, postId, type);
        redditDataRoomDatabase.savedPostDao().insert(savedPost);
    }

    public static void deleteSavedPost(RedditDataRoomDatabase redditDataRoomDatabase,
                                       String username, String postId) {
        redditDataRoomDatabase.savedPostDao().deleteSavedPost(postId, username);
    }
}
