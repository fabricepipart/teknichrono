

angular.module('frontend').controller('LapTimeController', function ($scope, $location, $http, $filter, flash, LapTimeResource, PilotResource, SessionResource, LocationResource, EventResource, CategoryResource) {

    $scope.search = {};
    $scope.searchTypeSelection = {}
    $scope.session = {};
    $scope.event = {};
    $scope.location = {};
    $scope.pilot = {};
    $scope.category = {};
    $scope.currentPage = 0;
    $scope.pageSize = 50;
    $scope.searchResults = [];
    $scope.filteredResults = [];
    $scope.pageRange = [];
    $scope.numberOfPages = function () {
        var result = Math.ceil($scope.filteredResults.length / $scope.pageSize);
        var max = (result == 0) ? 1 : result;
        $scope.pageRange = [];
        for (var ctr = 0; ctr < max; ctr++) {
            $scope.pageRange.push(ctr);
        }
        return max;
    };
    $scope.pilotList = PilotResource.queryAll(function (items) {
        $scope.pilotSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.firstName + ' ' + item.lastName
            });
        });
    });
    $scope.$watch("pilotSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.pilot = {};
            $scope.pilot.id = selection.value;
            $scope.performSearch();
        }
    });

    $scope.sessionSelectionList = SessionResource.queryAll(function (items) {
        $scope.sessionSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.name,
                locationId: item.location.id,
                eventId: item.event.id,
                current: item.current
            });
        });
    });
    $scope.$watch("sessionSelection", function (selection) {
        if (typeof selection != 'undefined' && selection) {
            $scope.session = {};
            $scope.session.id = selection.value;
            $scope.locationSelection = $filter("filter")($scope.locationSelectionList, { value: selection.locationId })[0];
            $scope.eventSelection = $filter("filter")($scope.eventSelectionList, { value: selection.eventId })[0];
            $scope.performSearch();
            if (selection.current == 'true') {
                $interval($scope.reload, 2000);
            }
        }
    });

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
            $scope.performSearch();
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
            $scope.performSearch();
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
            $scope.performSearch();
        }
    });

    $scope.searchesTypesList = [
        { key: "", text: "All laps" },
        { key: "results", text: "Race results" },
        { key: "best", text: "Best laps" }
    ];
    $scope.$watch("searchTypeSelection", function (selection) {
        if (typeof selection != 'undefined' && selection) {
            $scope.performSearch();
        }
    });

    $scope.performSearch = function () {
        $scope.search = null
        if ($scope.session.id) {
            $scope.sessionId = $scope.session.id
        }
        $scope.sessionId = null
        if ($scope.session.id) {
            $scope.sessionId = $scope.session.id
        }
        $scope.eventId = null
        if ($scope.event.id) {
            $scope.eventId = $scope.event.id
        }
        $scope.locationId = null
        if ($scope.location.id) {
            $scope.locationId = $scope.location.id
        }
        $scope.pilotId = null
        if ($scope.pilot.id) {
            $scope.pilotId = $scope.pilot.id
        }
        $scope.categoryId = null
        if ($scope.category.id) {
            $scope.categoryId = $scope.category.id
        }
        flash.setMessage({}, true);
        if ($scope.searchTypeSelection && $scope.searchTypeSelection.text) {
            valid = true
            if ($scope.searchTypeSelection.key == "results") {
                if (!$scope.sessionId) {
                    valid = false
                }
            } else {
                if (!$scope.sessionId && !$scope.eventId && !$scope.locationId) {
                    valid = false
                }
            }
            if (valid) {
                $scope.searchResults = LapTimeResource.queryAll({ SearchType: $scope.searchTypeSelection.key, sessionId: $scope.sessionId, eventId: $scope.eventId, locationId: $scope.locationId, pilotId: $scope.pilotId, categoryId: $scope.categoryId }, function () {
                    $scope.filteredResults = $filter('searchFilter')($scope.searchResults, $scope);
                    $scope.currentPage = 0;
                });
            } else {
                flash.setMessage({ 'type': 'success', 'text': 'Please select the correct criteria to search for LapTimes : Search Type and one of Event, Location or Session' }, true);
                $scope.session.invalid = true
            }
        }
    };


    $scope.save = function () {
        var successCallback = function (data, responseHeaders) {
            var id = locationParser(responseHeaders);
            flash.setMessage({ 'type': 'info', 'text': 'The lapTime was created successfully.' });
            $location.path('/LapTimes');
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

    $scope.previous = function () {
        if ($scope.currentPage > 0) {
            $scope.currentPage--;
        }
    };

    $scope.next = function () {
        if ($scope.currentPage < ($scope.numberOfPages() - 1)) {
            $scope.currentPage++;
        }
    };

    $scope.setPage = function (n) {
        $scope.currentPage = n;
    };
    $scope.cancel = function () {
        $location.path('/LapTimes?');
    };

    $scope.performSearch();
});