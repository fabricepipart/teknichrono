

angular.module('frontend').controller('LogsChronometerController', function ($scope, $interval, $routeParams, $location, flash, LogsResource) {

    $scope.search = {};
    $scope.currentPage = 0;
    $scope.pageSize = 100;
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
        $scope.searchResults = LogsResource.queryAll({ chronoId: $routeParams.ChronometerId }, function () {
            //$scope.filteredResults = $filter('searchFilter')($scope.searchResults, $scope);
            $scope.filteredResults = $scope.searchResults
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

    $scope.get();
    var refresher = $interval(function () {
        $scope.get();
    }, 10000);
    $scope.$on('$destroy', function () {
        $interval.cancel(refresher);
    });
});