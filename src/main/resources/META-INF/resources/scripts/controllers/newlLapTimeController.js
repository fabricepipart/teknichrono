
angular.module('frontend').controller('NewlLapTimeController', function ($scope, $location, locationParser, flash, LapTimeResource, PilotResource, SessionResource, PingResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.lapTime = $scope.lapTime || {};

    $scope.pilotList = PilotResource.queryAll(function (items) {
        $scope.pilotSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.id
            });
        });
    });
    $scope.$watch("pilotSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.lapTime.pilot = {};
            $scope.lapTime.pilot.id = selection.value;
        }
    });

    $scope.sessionList = SessionResource.queryAll(function (items) {
        $scope.sessionSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.id
            });
        });
    });
    $scope.$watch("sessionSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.lapTime.session = {};
            $scope.lapTime.session.id = selection.value;
        }
    });

    $scope.intermediatesList = PingResource.queryAll(function (items) {
        $scope.intermediatesSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.id
            });
        });
    });
    $scope.$watch("intermediatesSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.lapTime.intermediates = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.lapTime.intermediates.push(collectionItem);
            });
        }
    });


    $scope.save = function () {
        var successCallback = function (data, responseHeaders) {
            flash.setMessage({ 'type': 'success', 'text': 'The lapTime was created successfully.' });
            $location.path('/lLapTimes');
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        LapTimeResource.save($scope.lapTime, successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/lLapTimes");
    };
});