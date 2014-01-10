var portalToolbar = angular.module('portalToolbar', []);

portalToolbar.controller('widgetsCtrl', function ctrl($scope) {
    $scope.widgets = [];
    $scope.desiredName = "";
    $scope.showWidgetsMenu = false;

    $scope.toggleWidgetsMenu = function () {
        if(!$scope.showWidgetsMenu){
            $scope.showWidgetsMenu = true;

            portal.getWidgetTypes(function (widgets) {
                $scope.$apply(function () {
                    $scope.widgets = widgets;
                });
            });
        }else {
            $scope.showWidgetsMenu = false;
        }
    };

    $scope.addWidget = function(nodetype) {
        if($scope.desiredName.length > 0){
            portal.addNewWidget(nodetype, $scope.desiredName);
            $scope.showWidgetsMenu = false;
            $scope.desiredName = "";
        }
    }
});

portalToolbar.controller('tabCtrl', function test($scope) {
    $scope.showForm = false;
    $scope.form = [];

    $scope.toggle = function () {
        $scope.showForm = !$scope.showForm;
        if($scope.showForm){
            $scope.loadForm();
        }
    };

    $scope.loadForm = function() {
        portal.getTabFormInfo(function(form){
            $scope.$apply(function () {
                $scope.form = form;
            });
        });
    };

    $scope.cancel = function() {
        $scope.showForm = false;
    };

    $scope.save = function() {
        portal.saveTabForm($scope.form, function(){
            $scope.$apply(function () {
                $scope.form = [];
            });
        });

    }
});

portalToolbar.controller('navCtrl', function test($scope) {
    $scope.form = [];


});