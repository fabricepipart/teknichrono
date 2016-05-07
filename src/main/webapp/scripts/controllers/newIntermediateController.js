
angular.module('frontend').controller('NewIntermediateController', function ($scope, $location, locationParser, flash, IntermediateResource , PilotResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.intermediate = $scope.intermediate || {};
    
    $scope.pilotList = PilotResource.queryAll(function(items){
        $scope.pilotSelectionList = $.map(items, function(item) {
            return ( {
                value : item.id,
                text : item.id
            });
        });
    });
    $scope.$watch("pilotSelection", function(selection) {
        if ( typeof selection != 'undefined') {
            $scope.intermediate.pilot = {};
            $scope.intermediate.pilot.id = selection.value;
        }
    });
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The intermediate was created successfully.'});
            $location.path('/Intermediates');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        IntermediateResource.save($scope.intermediate, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Intermediates");
    };
});