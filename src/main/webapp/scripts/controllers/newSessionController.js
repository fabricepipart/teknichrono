
angular.module('frontend').controller('NewSessionController', function ($scope, $location, locationParser, flash, SessionResource, PilotResource, EventResource, LocationResource, ChronometerResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.session = $scope.session || {};

    $scope.pilotsList = PilotResource.queryAll(function (items) {
        $scope.pilotsSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.firstName + ' ' + item.lastName
            });
        });
    });
    $scope.$watch("pilotsSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.session.pilots = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.session.pilots.push(collectionItem);
            });
        }
    });

    $scope.eventList = EventResource.queryAll(function (items) {
        $scope.eventSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.name
            });
        });
    });
    $scope.$watch("eventSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.session.event = {};
            $scope.session.event.id = selection.value;
        }
    });

    $scope.locationList = LocationResource.queryAll(function (items) {
        $scope.locationSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.name
            });
        });
    });
    $scope.$watch("locationSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.session.location = {};
            $scope.session.location.id = selection.value;
        }
    });

    $scope.chronometersList = ChronometerResource.queryAll(function (items) {
        $scope.chronometersSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.name
            });
        });
    });
    $scope.$watch("chronometersSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.session.chronometers = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.session.chronometers.push(collectionItem);
            });
        }
    });

    $scope.sessionTypeList = [
        { key: "TIME_TRIAL", short: "tt", text: "Time trial" },
        { key: "RACE", short: "rc", text: "Race" }
    ];


    $scope.save = function () {
        var successCallback = function (data, responseHeaders) {
            var id = locationParser(responseHeaders);
            flash.setMessage({ 'type': 'success', 'text': 'The session was created successfully.' });
            $location.path('/Sessions');
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        SessionResource.save($scope.session, successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Sessions");
    };
});