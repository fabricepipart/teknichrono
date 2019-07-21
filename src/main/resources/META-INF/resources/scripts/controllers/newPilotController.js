
angular.module('frontend').controller('NewPilotController', function ($scope, $location, locationParser, flash, PilotResource, SessionResource, CategoryResource, BeaconResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.pilot = $scope.pilot || {};

    $scope.sessionsList = SessionResource.queryAll(function (items) {
        $scope.sessionsSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.id
            });
        });
    });
    $scope.$watch("sessionsSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.pilot.sessions = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.pilot.sessions.push(collectionItem);
            });
        }
    });

    $scope.categoryList = CategoryResource.queryAll(function (items) {
        $scope.categorySelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.name
            });
        });
    });
    $scope.$watch("categorySelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.pilot.category = {};
            $scope.pilot.category.id = selection.value;
        }
    });

    $scope.currentBeaconList = BeaconResource.queryAll(function (items) {
        $scope.currentBeaconSelectionList = $.map(items, function (item) {
            beaconText = item.number
            if (item.pilot && item.pilot.id > 0) {
                beaconText = item.number + ' (' + item.pilot.firstName + ' ' + item.pilot.lastName + ')'
            }
            return ({
                value: item.id,
                text: beaconText
            });
        });
    });
    $scope.$watch("currentBeaconSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.pilot.currentBeacon = {};
            $scope.pilot.currentBeacon.id = selection.value;
        }
    });


    $scope.save = function () {
        var successCallback = function (data, responseHeaders) {
            var id = locationParser(responseHeaders);
            flash.setMessage({ 'type': 'success', 'text': 'The pilot was created successfully.' });
            $location.path('/Pilots');
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        PilotResource.save($scope.pilot, successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Pilots");
    };
});