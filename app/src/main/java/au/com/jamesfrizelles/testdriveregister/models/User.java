package au.com.jamesfrizelles.testdriveregister.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Koko on 26/08/2016.
 */
public class User implements Serializable {
    public String name;
    public String email;
    public String phone;
    public boolean enabled;
    public String role;
    public Map<String, Object> resume;

    public User(){

    }

    public User(String name, String email, String phone, String role, Map<String, Object> resume, boolean enabled){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.enabled = enabled;
        this.resume = resume;
        this.role = role;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("name", name);
        result.put("email", email);
        result.put("phone", phone);
        result.put("role", role);
        result.put("resume", resume);
        result.put("enabled", enabled);
        return result;
    }
}
