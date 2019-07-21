

angular.module('frontend').controller('EditPingController', function($scope, $routeParams, $location, flash, PingResource , BeaconResource, ChronometerResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.ping = new PingResource(self.original);
            BeaconResource.queryAll(function(items) {
                $scope.beaconSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.ping.beacon && item.id == $scope.ping.beacon.id) {
                        $scope.beaconSelection = labelObject;
                        $scope.ping.beacon = wrappedObject;
                        self.original.beacon = $scope.ping.beacon;
                    }
                    return labelObject;
                });
            });
            ChronometerResource.queryAll(function(items) {
                $scope.chronoSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.ping.chrono && item.id == $scope.ping.chrono.id) {
                        $scope.chronoSelection = labelObject;
                        $scope.ping.chrono = wrappedObject;
                        self.original.chrono = $scope.ping.chrono;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The ping could not be found.'});
            $location.path("/Pings");
        };
        PingResource.get({PingId:$routeParams.PingId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.ping);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The ping was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.ping.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Pings");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The ping was deleted.'});
            $location.path("/Pings");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.ping.$remove(successCallback, errorCallback);
    };
    
    $scope.$watch("beaconSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.ping.beacon = {};
            $scope.ping.beacon.id = selection.value;
        }
    });
    $scope.$watch("chronoSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.ping.chrono = {};
            $scope.ping.chrono.id = selection.value;
        }
    });
    
    $scope.get();
});