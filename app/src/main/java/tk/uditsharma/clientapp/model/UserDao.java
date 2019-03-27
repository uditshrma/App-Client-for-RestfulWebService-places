package tk.uditsharma.clientapp.model;

public class UserDao {

    //static ArrayList<User> userList = new ArrayList<User>();
    //static User cUser;
    private static String currentUser;
    private static String currentName;
    private static String token;
    //private static Context mContext;

    public static String getCurrentUser() {
        return currentUser;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        UserDao.token = token;
    }

    public static void setCurrentUser(String currentUser) {
        UserDao.currentUser = currentUser;
    }

    public static String getCurrentName() {
        return currentName;
    }

    public static void setCurrentName(String currentName) {
        UserDao.currentName = currentName;
    }
}
