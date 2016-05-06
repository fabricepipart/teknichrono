angular.module('jbossforgehtml5').factory('PilotsResource', function($resource){
    var resource = $resource('rest/pilots/:PilotsId',{PilotsId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});