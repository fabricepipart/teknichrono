'use strict';

angular.module('frontend',['ngRoute','ngResource'])
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/',{templateUrl:'views/landing.html',controller:'LandingPageController'})
      .when('/Beacons',{templateUrl:'views/Beacon/search.html',controller:'SearchBeaconController'})
      .when('/Beacons/new',{templateUrl:'views/Beacon/detail.html',controller:'NewBeaconController'})
      .when('/Beacons/edit/:BeaconId',{templateUrl:'views/Beacon/detail.html',controller:'EditBeaconController'})
      .when('/Chronometers',{templateUrl:'views/Chronometer/search.html',controller:'SearchChronometerController'})
      .when('/Chronometers/new',{templateUrl:'views/Chronometer/detail.html',controller:'NewChronometerController'})
      .when('/Chronometers/edit/:ChronometerId',{templateUrl:'views/Chronometer/detail.html',controller:'EditChronometerController'})
      .when('/Events',{templateUrl:'views/Event/search.html',controller:'SearchEventController'})
      .when('/Events/new',{templateUrl:'views/Event/detail.html',controller:'NewEventController'})
      .when('/Events/edit/:EventId',{templateUrl:'views/Event/detail.html',controller:'EditEventController'})
      .when('/Intermediates',{templateUrl:'views/Intermediate/search.html',controller:'SearchIntermediateController'})
      .when('/Intermediates/new',{templateUrl:'views/Intermediate/detail.html',controller:'NewIntermediateController'})
      .when('/Intermediates/edit/:IntermediateId',{templateUrl:'views/Intermediate/detail.html',controller:'EditIntermediateController'})
      .when('/LapTimes',{templateUrl:'views/LapTime/search.html',controller:'SearchLapTimeController'})
      .when('/LapTimes/new',{templateUrl:'views/LapTime/detail.html',controller:'NewLapTimeController'})
      .when('/LapTimes/edit/:LapTimeId',{templateUrl:'views/LapTime/detail.html',controller:'EditLapTimeController'})
      .when('/Pilots',{templateUrl:'views/Pilot/search.html',controller:'SearchPilotController'})
      .when('/Pilots/new',{templateUrl:'views/Pilot/detail.html',controller:'NewPilotController'})
      .when('/Pilots/edit/:PilotId',{templateUrl:'views/Pilot/detail.html',controller:'EditPilotController'})
      .when('/Pings',{templateUrl:'views/Ping/search.html',controller:'SearchPingController'})
      .when('/Pings/new',{templateUrl:'views/Ping/detail.html',controller:'NewPingController'})
      .when('/Pings/edit/:PingId',{templateUrl:'views/Ping/detail.html',controller:'EditPingController'})
      .otherwise({
        redirectTo: '/'
      });
  }])
  .controller('LandingPageController', function LandingPageController() {
  })
  .controller('NavController', function NavController($scope, $location) {
    $scope.matchesRoute = function(route) {
        var path = $location.path();
        return (path === ("/" + route) || path.indexOf("/" + route + "/") == 0);
    };
  });
