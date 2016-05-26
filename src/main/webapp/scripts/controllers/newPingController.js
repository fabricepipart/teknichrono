
angular.module('frontend').controller('NewPingController', function ($scope, $location, locationParser, flash, PingResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.ping = $scope.ping || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The ping was created successfully.'});
            $location.path('/Pings');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        PingResource.save($scope.ping, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Pings");
    };
});