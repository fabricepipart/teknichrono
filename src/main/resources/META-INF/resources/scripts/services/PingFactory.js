angular.module('frontend').factory('PingResource', function ($resource) {
    var resource = $resource('rest/pings/:PingId', { PingId: '@id', ChronoId: '@chronoId' },
        {
            'queryAll': { method: 'GET', isArray: true },
            'query': { method: 'GET', isArray: false },
            'update': { method: 'PUT' },
            'create': { method: 'POST', url: 'rest/pings/create' },
            'latest': { method: 'GET', isArray: false, url: 'rest/pings/latest?:ChronoId' },

        });
    return resource;
});