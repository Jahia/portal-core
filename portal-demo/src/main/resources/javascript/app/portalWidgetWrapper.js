var portalWidgetWrapper = angular.module('widgetWrapper', []);

portalWidgetWrapper.controller('widgetCtrl', function test($scope, widget) {
    $scope._minimize = true;

    $scope.delete = function(){
        portal.deleteWidget(widget)
    };

    $scope.minimize = function(){
        $scope._minimize = !$scope._minimize;
        $("#"+widget._id).find("." + widget.contentClass).toggle();
    }
});