

angular.module('frontend').controller('EditBeaconController', function ($scope, $routeParams, $location, flash, BeaconResource, PilotResource, PingResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;

    $scope.get = function () {
        var successCallback = function (data) {
            self.original = data;
            $scope.beacon = new BeaconResource(self.original);
            PilotResource.queryAll(function (items) {
                $scope.pilotSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.firstName + ' ' + item.lastName
                    };
                    if ($scope.beacon.pilot && item.id == $scope.beacon.pilot.id) {
                        $scope.pilotSelection = labelObject;
                        $scope.beacon.pilot = wrappedObject;
                        self.original.pilot = $scope.beacon.pilot;
                    }
                    return labelObject;
                });
            });
            PingResource.queryAll(function (items) {
                $scope.pingsSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.id
                    };
                    if ($scope.beacon.pings) {
                        $.each($scope.beacon.pings, function (idx, element) {
                            if (item.id == element.id) {
                                $scope.pingsSelection.push(labelObject);
                                $scope.beacon.pings.push(wrappedObject);
                            }
                        });
                        self.original.pings = $scope.beacon.pings;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The beacon could not be found.' });
            $location.path("/Beacons");
        };
        BeaconResource.get({ BeaconId: $routeParams.BeaconId }, successCallback, errorCallback);
    };

    $scope.isClean = function () {
        return angular.equals(self.original, $scope.beacon);
    };

    $scope.save = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'success', 'text': 'The beacon was updated successfully.' }, true);
            $scope.get();
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.beacon.$update(successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Beacons");
    };

    $scope.remove = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The beacon was deleted.' });
            $location.path("/Beacons");
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.beacon.$remove(successCallback, errorCallback);
    };

    $scope.$watch("pilotSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.beacon.pilot = {};
            $scope.beacon.pilot.id = selection.value;
        }
    });
    $scope.pingsSelection = $scope.pingsSelection || [];
    $scope.$watch("pingsSelection", function (selection) {
        if (typeof selection != 'undefined' && $scope.beacon) {
            $scope.beacon.pings = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.beacon.pings.push(collectionItem);
            });
        }
    });

    $scope.get();
});