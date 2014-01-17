// re use the wrapper app or create a new one
var googleFeedWidget;
try {
    // reuse the same app
    googleFeedWidget = angular.module('widgetApp');
} catch (e) {
    // instantiate a new app
    googleFeedWidget = angular.module('widgetApp', []);
}

googleFeedWidget.controller('google-feed-view-ctrl', function ctrl($scope) {
    $scope.feedId = "";

    $scope.init = function (conf) {
        $scope.feedId = conf.feedId;

        function initFeed(){
            var feedControl = new google.feeds.FeedControl();

            // Add two feeds.
            feedControl.addFeed("http://www.digg.com/rss/index.xml");
            feedControl.addFeed("http://feeds.feedburner.com/Techcrunch", "TechCrunch");

            // Draw it.
            feedControl.draw($("#" + $scope.feedId).find(".feeds").get(0));
        }

        // Do not load the scripts twice
        if((typeof google === 'undefined') || (typeof google.feeds === 'undefined')){
            $.getScript("https://www.google.com/jsapi").done(function () {
                google.load("feeds", "1", {'callback': initFeed});
            });
        }else {
            initFeed();
        }

    };
});

googleFeedWidget.controller('google-feed-edit-ctrl', function test($scope) {

});