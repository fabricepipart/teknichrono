angular.module('frontend').factory('ChronoPointResource', function($resource){
    var resource = $resource('rest/chronopoints/:ChronoPointId',{ChronoPointId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});