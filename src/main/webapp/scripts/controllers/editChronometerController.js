

angular.module('frontend').controller('EditChronometerController', function($scope, $routeParams, $location, flash, ChronometerResource , PingResource, EventResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.chronometer = new ChronometerResource(self.original);
            PingResource.queryAll(function(items) {
                $scope.pingsSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.chronometer.pings){
                        $.each($scope.chronometer.pings, function(idx, element) {
                            if(item.id == element.id) {
                                $scope.pingsSelection.push(labelObject);
                                $scope.chronometer.pings.push(wrappedObject);
                            }
                        });
                        self.original.pings = $scope.chronometer.pings;
                    }
                    return labelObject;
                });
            });
            EventResource.queryAll(function(items) {
                $scope.eventSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.chronometer.event && item.id == $scope.chronometer.event.id) {
                        $scope.eventSelection = labelObject;
                        $scope.chronometer.event = wrappedObject;
                        self.original.event = $scope.chronometer.event;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The chronometer could not be found.'});
            $location.path("/Chronometers");
        };
        ChronometerResource.get({ChronometerId:$routeParams.ChronometerId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.chronometer);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The chronometer was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.chronometer.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Chronometers");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The chronometer was deleted.'});
            $location.path("/Chronometers");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.chronometer.$remove(successCallback, errorCallback);
    };
    
    $scope.pingsSelection = $scope.pingsSelection || [];
    $scope.$watch("pingsSelection", function(selection) {
        if (typeof selection != 'undefined' && $scope.chronometer) {
            $scope.chronometer.pings = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.chronometer.pings.push(collectionItem);
            });
        }
    });
    $scope.$watch("eventSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.chronometer.event = {};
            $scope.chronometer.event.id = selection.value;
        }
    });
    
    $scope.get();
});