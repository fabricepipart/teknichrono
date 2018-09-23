angular.module('frontend').factory('PingResource', function ($resource) {
    var resource = $resource('rest/pings/:PingId', { PingId: '@id', ChronoId: '@cid', BeaconId: '@bid' },
        {
            'queryAll': { method: 'GET', isArray: true },
            'query': { method: 'GET', isArray: false },
            'update': { method: 'PUT' },
            'create': { method: 'POST', url: 'rest/pings/create' }
        });
    return resource;
});