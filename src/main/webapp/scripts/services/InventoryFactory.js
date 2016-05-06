angular.module('jbossforgehtml5').factory('InventoryResource', function($resource){
    var resource = $resource('rest/inventories/:InventoryId',{InventoryId:'@id'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});