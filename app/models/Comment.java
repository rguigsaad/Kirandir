package models;
 
import java.util.*;
import com.google.code.morphia.annotations.*;
 
import play.modules.morphia.Model;

@Entity
public class Comment extends Model {

	@Reference
    public String author;
    public Date postedAt;
     
    public String content;
    
    public Post post;
    
    public Comment(Post post, String author, String content){
    	this.author = author;
    	this.post = post;
    	this.content = content;
    	this.postedAt = new Date();
    }
}
