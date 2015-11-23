package visualharvester.storage;

import java.util.List;

import visualharvester.objects.Tweet;

public interface Storage {

	public void clearTweets(String collectionName);

	public void close();

	public void empty();

	public List<Tweet> getTweets(String collectionName);

	public void storeTweets(List<Tweet> tweets, String collectionName);

}
