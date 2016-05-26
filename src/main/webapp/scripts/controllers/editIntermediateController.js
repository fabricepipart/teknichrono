

angular.module('frontend').controller('EditIntermediateController', function($scope, $routeParams, $location, flash, IntermediateResource , PilotResource, ChronoPointResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.intermediate = new IntermediateResource(self.original);
            PilotResource.queryAll(function(items) {
                $scope.pilotSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.intermediate.pilot && item.id == $scope.intermediate.pilot.id) {
                        $scope.pilotSelection = labelObject;
                        $scope.intermediate.pilot = wrappedObject;
                        self.original.pilot = $scope.intermediate.pilot;
                    }
                    return labelObject;
                });
            });
            ChronoPointResource.queryAll(function(items) {
                $scope.chronopointSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.intermediate.chronopoint && item.id == $scope.intermediate.chronopoint.id) {
                        $scope.chronopointSelection = labelObject;
                        $scope.intermediate.chronopoint = wrappedObject;
                        self.original.chronopoint = $scope.intermediate.chronopoint;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The intermediate could not be found.'});
            $location.path("/Intermediates");
        };
        IntermediateResource.get({IntermediateId:$routeParams.IntermediateId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.intermediate);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The intermediate was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.intermediate.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Intermediates");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The intermediate was deleted.'});
            $location.path("/Intermediates");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.intermediate.$remove(successCallback, errorCallback);
    };
    
    $scope.$watch("pilotSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.intermediate.pilot = {};
            $scope.intermediate.pilot.id = selection.value;
        }
    });
    $scope.$watch("chronopointSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.intermediate.chronopoint = {};
            $scope.intermediate.chronopoint.id = selection.value;
        }
    });
    
    $scope.get();
});