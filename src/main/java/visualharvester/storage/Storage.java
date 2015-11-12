package visualharvester.storage;

import java.util.List;

import visualharvester.objects.Tweet;

public interface Storage {

	public void empty();

	public void storeTweets(List<Tweet> tweets);

}
