
angular.module('frontend').controller('NewEventController', function ($scope, $location, locationParser, flash, EventResource, SessionResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.event = $scope.event || {};

    $scope.sessionsList = SessionResource.queryAll(function (items) {
        $scope.sessionsSelectionList = $.map(items, function (item) {
            return ({
                value: item.id,
                text: item.id
            });
        });
    });
    $scope.$watch("sessionsSelection", function (selection) {
        if (typeof selection != 'undefined') {
            $scope.event.sessions = [];
            $.each(selection, function (idx, selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.event.sessions.push(collectionItem);
            });
        }
    });


    $scope.save = function () {
        var successCallback = function (data, responseHeaders) {
            flash.setMessage({ 'type': 'success', 'text': 'The event was created successfully.' });
            $location.path('/Events');
        };
        var errorCallback = function (response) {
            if (response && response.data && response.data.message) {
                flash.setMessage({ 'type': 'error', 'text': response.data.message }, true);
            } else {
                flash.setMessage({ 'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.' }, true);
            }
        };
        EventResource.save($scope.event, successCallback, errorCallback);
    };

    $scope.cancel = function () {
        $location.path("/Events");
    };
});