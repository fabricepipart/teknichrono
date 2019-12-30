

angular.module('frontend').controller('ReviewChronometerController', function ($scope, $interval, $routeParams, $location, flash, ChronometerResource, PingResource, BeaconResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;

    $scope.get = function () {
        var successCallback = function (data) {
            self.original = data;
            $scope.chronometer = new ChronometerResource(self.original);
        };
        var errorCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The chronometer could not be found.' });
            $location.path("/Chronometers");
        };
        ChronometerResource.get({ ChronometerId: $routeParams.ChronometerId }, successCallback, errorCallback);
    };

    $scope.update = function () {
        flash.setMessage({}, true);
        var successCallbackPing = function (data) {
            self.originalPing = data;
            $scope.latest = new PingResource(self.originalPing);
            if ($scope.latest) {
                $scope.latestBeacon = {}
                var successCallbackBeacon = function (data) {
                    self.originalBeacon = data;
                    $scope.latestBeacon = new BeaconResource(self.originalBeacon);
                };
                var errorCallbackBeacon = function () {
                    flash.setMessage({ 'type': 'error', 'text': 'The latest Beacon could not be found.' });
                    $location.path("/Chronometers");
                };
                BeaconResource.get({ BeaconId: $scope.latest.beacon.id }, successCallbackBeacon, errorCallbackBeacon);
            }
        };
        var errorCallbackPing = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The latest Ping could not be found.' });
            //$location.path("/Chronometers");
        };
        PingResource.latest({ chronoId: $routeParams.ChronometerId }, successCallbackPing, errorCallbackPing);

    };


    $scope.selectionStrategyList = [
        { key: "FIRST", short: "FIRST", text: "First ping" },
        { key: "HIGH", short: "HIGH", text: "Highest ping" },
        { key: "LAST", short: "LAST", text: "Last ping" },
        { key: "PROXIMITY", short: "PROXIMITY", text: "Closest beacon" }
    ];

    $scope.sendStrategyList = [
        { key: "ASYNC", short: "ASYNC", text: "Send in background" },
        { key: "NONE", short: "NONE", text: "Don't send" }
    ];

    $scope.orderToExecuteList = [
        { key: "UPDATE", short: "UPDATE", text: "Update" },
        { key: "RESTART", short: "RESTART", text: "Restart" }
    ];

    $scope.get();
    $scope.update();
    var refresher = $interval(function () {
        $scope.update();
    }, 2000);
    $scope.$on('$destroy', function () {
        $interval.cancel(refresher);
    });
});