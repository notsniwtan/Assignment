import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class AuthSetup {
	public static Twitter setup(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("eE0Qf9NAH0nF1PdH24btvY6E0")
		  .setOAuthConsumerSecret("ilkdzUhMPtvDzAQbjJYElcHlo1u91pGfUz6ey5849XwEwstewk")
		  .setOAuthAccessToken("934520347-b53ZuwASVjG4I2Ck99DHhM8bouzjWgnX4MH8MxQB")
		  .setOAuthAccessTokenSecret("2YzoKIOLJGHFy5vqLypTOkN4q0CqGmkl9npWkw0n0q0UW");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		return twitter;
	}
}
