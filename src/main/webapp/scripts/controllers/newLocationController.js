
angular.module('frontend').controller('NewLocationController', function ($scope, $location, locationParser, flash, LocationResource , SessionResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.location = $scope.location || {};
    
    $scope.sessionsList = SessionResource.queryAll(function(items){
        $scope.sessionsSelectionList = $.map(items, function(item) {
            return ( {
                value : item.id,
                text : item.id
            });
        });
    });
    $scope.$watch("sessionsSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.location.sessions = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.location.sessions.push(collectionItem);
            });
        }
    });

    $scope.loopTrackList = [
        "true",
        "false"
    ];


    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The location was created successfully.'});
            $location.path('/Locations');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        LocationResource.save($scope.location, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Locations");
    };
});