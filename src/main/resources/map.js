var map;
var raster;
var borderLayer = null;

var tweetList = {};
var intervalList = [];

$(function() {
	$('#submitButton').button().click(function(event) {
		event.preventDefault();
		var query = $('#queryField').val();
		var limit = $('#limitField').val();
		augmentTweets(query, limit);

	});
	$('#resetButton').button().click(function(event) {
		event.preventDefault();
		for (var i = 0; i < intervalList.length; i++) {
			var intervalId = intervalList[i];
			clearInterval(intervalId);
		}

		console.log(Object.keys(tweetList));
		var keys = Object.keys(tweetList);
		for (var j = 0; j < keys.length; j++) {
			var marker = tweetList[keys[j]];
			map.removeLayer(marker);
		}

		resetCollections();
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

function resetCollections() {

	$.ajax({
		method : 'get',
		url : '../rest/api/clear',
		success : function(data) {
			updateTweetList(data);
		}
	});

}

function augmentTweets(query, limit) {
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
		url : '../rest/api/augment/' + query + limit,
		success : function() {
			console.log("submitted");
		}
	});

	intervalList.push(setInterval(getTweets, 10000, query));
}

function getTweets(query) {
	$.ajax({
		method : 'get',
		url : '../rest/api/tweets/' + query,
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

		var content = tweet.text + "<br /><div class='scroll'>";
		if (tweet.imageUrls != undefined) {

			for (var j = 0; j < tweet.imageUrls.length; j++) {
				var line = "<img src='" + tweet.imageUrls[j] + "'>";
				content += line;
			}

			content += "</div>";
			content += "<br />ENTITIES:";

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
