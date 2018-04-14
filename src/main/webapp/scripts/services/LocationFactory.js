angular.module('frontend').factory('LocationResource', function($resource){
    var resource = $resource('rest/locations/:LocationId',{LocationId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});