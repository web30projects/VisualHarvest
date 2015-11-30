package visualharvester.storage;

import java.util.List;

import visualharvester.objects.Tweet;

public interface Storage {

	public void clearTweets(String criteria);

	public void close();

	public void empty();

	public List<Tweet> getTweets(String criteria);

	public void storeTweets(List<Tweet> tweets, String criteria);

	public void insertTweet(Tweet tweet, String criteria);

}
