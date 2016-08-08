angular.module('frontend').factory('LapTimeResource', function($resource){
    var resource = $resource('rest/laptimes/:LapTimeId',{LapTimeId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});