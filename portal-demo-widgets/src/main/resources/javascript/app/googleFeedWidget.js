// re use the wrapper app or create a new one
var googleFeedWidget = (portal && portal.widgetAppWrapper) ? portal.widgetAppWrapper : angular.module('widgetApp', []);

googleFeedWidget.controller('google-feed-view-ctrl', function ctrl($scope) {
    $scope.feedId = "";

    $scope.init = function(conf){
        $scope.feedId = conf.feedId;

        // Load a bunch of scripts and make sure the DOM is ready.
        $.when(
                $.getScript("https://www.google.com/jsapi"),

                // DOM ready deferred.
                //
                // NOTE: This returns a Deferred object, NOT a promise.
                $.Deferred(
                    function (deferred) {
                        // In addition to the script loading, we also
                        // want to make sure that the DOM is ready to
                        // be interacted with. As such, resolve a
                        // deferred object using the $() function to
                        // denote that the DOM is ready.
                        $(deferred.resolve);
                    }
                )
            ).done(
            function (/* Deferred Results */) {
                // The DOM is ready to be interacted with AND all
                // of the scripts have loaded. Let's test to see
                // that the scripts have loaded.
                if (google) {
                    google.load("feeds", "1", {'callback': function () {
                        var feedControl = new google.feeds.FeedControl();

                        // Add two feeds.
                        feedControl.addFeed("http://www.digg.com/rss/index.xml");
                        feedControl.addFeed("http://feeds.feedburner.com/Techcrunch", "TechCrunch");

                        // Draw it.
                        feedControl.draw($("#" + $scope.feedId).find(".feeds").get(0));
                    }});
                }
            }
        );
    };
});

googleFeedWidget.controller('google-feed-edit-ctrl', function test($scope) {

});