package ml.docilealligator.infinityforreddit.savedpost;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import ml.docilealligator.infinityforreddit.account.Account;

@Entity(tableName = "saved_posts", primaryKeys = {"username", "id"},
        foreignKeys = @ForeignKey(entity = Account.class, parentColumns = "username",
                childColumns = "username", onDelete = ForeignKey.CASCADE))
public class SavedPost implements Parcelable {
    @NonNull
    @ColumnInfo(name = "username")
    private String username;
    @NonNull
    @ColumnInfo(name = "id")
    private String id;
    @ColumnInfo(name = "time")
    private long time;
    @ColumnInfo(name = "type")
    private int type; // 0 for post, 1 for comment

    public static final int TYPE_POST = 0;
    public static final int TYPE_COMMENT = 1;

    public SavedPost(@NonNull String username, @NonNull String id, int type) {
        this.username = username;
        this.id = id;
        this.type = type;
        this.time = System.currentTimeMillis();
    }

    protected SavedPost(Parcel in) {
        username = in.readString();
        id = in.readString();
        time = in.readLong();
        type = in.readInt();
    }

    public static final Creator<SavedPost> CREATOR = new Creator<SavedPost>() {
        @Override
        public SavedPost createFromParcel(Parcel in) {
            return new SavedPost(in);
        }

        @Override
        public SavedPost[] newArray(int size) {
            return new SavedPost[size];
        }
    };

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(id);
        parcel.writeLong(time);
        parcel.writeInt(type);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SavedPost) {
            return ((SavedPost) obj).id.equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
