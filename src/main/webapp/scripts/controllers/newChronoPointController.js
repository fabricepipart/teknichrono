
angular.module('frontend').controller('NewChronoPointController', function ($scope, $location, locationParser, flash, ChronoPointResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.chronoPoint = $scope.chronoPoint || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The chronoPoint was created successfully.'});
            $location.path('/ChronoPoints');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        ChronoPointResource.save($scope.chronoPoint, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/ChronoPoints");
    };
});