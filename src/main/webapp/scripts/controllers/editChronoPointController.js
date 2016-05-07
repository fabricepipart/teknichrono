

angular.module('frontend').controller('EditChronoPointController', function($scope, $routeParams, $location, flash, ChronoPointResource ) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.chronoPoint = new ChronoPointResource(self.original);
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The chronoPoint could not be found.'});
            $location.path("/ChronoPoints");
        };
        ChronoPointResource.get({ChronoPointId:$routeParams.ChronoPointId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.chronoPoint);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The chronoPoint was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.chronoPoint.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/ChronoPoints");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The chronoPoint was deleted.'});
            $location.path("/ChronoPoints");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.chronoPoint.$remove(successCallback, errorCallback);
    };
    
    
    $scope.get();
});