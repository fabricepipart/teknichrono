

angular.module('frontend').controller('EditCategoryController', function ($scope, $routeParams, $location, flash, CategoryResource, PilotResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;

    $scope.get = function () {
        var successCallback = function (data) {
            self.original = data;
            $scope.category = new CategoryResource(self.original);
            PilotResource.queryAll(function (items) {
                $scope.pilotsSelectionList = $.map(items, function (item) {
                    var wrappedObject = {
                        id: item.id
                    };
                    var labelObject = {
                        value: item.id,
                        text: item.firstName + ' ' + item.lastName
                    };
                    if ($scope.category.pilots) {
                        $.each($scope.category.pilots, function (idx, element) {
                            if (item.id == element.id) {
                                $scope.pilotsSelection.push(labelObject);
                                $scope.category.pilots.push(wrappedObject);
                            }
                        });
                        self.original.pilots = $scope.category.pilots;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The category could not be found.' });
            $location.path("/Categories");
        };
        CategoryResource.get({ CategoryId: $routeParams.CategoryId }, successCallback, errorCallback);
    };

    $scope.isClean = function () {
        return angular.equals(self.original, $scope.category);
    };

    $scope.save = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'success', 'text': 'The category was updated successfully.' }, true);
            $scope.get();
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.category.$update(successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Categories");
    };

    $scope.remove = function () {
        var successCallback = function () {
            flash.setMessage({ 'type': 'error', 'text': 'The category was deleted.' });
            $location.path("/Categories");
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        $scope.category.$remove(successCallback, errorCallback);
    };

    $scope.pilotsSelection = $scope.pilotsSelection || [];
    $scope.$watch("pilotsSelection", function (selection) {
        if (typeof selection != 'undefined' && $scope.category) {
            $scope.category.pilots = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.category.pilots.push(collectionItem);
            });
        }
    });

    $scope.get();
});