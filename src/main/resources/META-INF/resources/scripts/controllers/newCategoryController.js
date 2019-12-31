
angular.module('frontend').controller('NewCategoryController', function ($scope, $location, locationParser, flash, CategoryResource, PilotResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.category = $scope.category || {};

    $scope.pilotsList = PilotResource.queryAll(function (items) {
        $scope.pilotsSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.fullname
            });
        });
    });
    $scope.$watch("pilotsSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.category.pilots = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.category.pilots.push(collectionItem);
            });
        }
    });


    $scope.save = function () {
        var successCallback = function (data, responseHeaders) {
            flash.setMessage({ 'type': 'success', 'text': 'The category was created successfully.' });
            $location.path('/Categories');
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        CategoryResource.save($scope.category, successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Categories");
    };
});