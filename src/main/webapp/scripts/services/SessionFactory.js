angular.module('frontend').factory('SessionResource', function ($resource) {
    var resource = $resource('rest/sessions/:SessionId', { SessionId: '@id' },
        {
            'queryAll': { method: 'GET', isArray: true },
            'query': { method: 'GET', isArray: false },
            'update': { method: 'PUT' },
            'start': { method: 'POST', url: 'rest/sessions/:SessionId/start' },
            'end': { method: 'POST', url: 'rest/sessions/:SessionId/end' }
        });
    return resource;
});