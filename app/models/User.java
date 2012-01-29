package models;
 
import java.util.*;
import com.google.code.morphia.annotations.*;
 
import play.modules.morphia.Model;
 
@Entity
public class User extends Model {
 
    public String email;
    public String password;
    public String fullName;
    public boolean isAdmin;
    
    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullName = fullname;
    }

    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }
 
}