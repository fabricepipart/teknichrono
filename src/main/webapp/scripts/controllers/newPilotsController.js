
angular.module('jbossforgehtml5').controller('NewPilotsController', function ($scope, $location, locationParser, flash, PilotsResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.pilots = $scope.pilots || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The pilots was created successfully.'});
            $location.path('/Pilots');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        PilotsResource.save($scope.pilots, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Pilots");
    };
});