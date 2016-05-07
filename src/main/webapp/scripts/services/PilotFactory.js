angular.module('frontend').factory('PilotResource', function($resource){
    var resource = $resource('rest/forge/pilots/:PilotId',{PilotId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});