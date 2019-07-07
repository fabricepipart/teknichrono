angular.module('frontend').factory('BeaconResource', function($resource){
    var resource = $resource('rest/beacons/:BeaconId',{BeaconId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});