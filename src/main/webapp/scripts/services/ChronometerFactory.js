angular.module('frontend').factory('ChronometerResource', function($resource){
    var resource = $resource('rest/chronometers/:ChronometerId',{ChronometerId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});