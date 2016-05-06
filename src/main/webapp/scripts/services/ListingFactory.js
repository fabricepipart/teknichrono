angular.module('jbossforgehtml5').factory('ListingResource', function($resource){
    var resource = $resource('rest/listings/:ListingId',{ListingId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});