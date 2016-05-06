angular.module('jbossforgehtml5').factory('LapResource', function($resource){
    var resource = $resource('rest/laps/:LapId',{LapId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});