'use strict';

angular.module('frontend', ['ngRoute', 'ngResource'])
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider
      .when('/', { templateUrl: 'views/landing.html', controller: 'LandingPageController' })
      .when('/Beacons', { templateUrl: 'views/Beacon/search.html', controller: 'SearchBeaconController' })
      .when('/Beacons/new', { templateUrl: 'views/Beacon/detail.html', controller: 'NewBeaconController' })
      .when('/Beacons/edit/:BeaconId', { templateUrl: 'views/Beacon/detail.html', controller: 'EditBeaconController' })
      .when('/Categories', { templateUrl: 'views/Category/search.html', controller: 'SearchCategoryController' })
      .when('/Categories/new', { templateUrl: 'views/Category/detail.html', controller: 'NewCategoryController' })
      .when('/Categories/edit/:CategoryId', { templateUrl: 'views/Category/detail.html', controller: 'EditCategoryController' })
      .when('/Chronometers', { templateUrl: 'views/Chronometer/search.html', controller: 'SearchChronometerController' })
      .when('/Chronometers/new', { templateUrl: 'views/Chronometer/detail.html', controller: 'NewChronometerController' })
      .when('/Chronometers/edit/:ChronometerId', { templateUrl: 'views/Chronometer/detail.html', controller: 'EditChronometerController' })
      .when('/Events', { templateUrl: 'views/Event/search.html', controller: 'SearchEventController' })
      .when('/Events/new', { templateUrl: 'views/Event/detail.html', controller: 'NewEventController' })
      .when('/Events/edit/:EventId', { templateUrl: 'views/Event/detail.html', controller: 'EditEventController' })
      .when('/lLapTimes', { templateUrl: 'views/LapTime/lsearch.html', controller: 'SearchlLapTimeController' })
      .when('/lLapTimes/new', { templateUrl: 'views/LapTime/ldetail.html', controller: 'NewlLapTimeController' })
      .when('/lLapTimes/edit/:LapTimeId', { templateUrl: 'views/LapTime/ldetail.html', controller: 'EditlLapTimeController' })
      .when('/Locations', { templateUrl: 'views/Location/search.html', controller: 'SearchLocationController' })
      .when('/Locations/new', { templateUrl: 'views/Location/detail.html', controller: 'NewLocationController' })
      .when('/Locations/edit/:LocationId', { templateUrl: 'views/Location/detail.html', controller: 'EditLocationController' })
      .when('/Pilots', { templateUrl: 'views/Pilot/search.html', controller: 'SearchPilotController' })
      .when('/Pilots/new', { templateUrl: 'views/Pilot/detail.html', controller: 'NewPilotController' })
      .when('/Pilots/edit/:PilotId', { templateUrl: 'views/Pilot/detail.html', controller: 'EditPilotController' })
      .when('/Pings', { templateUrl: 'views/Ping/search.html', controller: 'SearchPingController' })
      .when('/Pings/new', { templateUrl: 'views/Ping/detail.html', controller: 'NewPingController' })
      .when('/Pings/edit/:PingId', { templateUrl: 'views/Ping/detail.html', controller: 'EditPingController' })
      .when('/Sessions', { templateUrl: 'views/Session/search.html', controller: 'SearchSessionController' })
      .when('/Sessions/new', { templateUrl: 'views/Session/detail.html', controller: 'NewSessionController' })
      .when('/Sessions/edit/:SessionId', { templateUrl: 'views/Session/detail.html', controller: 'EditSessionController' })
      .when('/LapTimes', { templateUrl: 'views/LapTime/search.html', controller: 'LapTimeController' })
      .otherwise({
        redirectTo: '/'
      });
  }])
  .controller('LandingPageController', function LandingPageController() {
  })
  .controller('NavController', function NavController($scope, $location) {
    $scope.matchesRoute = function (route) {
      var path = $location.path();
      return (path === ("/" + route) || path.indexOf("/" + route + "/") == 0);
    };
  });
