

angular.module('frontend').controller('EditPilotController', function($scope, $routeParams, $location, flash, PilotResource , BeaconResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.pilot = new PilotResource(self.original);
            BeaconResource.queryAll(function(items) {
                $scope.currentBeaconSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.pilot.currentBeacon && item.id == $scope.pilot.currentBeacon.id) {
                        $scope.currentBeaconSelection = labelObject;
                        $scope.pilot.currentBeacon = wrappedObject;
                        self.original.currentBeacon = $scope.pilot.currentBeacon;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The pilot could not be found.'});
            $location.path("/Pilots");
        };
        PilotResource.get({PilotId:$routeParams.PilotId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.pilot);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The pilot was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.pilot.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Pilots");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The pilot was deleted.'});
            $location.path("/Pilots");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.pilot.$remove(successCallback, errorCallback);
    };
    
    $scope.$watch("currentBeaconSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.pilot.currentBeacon = {};
            $scope.pilot.currentBeacon.id = selection.value;
        }
    });
    
    $scope.get();
});