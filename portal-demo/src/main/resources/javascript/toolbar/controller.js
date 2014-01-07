function ToolbarCtrl($scope) {
    $scope.widgets = [];
    $scope.desiredName = "";
    $scope.showWidgetsMenu = false;

    $scope.openWidgetsMenu = function () {
        $scope.showWidgetsMenu = true;

        portal.getWidgetTypes(function (widgets) {
            $scope.$apply(function () {
                $scope.widgets = widgets;
            });
        });
    };

    $scope.addWidget = function(nodetype) {
        if($scope.desiredName.length > 0){
            portal.addNewWidget(nodetype, $scope.desiredName);
            $scope.showWidgetsMenu = false;
            $scope.desiredName = "";
        }
    }
}