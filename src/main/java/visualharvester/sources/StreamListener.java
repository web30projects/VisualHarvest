package visualharvester.sources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class StreamListener implements StatusListener {

	List<Status> tweets;
	String queryString;

	public StreamListener(String queryString) {
		this.tweets = new ArrayList<>();
		this.queryString = queryString;
	}

	public List<Status> getTweets() {
		return tweets;
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onException(Exception ex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStallWarning(StallWarning warning) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatus(Status status) {
		final Date currentTime = new Date();
		encountered++;
		if (status.getText().contains(queryString)) {
			count++;
			tweets.add(status);
		}

		final long elapsed = currentTime.getTime() - startTime.getTime();

		final double remaining = (elapsed * 1.0) / (duration * 1.0);
		final Double percentage = remaining * 100.0;
		System.out.println("Tweets encountered: " + encountered + "\t Tweets Matched: " + count + "\t"
				+ format.format(percentage) + "% remaining");
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		// TODO Auto-generated method stub

	}

}