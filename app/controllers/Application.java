package controllers;

import play.*;
import play.data.validation.Required;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	User bob = User.find("byEmail", "bob@gmail.com").first();
    	new Post(bob,"New Post","Donec sed odio dui. Cras justo odio, dapibus ac facilisis in, egestas eget quam. Vestibulum id ligula porta felis euismod semper. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.");
    	Post mainPost = Post.q().order("-postedAt").first();
        List<Post> postList = Post.q().order("-postedAt").asList();
        //List<Post> postList = new ArrayList();
        render(mainPost,postList);
    }
    
    @Before
    static void addDefaults() {
        renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
        renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
    }
    
    public static void show(Long id) {
       Post post = Post.findById(id);
       render(post);
    }
    
    public static void postComment(Long postId,@Required String author, @Required String content) {
        Post post = Post.findById(postId);
        if (validation.hasErrors()) {
            render("Application/show.html", post);
        }
        post.addComment(author, content);
        flash.success("Thanks dude :)", author);
        show(postId);
    }
}