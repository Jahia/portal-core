var googleFeedWidget = angular.module('googleFeedWidgetApp', []);


googleFeedWidget.controller('google-feed-view-ctrl', function ctrl($scope) {
    $scope.feedId = "";
    $scope.url = "";
    $scope.nbEntries = 10;

    $scope.init = function (feedId) {
        $scope.feedId = feedId;

        function initFeed(){
            if($scope.url){
                var feedControl = new google.feeds.FeedControl();
                feedControl.addFeed("http://www.digg.com/rss/index.xml");
                feedControl.setNumEntries($scope.nbEntries);
                feedControl.draw($("#" + $scope.feedId).find(".feeds").get(0));
            }
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
    $scope.widget = {};

    $scope.init = function(widgetId){
        $scope.widget = portal.getCurrentWidget(widgetId);
    };

    $scope.update = function(form){
        $scope.widget.performUpdate(form, function(data){
            $scope.widget.load();
        });
    };

    $scope.cancel = function(){
        $scope.widget.load();
    };
});
