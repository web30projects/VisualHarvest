package visualharvester.sources;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class StreamListener implements StatusListener {

	Logger log = Logger.getLogger(getClass());
	private List<Status> tweets;
	private final String queryString;
	private final boolean allowRetweets;

	public StreamListener(String queryString, boolean allowRetweets) {
		this.queryString = queryString;
		this.allowRetweets = allowRetweets;
	}

	public List<Status> getTweets() {
		if (tweets == null) {
			tweets = new ArrayList<>();
		}
		return tweets;
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		// Unused for application purposes; included to meet interface contract
	}

	@Override
	public void onException(Exception ex) {
		// Unused for application purposes; included to meet interface contract
	}

	@Override
	public void onScrubGeo(long userId, long upToStatusId) {
		// Unused for application purposes; included to meet interface contract
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		// Unused for application purposes; included to meet interface contract
	}

	@Override
	public void onStatus(Status status) {
		log.debug("Status Encountered");

		if (status.isRetweet() && !allowRetweets) {
			return;
		}

		if ((queryString == null) || queryString.isEmpty()) {
			getTweets().add(status);
		} else {
			if (status.getText().contains(queryString)) {
				getTweets().add(status);
			}
		}

		log.debug("Encountered " + getTweets().size() + " tweets");
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		// Unused for application purposes; included to meet interface contract
	}
}