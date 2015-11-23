package visualharvester.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Tweet {

	String text = null;
	Long id = null;
	Location location = null;
	String tweetUrl = null;
	List<String> imageUrls = null;
	List<String> extractedEntities = null;

	public Tweet() {

	}

	public List<String> getExtractedEntities() {
		if (extractedEntities == null) {
			extractedEntities = new ArrayList<>();
		}
		return extractedEntities;
	}

	public Long getId() {
		return id;
	}

	public List<String> getImageUrls() {
		if (imageUrls == null) {
			imageUrls = new ArrayList<>();
		}
		return imageUrls;
	}

	public Location getLocation() {
		if (location == null) {
			location = new Location();
		}
		return location;
	}

	public String getText() {
		return text;
	}

	public String getTweetUrl() {
		return tweetUrl;
	}

	public void setExtractedEntities(List<String> extractedEntities) {
		this.extractedEntities = extractedEntities;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTweetUrl(String tweetUrl) {
		this.tweetUrl = tweetUrl;
	}

}
