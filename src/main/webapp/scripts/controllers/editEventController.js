

angular.module('frontend').controller('EditEventController', function($scope, $routeParams, $location, flash, EventResource , ChronoPointResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.event = new EventResource(self.original);
            ChronoPointResource.queryAll(function(items) {
                $scope.chronopointsSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        id : item.id
                    };
                    var labelObject = {
                        value : item.id,
                        text : item.id
                    };
                    if($scope.event.chronopoints){
                        $.each($scope.event.chronopoints, function(idx, element) {
                            if(item.id == element.id) {
                                $scope.chronopointsSelection.push(labelObject);
                                $scope.event.chronopoints.push(wrappedObject);
                            }
                        });
                        self.original.chronopoints = $scope.event.chronopoints;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The event could not be found.'});
            $location.path("/Events");
        };
        EventResource.get({EventId:$routeParams.EventId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.event);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The event was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.event.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Events");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The event was deleted.'});
            $location.path("/Events");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.event.$remove(successCallback, errorCallback);
    };
    
    $scope.chronopointsSelection = $scope.chronopointsSelection || [];
    $scope.$watch("chronopointsSelection", function(selection) {
        if (typeof selection != 'undefined' && $scope.event) {
            $scope.event.chronopoints = [];
            $.each(selection, function(idx,selectedItem) {
                var collectionItem = {};
                collectionItem.id = selectedItem.value;
                $scope.event.chronopoints.push(collectionItem);
            });
        }
    });
    
    $scope.get();
});