
angular.module('frontend').controller('NewBeaconController', function ($scope, $location, locationParser, flash, BeaconResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.beacon = $scope.beacon || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The beacon was created successfully.'});
            $location.path('/Beacons');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        BeaconResource.save($scope.beacon, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Beacons");
    };
});