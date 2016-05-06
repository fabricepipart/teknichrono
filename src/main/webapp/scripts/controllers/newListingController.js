
angular.module('jbossforgehtml5').controller('NewListingController', function ($scope, $location, locationParser, flash, ListingResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.listing = $scope.listing || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The listing was created successfully.'});
            $location.path('/Listings');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        ListingResource.save($scope.listing, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Listings");
    };
});