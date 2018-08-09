

angular.module('frontend').controller('EditPilotController', function ($scope, $routeParams, $location, flash, PilotResource, SessionResource, CategoryResource, BeaconResource, LapTimeResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;

    $scope.get = function () {
        var successCallback = function (data) {
            self.original = data;
            $scope.pilot = new PilotResource(self.original);
            SessionResource.queryAll(function (items) {
                $scope.sessionsSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.name
                    };
                    if ($scope.pilot.sessions) {
                        $.each($scope.pilot.sessions, function (idx, element) {
                            if (item.id == element.id) {
                                $scope.sessionsSelection.push(labelObject);
                                $scope.pilot.sessions.push(wrappedObject);
                            }
                        });
                        self.original.sessions = $scope.pilot.sessions;
                    }
                    return labelObject;
                });
            });
            CategoryResource.queryAll(function (items) {
                $scope.categorySelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.name
                    };
                    if ($scope.pilot.category && item.id == $scope.pilot.category.id) {
                        $scope.categorySelection = labelObject;
                        $scope.pilot.category = wrappedObject;
                        self.original.category = $scope.pilot.category;
                    }
                    return labelObject;
                });
            });
            BeaconResource.queryAll(function (items) {
                $scope.currentBeaconSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    beaconText = item.number
                    if (item.pilot.id > 0) {
                        beaconText = item.number + ' (' + item.pilot.firstName + ' ' + item.pilot.lastName + ')'
                    }
                    var labelObject = {
                        value: item.id,
                        text: beaconText
                    };
                    if ($scope.pilot.currentBeacon && item.id == $scope.pilot.currentBeacon.id) {
                        $scope.currentBeaconSelection = labelObject;
                        $scope.pilot.currentBeacon = wrappedObject;
                        self.original.currentBeacon = $scope.pilot.currentBeacon;
                    }
                    return labelObject;
                });
            });
            LapTimeResource.queryAll(function (items) {
                $scope.lapsSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.id
                    };
                    if ($scope.pilot.laps) {
                        $.each($scope.pilot.laps, function (idx, element) {
                            if (item.id == element.id) {
                                $scope.lapsSelection.push(labelObject);
                                $scope.pilot.laps.push(wrappedObject);
                            }
                        });
                        self.original.laps = $scope.pilot.laps;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The pilot could not be found.' });
            $location.path("/Pilots");
        };
        PilotResource.get({ PilotId: $routeParams.PilotId }, successCallback, errorCallback);
    };

    $scope.isClean = function () {
        return angular.equals(self.original, $scope.pilot);
    };

    $scope.save = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'success', 'text': 'The pilot was updated successfully.' }, true);
            $scope.get();
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.pilot.$update(successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Pilots");
    };

    $scope.remove = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The pilot was deleted.' });
            $location.path("/Pilots");
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.pilot.$remove(successCallback, errorCallback);
    };

    $scope.sessionsSelection = $scope.sessionsSelection || [];
    $scope.$watch("sessionsSelection", function (selection) {
        if (typeof selection != 'undefined' && $scope.pilot) {
            $scope.pilot.sessions = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.pilot.sessions.push(collectionItem);
            });
        }
    });
    $scope.$watch("categorySelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.pilot.category = {};
            $scope.pilot.category.id = selection.value;
        }
    });
    $scope.$watch("currentBeaconSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.pilot.currentBeacon = {};
            $scope.pilot.currentBeacon.id = selection.value;
        }
    });
    $scope.lapsSelection = $scope.lapsSelection || [];
    $scope.$watch("lapsSelection", function (selection) {
        if (typeof selection != 'undefined' && $scope.pilot) {
            $scope.pilot.laps = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.pilot.laps.push(collectionItem);
            });
        }
    });

    $scope.get();
});