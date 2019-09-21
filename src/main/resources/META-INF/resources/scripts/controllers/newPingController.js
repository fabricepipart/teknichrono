
angular.module('frontend').controller('NewPingController', function ($scope, $location, locationParser, flash, PingResource, BeaconResource, ChronometerResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.ping = $scope.ping || {};

    $scope.beaconList = BeaconResource.queryAll(function (items) {
        $scope.beaconSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.id
            });
        });
    });
    $scope.$watch("beaconSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.ping.beacon = {};
            $scope.ping.beacon.id = selection.value;
        }
    });

    $scope.chronoList = ChronometerResource.queryAll(function (items) {
        $scope.chronoSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.id
            });
        });
    });
    $scope.$watch("chronoSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.ping.chrono = {};
            $scope.ping.chrono.id = selection.value;
        }
    });


    $scope.save = function () {
        var successCallback = function (data, responseHeaders) {
            flash.setMessage({ 'type': 'success', 'text': 'The ping was created successfully.' });
            $location.path('/Pings');
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        PingResource.save($scope.ping, successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Pings");
    };
});