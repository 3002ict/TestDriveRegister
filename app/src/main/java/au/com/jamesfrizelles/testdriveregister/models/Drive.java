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
    public String rego;
    public String address;
    public String make;
    public String model;
    public String status;
    public String start_drive;
    public String finish_drive;
    public String key;

    public Drive() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Drive(String drivername, String licence, String phone, String email, String userId, String username, String rego, String address, String make, String model, String start_drive, String finish_drive, String status) {
        this.drivername = drivername;
        this.licence = licence;
        this.phone = phone;
        this.email = email;
        this.userId = userId;
        this.username = username;
        this.rego = rego;
        this.address = address;
        this.make = make;
        this.model = model;
        this.start_drive = start_drive;
        this.finish_drive = finish_drive;
        this.status = status;

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
        result.put("rego", rego);
        result.put("address", address);
        result.put("make", make);
        result.put("model", model);
        result.put("start_drive", start_drive);
        result.put("finish_drive", finish_drive);
        result.put("status", status);

        return result;
    }

    public void setKey(String key){
        this.key = key;
    }

    public String getKey(){return this.key;}

}
