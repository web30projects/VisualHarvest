var map;
var raster;
var borderLayer = null;

var tweetList = {};


$(function() {
	
	$('#submitButton').button().click(function(event) {
		event.preventDefault();
		var query = $('#queryField').val();
		var limit = $('#limitField').val();
		fetchTweets(query, limit);

	});
	$('#resetButton').button().click(function(event) {
		event.preventDefault();
		console.log(Object.keys(tweetList));
		var keys = Object.keys(tweetList);
		for (var j = 0; j < keys.length; j++) {
			var marker = tweetList[keys[j]];
			map.removeLayer(marker);
		}
	});

	map = new L.map('map').setView([ 0.0, 0.0 ], 2);
	var nationalMapUrl = "../map/{z}/{y}/{x}";
	var nationalMapAttribution = "<a href='http:usgs.gov'>USGS</a> National Map Data";
	var nationalMap = new L.TileLayer(nationalMapUrl, {
		maxZoom : 18,
		attribution : nationalMapAttribution
	});
	nationalMap.addTo(map);
});



function fetchTweets(query, limit) {
	if (query == undefined || query == "") {
		return;
	}

	if (limit != undefined && limit != "") {
		limit = "/" + limit;
	} else {
		limit = "/20";
	}

	$.ajax({
		method : 'get',
		url : '../rest/api/fetch/' + query + limit,
		success : function(data) {
			updateTweetList(data);
		}
	});
}

function updateTweetList(data) {

	if (data == undefined || data.tweets == undefined) {
		return;
	}

	for (var i = 0; i < data.tweets.length; i++) {
		var tweet = data.tweets[i];

		if (tweetList[tweet.id] != undefined) {
			continue;
		}

		var lat = tweet.location.latitude;
		var lon = tweet.location.longitude;
		var marker = L.marker([ lat, lon ]);

		console.log(tweet.tweetUrl);
		var content = "<p><a target='_blank' href='https://twitter.com/statuses/"
				+ tweet.id + "'>Tweet URL</a>";

		content += "<p><a target='_blank' href='" + tweet.tweetUrl
				+ "'>Target URL</a>";

		content += "<br />" + tweet.text;
		if (tweet.imageUrls != undefined && tweet.imageUrls.length > 0) {
			content += "<br /><div class='scroll'>";
			for (var j = 0; j < tweet.imageUrls.length; j++) {
				var line = "<a target='_blank' href='" + tweet.imageUrls[j]
						+ "'>";
				line += "<img src='" + tweet.imageUrls[j] + "'>";
				line += "</a>"
				content += line;
			}
			content += "</div>";
		}

		if (tweet.extractedEntities.length > 0) {
			content += "<p>ENTITIES:";
			for (var k = 0; k < tweet.extractedEntities.length; k++) {
				var line = "<p>" + tweet.extractedEntities[k] + "</p>";
				content += line;
			}
		}

		marker.bindPopup(content);
		map.addLayer(marker);
		tweetList[tweet.id] = marker;
	}

}
