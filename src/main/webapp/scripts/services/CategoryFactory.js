angular.module('frontend').factory('CategoryResource', function($resource){
    var resource = $resource('rest/categories/:CategoryId',{CategoryId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});