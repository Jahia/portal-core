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

    $scope.selectWidget = function (nodetype) {
        $scope.desiredWidget = nodetype;
    };

    $scope.addWidget = function () {
        if ($scope.desiredName.length > 0 && $scope.desiredWidget.length > 0) {
            portal.addNewWidget($scope.desiredWidget, $scope.desiredName);
            $scope.cancel();
        }
    };

    $scope.cancel = function () {
        $('#' + $scope.modalId).modal('hide');
        $scope.desiredName = "";
        $scope.desiredWidget = "";
    }
});

portalToolbar.controller('tabCtrl', function test($scope) {
    $scope.modalId = "";
    $scope.edit = false;
    $scope.form = [];

    $scope.init = function (type, modalId) {
        $scope.modalId = modalId;
        $scope.edit = (type && type == "edit");
        $('#' + modalId).on('show', function () {
            portal.getTabFormInfo(function (form) {
                $scope.$apply(function () {
                    $scope.form = form;
                    if(!$scope.edit){
                        form.name = "";
                    }
                });
            });
        });
    };

    $scope.cancel = function () {
        $('#' + $scope.modalId).modal('hide');
        $scope.form = [];
    };

    $scope.submit = function (isNew) {
        portal.saveTabForm($scope.form, function () {
            $scope.$apply(function () {
                $scope.form = [];
            });
        }, isNew);
    };
});

portalToolbar.controller('navCtrl', function test($scope) {
    $scope.canBeDeleted = false;
    $scope.tabs = [];

    $scope.loadTabs = function () {
        portal.getTabs(function (data) {
            $scope.$apply(function () {
                $scope.tabs = data;
                if($scope.tabs.length > 1){
                    $scope.canBeDeleted = true;
                }
            });
        });
    };

    $scope.isCurrentTab = function (url) {
        return url == window.location.pathname;
    };

    $scope.deleteTab = function(){
        if($scope.canBeDeleted){
            portal.deleteCurrentTab();
        }
    };

    $scope.copyModel = function(){
        portal.initPortalFromModel();
    }
});