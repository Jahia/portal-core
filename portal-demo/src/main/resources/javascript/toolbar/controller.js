function ToolbarCtrl($scope) {
    $scope.widgets = [];
    $scope.selectedWidgets = [];
    $scope.showWidgetsMenu = false;

    $scope.openWidgetsMenu = function () {
        $scope.showWidgetsMenu = true;

        portal.getWidgets(function (widgets) {
            $scope.$apply(function () {
                $scope.widgets = widgets;
            });
        });
    };

    $scope.selectWidget = function selectWidget(widgetName) {
        var idx = $scope.selectedWidgets.indexOf(widgetName);

        // is currently selected
        if (idx > -1) {
            $scope.selectedWidgets.splice(idx, 1);
        }

        // is newly selected
        else {
            $scope.selectedWidgets.push(widgetName);
        }
    };

    $scope.addWidgets = function() {
        if($scope.selectedWidgets.length > 0){
            portal.addWidgets($scope.selectedWidgets);
        }

        $scope.showWidgetsMenu = false;
        $scope.selectedWidgets = [];
    }
}