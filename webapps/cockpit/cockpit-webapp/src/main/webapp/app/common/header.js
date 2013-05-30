"use strict";

define(["angular"], function(angular, $) {

  var module = angular.module("cockpit.common");

    var LogoutController = function($scope, $location, Notifications, Authentication) {

    $scope.logout = function() {
      Authentication.logout().then(function(success) {
        Notifications.clear();
        Notifications.add({ status: "Logout", message: "You have been logged out" });
        $location.path("/login");
      });
    };
  };

  LogoutController.$inject = ["$scope", "$location", "Notifications", "Authentication"];

  module
    .controller("LogoutController", LogoutController);

});