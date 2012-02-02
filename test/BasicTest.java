import org.junit.*;

import com.google.code.morphia.Key;

import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
        MorphiaFixtures.deleteDatabase();
    }
    	
    @Test
    public void testUserCreation() {
        
    	new User("test@gmail.com", "mdp", "testUserCreation").save();
    	
    	User testUser = User.find("byEmail","test@gmail.com").first();
    	
        assertNotNull(testUser);
        assertEquals("testUserCreation", testUser.fullName);
    }
    
    @Test
    public void testUserConnection() {
        
    	new User("test2@gmail.com", "mdp", "testUserConnection").save();

        assertNotNull(User.connect("test2@gmail.com", "mdp"));
        assertNull(User.connect("bob@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
    }
    
    @Test
    public void testPostCreation() {
    	
    	User testUser =new User("testAuthor@gmail.com","mdp","Test Author").save();
    	
    	new Post(User.connect("testAuthor@gmail.com","mdp"), "test Article", "My first content").save();
    	
        assertEquals(1, Post.count());
        
        List<Post> testPostList = Post.find("byAuthor",testUser).asList();
        
        assertEquals(1,testPostList.size());
        
        Post testPost = testPostList.get(0);
    	       
    	assertNotNull(testPost);
    	assertEquals(testPost.title,"test Article");
    	assertEquals(testPost.content,"My first content");
    	assertNotNull(testPost.postedAt);
    }
    
    @Test
    public void testCommentCreation(){
    	
    	User testUser = new User("testUser@gmail.com","mdp","test User").save();
    	
    	Post testPost = new Post(testUser,"test Post","test Content").save();
    	
    	new Comment(testPost, "test writter", "My Test Comment").save();
    	new Comment(testPost, "test writter2", "My Test Comment2").save();
    	
    	assertEquals(2,Comment.count());
    	
    	List<Comment> testCommentList = Comment.find("byPost", testPost).asList();
    	
    	assertEquals(2,testCommentList.size());
    	
    	Comment testComment = testCommentList.get(0);
    	
    	assertNotNull(testComment);
    	assertEquals(testComment.author,"test writter");
    	assertEquals(testComment.content,"My Test Comment");
    	assertNotNull(testPost.postedAt);
    	
    	testComment = testCommentList.get(1);
    	
    	assertNotNull(testComment);
    	assertEquals(testComment.author,"test writter2");
    	assertEquals(testComment.content,"My Test Comment2");
    	assertNotNull(testPost.postedAt);
    	
    }
    
    @Test
    public void useTheCommentsRelation() {
        // Create a new user and save it
        User bob = new User("bob@gmail.com", "secret", "Bob").save();
     
        // Create a new post
        Post bobPost = new Post(bob, "My first post", "Hello world").save();
     
        // Post a first comment
        bobPost.addComment("Jeff", "Nice post");
        bobPost.addComment("Tom", "I knew that !");
     
        assertEquals(1,Post.count());
        assertEquals(1,User.count());
        assertEquals(2,Comment.count());
        
        List commentList = bobPost.comments;
        
        assertEquals(2,commentList.size());
        
        bobPost.delete();
        assertEquals(0,Post.count());
        assertEquals(0,Comment.count());
    }
    
    @Test
    public void previousAndNextPostTest(){
    	Fixtures.loadModels("data.yml");
    	
    	Post firstPost = Post.q().order("postedAt").first();
    	assertEquals("Just a test of YABE",firstPost.title);
    	
    	Post secondPost = firstPost.next();
    	assertEquals("The MVC application",secondPost.title);
    	
    	assertEquals("Just a test of YABE",secondPost.previous().title);
    }
    
    @Test
    public void fullTest() {
        Fixtures.loadModels("data.yml");
     
        // Count things
        assertEquals(2, User.count());
        assertEquals(3, Post.count());
        assertEquals(3, Comment.count());
     
        // Try to connect as users
        assertNotNull(User.connect("bob@gmail.com", "secret"));
        assertNotNull(User.connect("jeff@gmail.com", "secret"));
        assertNull(User.connect("jeff@gmail.com", "badpassword"));
        assertNull(User.connect("tom@gmail.com", "secret"));
     
        // Find all bob's posts
        User bob = User.find("byEmail","bob@gmail.com").first();
        List<Post> bobPosts = Post.find("byAuthor", bob).asList();
        assertEquals(2, bobPosts.size());
     
        // Find all comments related to bob's posts
        List<Key<Post>> bobPosts2 = Post.find("byAuthor", bob).asKeyList();
        List<Comment> bobComments = Comment.q().filter("post in", bobPosts2).asList();
        assertEquals(3, bobComments.size());
     
        // Find the most recent post
        Post frontPost = Post.q().order("-postedAt").first();
        assertNotNull(frontPost);
        assertEquals("About the model layer", frontPost.title);
     
        // Check that this post has two comments
        assertEquals(2, frontPost.comments.size());
     
        // Post a new comment
        frontPost.addComment("Jim", "Hello guys");
        assertEquals(3, frontPost.comments.size());
        assertEquals(4, Comment.count());
    }

}
