angular.module('frontend').factory('ChronometerResource', function ($resource) {
    var resource = $resource('rest/chronometers/:ChronometerId', { ChronometerId: '@id' }, {
        'queryAll': {
            method: 'GET', isArray: true, transformResponse: function (data) {
                var items = angular.fromJson(data);
                angular.forEach(items, function (chrono) {
                    chrono['inactivityWindow'] = moment.duration(chrono['inactivityWindow']).seconds();
                });
                return items;
            }
        },
        'get': {
            method: 'GET', isArray: false, transformResponse: function (data) {
                var chrono = angular.fromJson(data);
                chrono['inactivityWindow'] = moment.duration(chrono['inactivityWindow']).seconds();
                return chrono
            }
        },
        'update': {
            method: 'PUT', transformRequest: function (chrono) {
                chrono['inactivityWindow'] = moment.duration(chrono['inactivityWindow'], 's').toISOString();
                return angular.toJson(chrono);
            }
        }
    });
    return resource;
});