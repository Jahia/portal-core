// re use the wrapper app or create a new one
var googleMapWidget;
try {
    // reuse the same app
    googleMapWidget = angular.module('widgetApp');
} catch (e) {
    // instantiate a new app
    googleMapWidget = angular.module('widgetApp', []);
}

function mapApiLoaded(){
    window.map_api_loaded = true;
}

googleMapWidget.controller('google-map-view-ctrl', function ctrl($scope) {
    $scope.widget = [];

    $scope.init = function (widgetId, canvasId) {
        $scope.widget = portal.getCurrentWidget(widgetId);

        // Do not load the scripts twice
        $(document).ready(function(){

            // Hack to be able to load only one time the google map API
            (function wait() {
                if ((typeof window.map_api_loaded !== 'undefined') && window.map_api_loaded) {
                    new google.maps.Map(document.getElementById(canvasId), {
                        zoom: 8,
                        center: new google.maps.LatLng(-34.397, 150.644)
                    });
                } else {
                    if((typeof window.map_api_loading === 'undefined')){
                        $.getScript("http://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false&callback=mapApiLoaded");
                    }
                    window.map_api_loading = true;
                    setTimeout( wait, 1000 );
                }
            })();

        });

    }
});