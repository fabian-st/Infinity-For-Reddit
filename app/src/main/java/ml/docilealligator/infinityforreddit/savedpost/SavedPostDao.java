package ml.docilealligator.infinityforreddit.savedpost;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface SavedPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SavedPost savedPost);

    @Query("SELECT * FROM saved_posts WHERE username = :username AND type = :type ORDER BY time DESC")
    ListenableFuture<List<SavedPost>> getAllSavedPostsListenableFuture(String username, int type);

    @Query("SELECT * FROM saved_posts WHERE username = :username AND type = :type ORDER BY time DESC")
    List<SavedPost> getAllSavedPosts(String username, int type);

    @Query("SELECT * FROM saved_posts WHERE id = :id AND username = :username LIMIT 1")
    SavedPost getSavedPost(String id, String username);

    @Query("DELETE FROM saved_posts WHERE id = :id AND username = :username")
    void deleteSavedPost(String id, String username);

    @Query("DELETE FROM saved_posts WHERE username = :username")
    void deleteAllSavedPosts(String username);

    @Query("DELETE FROM saved_posts")
    void deleteAllSavedPosts();

    @Query("SELECT id FROM saved_posts WHERE id IN (:ids) AND username = :username")
    List<String> getSavedPostsIdsByIds(List<String> ids, String username);
}
