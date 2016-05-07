
angular.module('frontend').controller('NewLapTimeController', function ($scope, $location, locationParser, flash, LapTimeResource , PilotResource, EventResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.lapTime = $scope.lapTime || {};
    
    $scope.pilotList = PilotResource.queryAll(function(items){
        $scope.pilotSelectionList = $.map(items, function(item) {
            return ( {
                value : item.id,
                text : item.id
            });
        });
    });
    $scope.$watch("pilotSelection", function(selection) {
        if ( typeof selection != 'undefined') {
            $scope.lapTime.pilot = {};
            $scope.lapTime.pilot.id = selection.value;
        }
    });
    
    $scope.eventList = EventResource.queryAll(function(items){
        $scope.eventSelectionList = $.map(items, function(item) {
            return ( {
                value : item.id,
                text : item.id
            });
        });
    });
    $scope.$watch("eventSelection", function(selection) {
        if ( typeof selection != 'undefined') {
            $scope.lapTime.event = {};
            $scope.lapTime.event.id = selection.value;
        }
    });
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The lapTime was created successfully.'});
            $location.path('/LapTimes');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        LapTimeResource.save($scope.lapTime, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/LapTimes");
    };
});