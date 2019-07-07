angular.module('frontend').factory('LapTimeResource', function ($resource) {
    var resource = $resource('rest/laptimes/:LapTimeId:SearchType', { LapTimeId: '@id' }, {
        'queryAll': { method: 'GET', isArray: true },
        'query': { method: 'GET', isArray: false },
        'update': { method: 'PUT' }
    });
    return resource;
});
angular.module('frontend').factory('LapTimeExportResource', function ($resource) {
    var resource = $resource('rest/laptimes/csv/:SearchType', {}, {
        'queryAll': {
            method: 'GET', isArray: false, transformResponse: function (data) { return { csv: data.toString() } }
        }
    });
    return resource;
});