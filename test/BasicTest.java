import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setup() {
        Fixtures.deleteDatabase();
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
        
        List<Post> testPostList = Post.find("byAuthor",testUser).fetch();
        
        assertEquals(testPostList.size(),1);
        
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
    	
    	assertEquals(Comment.count(),2);
    	
    	List<Comment> testCommentList = Comment.find("byPost", testPost).fetch();
    	
    	assertEquals(testCommentList.size(),2);
    	
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
     
        assertEquals(Post.count(),1);
        assertEquals(User.count(),1);
        assertEquals(Comment.count(),2);
        
        List commentList = bobPost.comments;
        
        assertEquals(commentList.size(),2);
        
        bobPost.delete();
        assertEquals(Post.count(),0);
        assertEquals(Comment.count(),0);
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
        List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
        assertEquals(2, bobPosts.size());
     
        // Find all comments related to bob's posts
        List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
        assertEquals(3, bobComments.size());
     
        // Find the most recent post
        Post frontPost = Post.find("order by postedAt desc").first();
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
