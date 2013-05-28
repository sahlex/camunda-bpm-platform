"use strict";

define(["angular"], function(angular, $) {

  var module = angular.module("cockpit.common");

    var LogoutController = function($scope, $location, Errors, Authentication) {

    $scope.logout = function() {
      Authentication.logout().then(function(success) {
        Errors.removeAllErrors();
        Errors.add({ status: "Logout", message: "You have been logged out" });
        $location.path("/login");
      });
    };
  };

  LogoutController.$inject = ["$scope", "$location", "Errors", "Authentication"];

  module
    .controller("LogoutController", LogoutController);

});