angular.module('frontend').factory('LogsResource', function ($resource) {
    var resource = $resource('rest/logs/', { ChronoId: '@chronoId' }, {
        'queryAll': {
            method: 'GET', isArray: true, url: 'rest/logs:ChronoId'
        }
    });
    return resource;
});