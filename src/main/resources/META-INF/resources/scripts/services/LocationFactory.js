angular.module('frontend').factory('LocationResource', function($resource){
    var resource = $resource('rest/locations/:LocationId',{LocationId:'@id'},{
        'queryAll': {
            method: 'GET', isArray: true, transformResponse: function (data) {
                var items = angular.fromJson(data);
                angular.forEach(items, function (location) {
                    var minDuration = moment.duration(location['minimum'])
                    location['minimum'] = minDuration.seconds() + (60 * minDuration.minutes()) + (3600 * minDuration.hours());
                    var maxDuration = moment.duration(location['maximum'])
                    location['maximum'] = maxDuration.seconds() + (60 * maxDuration.minutes()) + (3600 * maxDuration.hours());
                });
                return items;
            }
        },
        'get': {
            method: 'GET', isArray: false, transformResponse: function (data) {
                var location = angular.fromJson(data);
                var minDuration = moment.duration(location['minimum'])
                location['minimum'] = minDuration.seconds() + (60 * minDuration.minutes()) + (3600 * minDuration.hours());
                var maxDuration = moment.duration(location['maximum'])
                location['maximum'] = maxDuration.seconds() + (60 * maxDuration.minutes()) + (3600 * maxDuration.hours());
                return location
            }
        },
        'save': {
            method: 'POST', transformRequest: function (location) {
                location['minimum'] = moment.duration(location['minimum'], 's').toISOString();
                location['maximum'] = moment.duration(location['maximum'], 's').toISOString();
                return angular.toJson(location);
            }
        },
        'update': {
            method: 'PUT', transformRequest: function (location) {
                location['minimum'] = moment.duration(location['minimum'], 's').toISOString();
                location['maximum'] = moment.duration(location['maximum'], 's').toISOString();
                return angular.toJson(location);
            }
        }
    });
    return resource;
});