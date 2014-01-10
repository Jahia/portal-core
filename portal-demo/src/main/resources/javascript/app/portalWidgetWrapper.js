var portalWidgetWrapper = angular.module('widgetWrapper', []);

portalWidgetWrapper.controller('widgetCtrl', function test($scope, widget) {
    $scope.delete = function(){
        portal.deleteWidget(widget)
    }
});