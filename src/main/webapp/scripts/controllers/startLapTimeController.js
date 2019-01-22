


angular.module('frontend').controller('StartLapTimeController', function ($scope, $location, $http, $filter, flash, PingResource, PilotResource, ChronometerResource, SessionResource, BeaconResource, CategoryResource) {

    $scope.category = {};
    $scope.pilot = {};
    $scope.chrono = {};
    $scope.beacon = {};


    $scope.beaconList = BeaconResource.queryAll(function (items) {
        $scope.beaconSelectionList = $.map(items, function (item) {
            return ({
                id: item.id,
                number: item.number
            });
        });
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
        }
        $scope.filterPilots();
    });

    $scope.pilotList = PilotResource.queryAll(function (items) {
        $scope.pilotSelectionListComplete = $.map(items, function (item) {
            return ({
                value: item.id,
                text: (item.currentBeacon ? item.currentBeacon.number + ' - ' : '') + item.firstName + ' ' + item.lastName,
                categoryId: (item.category ? item.category.id : ''),
                beaconId: (item.currentBeacon ? item.currentBeacon.id : '')
            });
        });
        $scope.pilotSelectionList = $scope.pilotSelectionListComplete
    });
    $scope.$watch("pilotSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.pilot = {};
            $scope.pilot.id = selection.value;
            $scope.pilot.beaconId = selection.beaconId;
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
        }
    });

    $scope.filterPilots = function () {
        $scope.pilotSelectionList = $scope.pilotSelectionListComplete
        if ($scope.categorySelection) {
            $scope.pilotSelectionList = $filter("filter")($scope.pilotSelectionList, { categoryId: $scope.categorySelection.value });
        }
    };
    $scope.cancelLaptime = function () {
        $scope.categorySelection = {};
        $scope.chronoSelection = {};
        $scope.pilotSelection = {};
        $scope.beacon = {};
    };

    $scope.startLapTime = function () {
        var now = $filter('date')(new Date(), 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC')
        PingResource.create({ chronoId: $scope.chronoSelection.value, beaconId: $scope.pilotSelection.beaconId }, { power: '1', dateTime: now });
    }

    $scope.startLapTimeManual = function () {
        var now = $filter('date')(new Date(), 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC')
        var beaconSelection = $filter("filter")($scope.beaconSelectionList, { number: $scope.beacon.number })[0];
        PingResource.create({ chronoId: $scope.chronoSelection.value, beaconId: beaconSelection.id }, { power: '1', dateTime: now });
    }

});