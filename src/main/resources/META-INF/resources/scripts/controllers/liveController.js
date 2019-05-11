

angular.module('frontend').controller('LiveController', function ($scope, $interval, $location, $http, $filter, flash, LapTimeResource, PilotResource, SessionResource, LocationResource, EventResource, CategoryResource) {

    $scope.search = {};

    $scope.searchesTypesList = [
        { key: "", text: "All laps" },
        { key: "results", text: "Race results" },
        { key: "best", text: "Best laps" }
    ];
    $scope.searchTypeSelection = $scope.searchesTypesList[0];
    $scope.session = {};

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

    $scope.get = function () {
        var successCallback = function (data) {
            $scope.session = data;
        };
        var errorCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'No current session could be found.' });
            $location.path("/Live");
        };
        SessionResource.current({}, successCallback, errorCallback);
    };
    $scope.get();

    $scope.$watch("searchTypeSelection", function (selection) {
        if (typeof selection != 'undefined' && selection) {
            $scope.performSearch();
        }
    });

    $scope.performSearch = function () {
        $scope.search = null
        $scope.sessionId = null
        if ($scope.session.id) {
            $scope.sessionId = $scope.session.id
        }
        flash.setMessage({}, true);
        if ($scope.searchTypeSelection && $scope.searchTypeSelection.text) {
            valid = false
            if ($scope.sessionId) {
                valid = true
            }
            if (valid) {
                $scope.searchResults = LapTimeResource.queryAll({ SearchType: $scope.searchTypeSelection.key, sessionId: $scope.sessionId, eventId: $scope.eventId, locationId: $scope.locationId, pilotId: $scope.pilotId, categoryId: $scope.categoryId }, function () {
                    $scope.filteredResults = $filter('searchFilter')($scope.searchResults, $scope);
                    if ($scope.searchTypeSelection.key == "") {
                        $scope.filteredResults.reverse()
                    }
                    $scope.currentPage = 0;
                });
            } else {
                flash.setMessage({ 'type': 'success', 'text': 'Sorry there is no currently ongoing session' }, true);
                $scope.session.invalid = true
            }
        }
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

    $scope.performSearch();
    var refresher = $interval(function () {
        $scope.performSearch();
    }, 5000);
    $scope.$on('$destroy', function () {
        $interval.cancel(refresher);
    });

});