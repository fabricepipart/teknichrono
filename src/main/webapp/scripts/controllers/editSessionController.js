

angular.module('frontend').controller('EditSessionController', function ($scope, $filter, $routeParams, $location, flash, SessionResource, PilotResource, EventResource, LocationResource, ChronometerResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;

    $scope.get = function () {
        var successCallback = function (data) {
            self.original = data;
            $scope.session = new SessionResource(self.original);
            $scope.session.start = $filter('date')(self.original.start, 'yyyy-MM-ddTHH:mm:ss', 'UTC')
            $scope.session.end = $filter('date')(self.original.end, 'yyyy-MM-ddTHH:mm:ss', 'UTC')
            $scope.session.pilots = []
            $scope.session.chronometers = []

            PilotResource.queryAll(function (items) {
                $scope.pilotsSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.firstName + ' ' + item.lastName
                    };
                    if (self.original.pilots) {
                        $.each(self.original.pilots, function (idx, element) {
                            if (item.id == element.id) {
                                $scope.pilotsSelection.push(labelObject);
                                $scope.session.pilots.push(wrappedObject);
                            }
                        });
                    }
                    return labelObject;
                });
            });
            EventResource.queryAll(function (items) {
                $scope.eventSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.name
                    };
                    if ($scope.session.event && item.id == $scope.session.event.id) {
                        $scope.eventSelection = labelObject;
                        $scope.session.event = wrappedObject;
                        self.original.event = $scope.session.event;
                    }
                    return labelObject;
                });
            });
            LocationResource.queryAll(function (items) {
                $scope.locationSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.name
                    };
                    if ($scope.session.location && item.id == $scope.session.location.id) {
                        $scope.locationSelection = labelObject;
                        $scope.session.location = wrappedObject;
                        self.original.location = $scope.session.location;
                    }
                    return labelObject;
                });
            });
            ChronometerResource.queryAll(function (items) {
                $scope.chronometersSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.name
                    };
                    if (self.original.chronometers) {
                        $.each(self.original.chronometers, function (idx, element) {
                            if (item.id == element.id) {
                                $scope.chronometersSelection.push(labelObject);
                                $scope.session.chronometers.push(wrappedObject);
                            }
                        });
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The session could not be found.' });
            $location.path("/Sessions");
        };
        SessionResource.get({ SessionId: $routeParams.SessionId }, successCallback, errorCallback);
    };

    $scope.isClean = function () {
        return angular.equals(self.original, $scope.session);
    };

    $scope.isCreation = function () {
        return $location.path().indexOf('/Sessions/new') > -1;
    };

    $scope.save = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'success', 'text': 'The session was updated successfully.' }, true);
            $scope.get();
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.session.$update(successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Sessions");
    };

    $scope.startSession = function () {
        var now = $filter('date')(new Date(), 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC')
        SessionResource.start({ id: $routeParams.SessionId, dateTime: now });
    };

    $scope.endSession = function () {
        var now = $filter('date')(new Date(), 'yyyy-MM-ddTHH:mm:ss.sss', 'UTC')
        SessionResource.end({ id: $routeParams.SessionId, dateTime: now });
    };



    $scope.remove = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The session was deleted.' });
            $location.path("/Sessions");
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.session.$remove(successCallback, errorCallback);
    };

    $scope.pilotsSelection = $scope.pilotsSelection || [];
    $scope.$watch("pilotsSelection", function (selection) {
        if (typeof selection != 'undefined' && $scope.session) {
            $scope.session.pilots = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.session.pilots.push(collectionItem);
            });
        }
    });
    $scope.$watch("eventSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.session.event = {};
            $scope.session.event.id = selection.value;
        }
    });
    $scope.$watch("locationSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.session.location = {};
            $scope.session.location.id = selection.value;
        }
    });
    $scope.chronometersSelection = $scope.chronometersSelection || [];
    $scope.$watch("chronometersSelection", function (selection) {
        if (typeof selection != 'undefined' && $scope.session) {
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

    $scope.get();
});