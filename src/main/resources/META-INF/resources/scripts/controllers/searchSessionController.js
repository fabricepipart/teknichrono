

angular.module('frontend').controller('SearchSessionController', function ($scope, $http, $filter, SessionResource, PilotResource, EventResource, LocationResource, ChronometerResource) {

    $scope.search = {};
    $scope.currentPage = 0;
    $scope.pageSize = 50;
    $scope.searchResults = [];
    $scope.filteredResults = [];
    $scope.pageRange = [];
    $scope.sessionType = {};
    $scope.numberOfPages = function () {
        var result = Math.ceil($scope.filteredResults.length / $scope.pageSize);
        var max = (result == 0) ? 1 : result;
        $scope.pageRange = [];
        for (var ctr = 0; ctr < max; ctr++) {
            $scope.pageRange.push(ctr);
        }
        return max;
    };
    $scope.currentSessionList = [
        "true",
        "false"
    ];
    $scope.eventList = EventResource.queryAll();
    $scope.locationList = LocationResource.queryAll();
    $scope.sessionTypeList = [
        { key: "TIME_TRIAL", short: "tt", text: "Time trial" },
        { key: "RACE", short: "rc", text: "Race" }
    ];

    $scope.performSearch = function () {
        $scope.searchResults = SessionResource.queryAll(function () {
            $scope.filteredResults = $filter('searchFilter')($scope.searchResults, $scope);
            $scope.currentPage = 0;
        });
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
});