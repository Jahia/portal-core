var portalToolbar = angular.module('portalToolbar', []);

portalToolbar.controller('widgetsCtrl', function ctrl($scope) {
    $scope.modalId = "";
    $scope.widgets = [];
    $scope.desiredName = "";
    $scope.desiredWidget = "";

    $scope.init = function (modalId) {
        $scope.modalId = modalId;
        $('#' + modalId).on('show', function () {
            portal.getWidgetTypes(function (widgets) {
                $scope.$apply(function () {
                    $scope.widgets = widgets;
                });
            });
        });

    };

    $scope.selectWidget = function(nodetype) {
        $scope.desiredWidget = nodetype;
    };

    $scope.addWidget = function() {
        if($scope.desiredName.length > 0 && $scope.desiredWidget.length > 0){
            portal.addNewWidget($scope.desiredWidget, $scope.desiredName);
            $scope.cancel();
        }
    };

    $scope.cancel = function() {
        $('#' + $scope.modalId).modal('hide');
        $scope.desiredName = "";
        $scope.desiredWidget = "";
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
    $scope.tabs = [];

    $scope.loadTabs = function(){
        portal.getTabs(function(data){
            $scope.$apply(function () {
                $scope.tabs = data;
            });
        });
    };

    $scope.isCurrentTab = function(path) {
        return portal.getCurrentTabPath() == path;
    }
});