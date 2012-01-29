package models;
 
import java.util.*;
import com.google.code.morphia.annotations.*;
 
import play.modules.morphia.Model;
 
@Entity
public class Post extends Model {
 
    public String title;
    public Date postedAt;
    
    public String content;
    
    @Reference
    public User author;
    
    public List<Comment> comments;
    
    public Post(User author, String title, String content) {
    	this.comments = new ArrayList();
        this.author = author;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
    }
    
    public Post addComment(String author, String content) {
        Comment newComment = new Comment(this, author, content).save();
        this.comments.add(newComment);
        return this;
    }
 
}