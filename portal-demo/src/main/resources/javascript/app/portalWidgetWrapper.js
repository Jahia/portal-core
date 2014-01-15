var portalWidgetWrapper = angular.module('widgetApp', []);

portalWidgetWrapper.controller('widgetCtrl', function test($scope) {
    $scope._minimize = true;
    $scope.widget = [];

    $scope.init = function(widgetId){
        $scope.widget = portal.getCurrentWidget(widgetId);
    };

    $scope.delete = function(){
        portal.deleteWidget($scope.widget)
    };

    $scope.minimize = function(){
        $scope._minimize = !$scope._minimize;
        $("#"+$scope.widget._id).find(".widget-content").toggle();
    };

    $scope.switchEdit = function(){
        if($scope.widget.state != "edit"){
            $scope.widget.load("edit");
        } else {
            $scope.widget.load();
        }
    }
});

//push widget app to portal API
if(portal){
    portal.widgetAppWrapper = portalWidgetWrapper;
}