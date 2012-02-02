package models;

import java.util.*;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.*;
 
import play.data.validation.*;
import play.modules.morphia.Model;
import play.modules.morphia.Model.OnDelete;
 
@Entity
public class Post extends Model {
	
	@Required
    public String title;
    public Date postedAt;
    
    public String content;
    
    @Reference
    public User author;
    
    @Reference
    public List<Comment> comments;
    
    public Post(User author, String title, String content) {
    	this.comments = new ArrayList();
        this.author = author;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
    }
    
    public Post previous(){
    	Post previousPost = Post.q().filter("postedAt < ", this.postedAt).order("postedAt").first();
    	return previousPost == null ? this : previousPost;
    }
    
    public Post next(){
    	Post nextPost = Post.q().filter("postedAt > ", this.postedAt).order("postedAt").first();
    	return nextPost == null ? this : nextPost;
    }
    
    public Post addComment(String author, String content) {
        new Comment(this, author, content).save();
        return this;
    }
    
    @OnDelete void cascadeDelete() {
        for (Comment c: comments) {
            c.delete();
        }
    }

 
}