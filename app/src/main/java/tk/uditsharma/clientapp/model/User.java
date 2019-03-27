package tk.uditsharma.clientapp.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User implements Serializable {

    private static final long serialVersionUID = -1384421966394028852L;
    private String name;
    private String userName;
    private String password;
    private Timestamp regDate;

    public User(String name, String userName, String password, Timestamp regDate) {
        super();
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.regDate = regDate;
    }

    public User() {
        super();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRegDateString() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(regDate.getTime()));
    }
    public Timestamp getRegDate() {
        return regDate;
    }
    public void setRegDate(Timestamp regDate) {
        this.regDate = regDate;
    }


}
