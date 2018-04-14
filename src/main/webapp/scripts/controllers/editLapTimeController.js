

angular.module('frontend').controller('EditLapTimeController', function($scope, $routeParams, $location, flash, LapTimeResource , PilotResource, SessionResource, PingResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.lapTime = new LapTimeResource(self.original);
            PilotResource.queryAll(function(items) {
                $scope.pilotSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.lapTime.pilot && item.id == $scope.lapTime.pilot.id) {
                        $scope.pilotSelection = labelObject;
                        $scope.lapTime.pilot = wrappedObject;
                        self.original.pilot = $scope.lapTime.pilot;
                    }
                    return labelObject;
                });
            });
            SessionResource.queryAll(function(items) {
                $scope.sessionSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.lapTime.session && item.id == $scope.lapTime.session.id) {
                        $scope.sessionSelection = labelObject;
                        $scope.lapTime.session = wrappedObject;
                        self.original.session = $scope.lapTime.session;
                    }
                    return labelObject;
                });
            });
            PingResource.queryAll(function(items) {
                $scope.intermediatesSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.lapTime.intermediates){
                        $.each($scope.lapTime.intermediates, function(idx, element) {
                            if(item.id == element.id) {
                                $scope.intermediatesSelection.push(labelObject);
                                $scope.lapTime.intermediates.push(wrappedObject);
                            }
                        });
                        self.original.intermediates = $scope.lapTime.intermediates;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The lapTime could not be found.'});
            $location.path("/LapTimes");
        };
        LapTimeResource.get({LapTimeId:$routeParams.LapTimeId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.lapTime);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The lapTime was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.lapTime.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/LapTimes");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The lapTime was deleted.'});
            $location.path("/LapTimes");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.lapTime.$remove(successCallback, errorCallback);
    };
    
    $scope.$watch("pilotSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.lapTime.pilot = {};
            $scope.lapTime.pilot.id = selection.value;
        }
    });
    $scope.$watch("sessionSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.lapTime.session = {};
            $scope.lapTime.session.id = selection.value;
        }
    });
    $scope.intermediatesSelection = $scope.intermediatesSelection || [];
    $scope.$watch("intermediatesSelection", function(selection) {
        if (typeof selection != 'undefined' && $scope.lapTime) {
            $scope.lapTime.intermediates = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.lapTime.intermediates.push(collectionItem);
            });
        }
    });
    
    $scope.get();
});