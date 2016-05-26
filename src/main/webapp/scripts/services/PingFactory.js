angular.module('frontend').factory('PingResource', function($resource){
    var resource = $resource('rest/pings/:PingId',{PingId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});