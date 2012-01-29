
import play.*;
import play.jobs.*;
import play.test.*;
 
import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
	
	public String CLASS_NAME = this.getClass().getName();
 
    public void doJob() {
        // Check if the database is empty
        if(User.count() == 0) {
        	Logger.init();
        	Logger.info("Initialization...");
        	Logger.info("Loading sample data.");
            Fixtures.loadModels("initial-data.yml");
            Logger.info("Initialization done.");
            
        }
    }
 
}