'use strict';

angular.module('jbossforgehtml5',['ngRoute','ngResource'])
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/',{templateUrl:'views/landing.html',controller:'LandingPageController'})
      .when('/Inventories',{templateUrl:'views/Inventory/search.html',controller:'SearchInventoryController'})
      .when('/Inventories/new',{templateUrl:'views/Inventory/detail.html',controller:'NewInventoryController'})
      .when('/Inventories/edit/:InventoryId',{templateUrl:'views/Inventory/detail.html',controller:'EditInventoryController'})
      .when('/Laps',{templateUrl:'views/Lap/search.html',controller:'SearchLapController'})
      .when('/Laps/new',{templateUrl:'views/Lap/detail.html',controller:'NewLapController'})
      .when('/Laps/edit/:LapId',{templateUrl:'views/Lap/detail.html',controller:'EditLapController'})
      .when('/Listings',{templateUrl:'views/Listing/search.html',controller:'SearchListingController'})
      .when('/Listings/new',{templateUrl:'views/Listing/detail.html',controller:'NewListingController'})
      .when('/Listings/edit/:ListingId',{templateUrl:'views/Listing/detail.html',controller:'EditListingController'})
      .when('/Members',{templateUrl:'views/Member/search.html',controller:'SearchMemberController'})
      .when('/Members/new',{templateUrl:'views/Member/detail.html',controller:'NewMemberController'})
      .when('/Members/edit/:MemberId',{templateUrl:'views/Member/detail.html',controller:'EditMemberController'})
      .when('/Pilots',{templateUrl:'views/Pilots/search.html',controller:'SearchPilotsController'})
      .when('/Pilots/new',{templateUrl:'views/Pilots/detail.html',controller:'NewPilotsController'})
      .when('/Pilots/edit/:PilotsId',{templateUrl:'views/Pilots/detail.html',controller:'EditPilotsController'})
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
