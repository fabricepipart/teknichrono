angular.module('frontend').factory('IntermediateResource', function($resource){
    var resource = $resource('rest/intermediates/:IntermediateId',{IntermediateId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});