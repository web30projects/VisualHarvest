package visualharvester.service;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import visualharvester.objects.Tweet;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TweetList {

	List<Tweet> tweets;

	public TweetList() {

	}

	public TweetList(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public List<Tweet> getTweets() {
		if (tweets == null) {
			tweets = new ArrayList<>();
		}
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

}
