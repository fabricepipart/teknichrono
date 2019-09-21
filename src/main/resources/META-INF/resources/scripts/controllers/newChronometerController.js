
angular.module('frontend').controller('NewChronometerController', function ($scope, $location, locationParser, flash, ChronometerResource, PingResource, SessionResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.chronometer = $scope.chronometer || {};


    $scope.selectionStrategyList = [
        { key: "FIRST", short: "FIRST", text: "First ping" },
        { key: "HIGH", short: "HIGH", text: "Highest ping" },
        { key: "LAST", short: "LAST", text: "Last ping" }
    ];

    $scope.sendStrategyList = [
        { key: "ASYNC", short: "ASYNC", text: "Send in background" },
        { key: "NONE", short: "NONE", text: "Don't send" }
    ];

    $scope.orderToExecuteList = [
        { key: "UPDATE", short: "UPDATE", text: "Update" },
        { key: "RESTART", short: "RESTART", text: "Restart" },
        { key: "GET_LOGS", short: "GET_LOGS", text: "Retrieve last logs" }
    ];

    $scope.save = function () {
        var successCallback = function (data, responseHeaders) {
            flash.setMessage({ 'type': 'success', 'text': 'The chronometer was created successfully.' });
            $location.path('/Chronometers');
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        ChronometerResource.save($scope.chronometer, successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Chronometers");
    };
});