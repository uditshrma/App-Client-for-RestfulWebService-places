package tk.uditsharma.clientapp.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;



@AutoValue
public abstract class UserParcel implements Parcelable {
    public abstract String displayName();
    public abstract String userName();
    public abstract String password();
    public abstract String regDate();

    public static UserParcel create(String displayName, String userName, String password, String regDate) {
        return new AutoValue_UserParcel(displayName, userName, password, regDate);
    }

}
