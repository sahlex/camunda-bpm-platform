"use strict";

define(["angular"], function(angular) {

  var module = angular.module("cockpit.pages");

  var Controller = function($rootScope, $scope, $location, Errors, Authentication) {

    if (Authentication.current()) {
      $location.path("/overview");
    }

    $scope.login = function () {
      Authentication.login($scope.username, $scope.password).then(function(success) {
        Errors.removeAllErrors();
        
        if (success) {
          $rootScope.$broadcast("cockpit.reload");
          Errors.add({ type: "success", status: "Login", message: "Login successful", duration: 10000 });

          $location.path("/overview");
        } else {
          Errors.add({ status: "Login Failed", message: "Username / password are incorrect" });
        }
      });
    }
  };

  Controller.$inject = ["$rootScope", "$scope", "$location", "Errors", "Authentication"];

  var RouteConfig = function($routeProvider) {
    $routeProvider.when("/login", {
      templateUrl: "pages/login.html",
      controller: Controller
    });
  };

  RouteConfig.$inject = [ "$routeProvider"];

  module
    .config(RouteConfig)
    .controller("LoginController", Controller);

});