angular.module('frontend').factory('RaspberryResource', function($resource){
    var resource = $resource('rest/raspberries/:RaspberryId',{RaspberryId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});