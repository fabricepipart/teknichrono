

angular.module('frontend').controller('EditChronometerController', function ($scope, $routeParams, $location, flash, ChronometerResource, PingResource, SessionResource) {
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

    $scope.isClean = function () {
        return angular.equals(self.original, $scope.chronometer);
    };

    $scope.save = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'success', 'text': 'The chronometer was updated successfully.' }, true);
            $scope.get();
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.chronometer.$update(successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Chronometers");
    };

    $scope.remove = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The chronometer was deleted.' });
            $location.path("/Chronometers");
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.chronometer.$remove(successCallback, errorCallback);
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
});