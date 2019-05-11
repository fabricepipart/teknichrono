
angular.module('frontend').controller('NewChronometerController', function ($scope, $location, locationParser, flash, ChronometerResource , PingResource, SessionResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.chronometer = $scope.chronometer || {};
    
    $scope.pingsList = PingResource.queryAll(function(items){
        $scope.pingsSelectionList = $.map(items, function(item) {
            return ( {
                value : item.id,
                text : item.id
            });
        });
    });
    $scope.$watch("pingsSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.chronometer.pings = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.chronometer.pings.push(collectionItem);
            });
        }
    });

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
            $scope.chronometer.sessions = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.chronometer.sessions.push(collectionItem);
            });
        }
    });


    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The chronometer was created successfully.'});
            $location.path('/Chronometers');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        ChronometerResource.save($scope.chronometer, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Chronometers");
    };
});