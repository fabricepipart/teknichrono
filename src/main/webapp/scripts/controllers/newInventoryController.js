
angular.module('jbossforgehtml5').controller('NewInventoryController', function ($scope, $location, locationParser, flash, InventoryResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.inventory = $scope.inventory || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The inventory was created successfully.'});
            $location.path('/Inventories');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        InventoryResource.save($scope.inventory, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Inventories");
    };
});