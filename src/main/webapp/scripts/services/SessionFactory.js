angular.module('frontend').factory('SessionResource', function ($resource) {
    var resource = $resource('rest/sessions/:SessionId', { SessionId: '@id', ChronoId: '@chronoId', PilotId: '@pilotId', },
        {
            'queryAll': { method: 'GET', isArray: true },
            'query': { method: 'GET', isArray: false },
            'current': { method: 'GET', url: 'rest/sessions/current' },
            'update': { method: 'PUT' },
            'addChronometer': { method: 'POST', url: 'rest/sessions/:SessionId/addChronometer?chronoId=:ChronoId' },
            'addPilot': { method: 'POST', url: 'rest/sessions/:SessionId/addPilot?pilotId=:PilotId' },
            'start': { method: 'POST', url: 'rest/sessions/:SessionId/start' },
            'end': { method: 'POST', url: 'rest/sessions/:SessionId/end' }
        });
    return resource;
});