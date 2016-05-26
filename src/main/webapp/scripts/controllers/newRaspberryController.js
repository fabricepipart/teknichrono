
angular.module('frontend').controller('NewRaspberryController', function ($scope, $location, locationParser, flash, RaspberryResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.raspberry = $scope.raspberry || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The raspberry was created successfully.'});
            $location.path('/Raspberries');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        RaspberryResource.save($scope.raspberry, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Raspberries");
    };
});