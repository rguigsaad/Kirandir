package models;
 
import java.util.*;
import com.google.code.morphia.annotations.*;
 
import play.data.validation.Required;
import play.modules.morphia.Model;
import play.modules.morphia.Model.Added;

@Entity
public class Comment extends Model {
	
	@Required
    public String author;
	
    public Date postedAt;
     
    @Required
    public String content;
    
    @Reference
    public Post post;
    
    public Comment(Post post, String author, String content){
    	this.author = author;
    	this.post = post;
    	this.content = content;
    	this.postedAt = new Date();
    }
    
    @Added void cascadeAdd() {
        if (!post.comments.contains(this)) {
            post.comments.add(this);
            post.save();
        }
    }
        
}
