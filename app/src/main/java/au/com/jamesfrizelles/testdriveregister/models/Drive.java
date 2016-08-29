package au.com.jamesfrizelles.testdriveregister.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Koko on 26/08/2016.
 */
public class Drive implements Serializable {
    public String drivername;
    public String licence;
    public String phone;
    public String email;
    public String userId;
    public String username;

    public Drive() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Drive(String drivername, String licence, String phone, String email, String userId, String username) {
        this.drivername = drivername;
        this.licence = licence;
        this.phone = phone;
        this.email = email;
        this.userId = userId;
        this.username = username;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("drivername", drivername);
        result.put("licence", licence);
        result.put("phone", phone);
        result.put("email", email);
        result.put("username", username);
        result.put("userId", userId);
        return result;
    }

}
