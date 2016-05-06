
angular.module('jbossforgehtml5').controller('NewLapController', function ($scope, $location, locationParser, flash, LapResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.lap = $scope.lap || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The lap was created successfully.'});
            $location.path('/Laps');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        LapResource.save($scope.lap, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Laps");
    };
});