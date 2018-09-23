


angular.module('frontend').controller('StartLapTimeController', function ($scope, $location, $http, $filter, flash, PingResource, PilotResource, ChronometerResource, SessionResource, LocationResource, EventResource, CategoryResource) {


    $scope.event = {};
    $scope.location = {};
    $scope.category = {};


    $scope.session = {};
    $scope.pilot = {};
    $scope.chrono = {};


    LocationResource.queryAll(function (items) {
        $scope.locationSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.name
            });
        });
    });
    $scope.$watch("locationSelection", function (selection) {
        if (typeof selection != 'undefined' && selection) {
            $scope.location = {};
            $scope.location.id = selection.value;
            $scope.filter();
        }
    });
    EventResource.queryAll(function (items) {
        $scope.eventSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.name
            });
        });
    });
    $scope.$watch("eventSelection", function (selection) {
        if (typeof selection != 'undefined' && selection) {
            $scope.event = {};
            $scope.event.id = selection.value;
            $scope.filter();
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
        if (typeof selection != 'undefined' && selection) {
            $scope.category = {};
            $scope.category.id = selection.value;
            $scope.filter();
        }
    });



    $scope.pilotList = PilotResource.queryAll(function (items) {
        $scope.pilotSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: (item.currentBeacon ? item.currentBeacon.number + ' - ' : '') + item.firstName + ' ' + item.lastName,
                categoryId: (item.category ? item.category.id : ''),
                beaconId: (item.currentBeacon ? item.currentBeacon.id : '')
            });
        });
    });
    $scope.$watch("pilotSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.pilot = {};
            $scope.pilot.id = selection.value;
            $scope.pilot.beaconId = selection.beaconId;
            $scope.filter();
        }
    });

    $scope.chronoList = ChronometerResource.queryAll(function (items) {
        $scope.chronoSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.name
            });
        });
    });
    $scope.$watch("chronoSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.chrono = {};
            $scope.chrono.id = selection.value;
            $scope.filter();
        }
    });

    $scope.sessionSelectionList = SessionResource.queryAll(function (items) {
        $scope.sessionSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.name,
                locationId: item.location.id,
                eventId: item.event.id,
                current: item.current,
                chronosIds: item.chronometers.map(a => a.id),
                pilotsIds: item.pilots.map(a => a.id)
            });
        });
    });
    $scope.$watch("sessionSelection", function (selection) {
        if (typeof selection != 'undefined' && selection) {
            $scope.session = {};
            $scope.session.id = selection.value;
            $scope.locationSelection = $filter("filter")($scope.locationSelectionList, { value: selection.locationId })[0];
            $scope.eventSelection = $filter("filter")($scope.eventSelectionList, { value: selection.eventId })[0];
            $scope.chronoSelectionList = $filter("filter")($scope.chronoSelectionList, $scope.filterChronosBySession);
            $scope.pilotSelectionList = $filter("filter")($scope.pilotSelectionList, $scope.filterPilotsBySession);
            $scope.filter();
        }
    });

    $scope.filterChronosBySession = function (chrono) {
        return ($scope.sessionSelection.chronosIds.indexOf(chrono.value) !== -1);
    };

    $scope.filterPilotsBySession = function (pilot) {
        return ($scope.sessionSelection.pilotsIds.indexOf(pilot.value) !== -1);
    };

    $scope.filter = function () {
        if ($scope.locationSelection) {
            $scope.sessionSelectionList = $filter("filter")($scope.sessionSelectionList, { locationId: $scope.locationSelection.value });
        }
        if ($scope.eventSelection) {
            $scope.sessionSelectionList = $filter("filter")($scope.sessionSelectionList, { eventId: $scope.eventSelection.value });
        }
        if ($scope.categorySelection) {
            $scope.pilotSelectionList = $filter("filter")($scope.pilotSelectionList, { categoryId: $scope.categorySelection.value });
        }
    };

    $scope.cancel = function () {
        $scope.eventSelection = {};
        $scope.locationSelection = {};
        $scope.categorySelection = {};
        $scope.filter();
    };

    $scope.cancelLaptime = function () {
        $scope.sessionSelection = {};
        $scope.chronoSelection = {};
        $scope.pilotSelection = {};
    };

    $scope.startSession = function () {
        var now = $filter('date')(new Date(), 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC')
        SessionResource.start({ id: $scope.session.id, dateTime: now });
        $scope.sessionSelection.current = true;
    }

    $scope.endSession = function () {
        var now = $filter('date')(new Date(), 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC')
        SessionResource.end({ id: $scope.session.id, dateTime: now });
        $scope.sessionSelection.current = false;
    };


    $scope.startLapTime = function () {
        var now = $filter('date')(new Date(), 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC')
        PingResource.create({ chronoId: $scope.chronoSelection.value, beaconId: $scope.pilotSelection.beaconId }, { power: '1', dateTime: now });
    }


    $scope.filter();
});