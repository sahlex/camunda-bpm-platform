(function() {

  var cockpitCore = [
    'module:cockpit.pages:./pages/main',
    'module:cockpit.directives:./directives/main',
    'module:cockpit.resources:./resources/main',
    'module:cockpit.plugin:cockpit-plugin',
    'module:cockpit.common:./common/main' ];

  var commons = [
    'module:camunda.common.directives:camunda-common/directives/main',
    'module:camunda.common.extensions:camunda-common/extensions/main',
    'module:camunda.common.services:camunda-common/services/main' ];

  var plugins = PLUGIN_DEPENDENCIES || [];

  var dependencies = [ 'jquery', 'module:ng', 'module:ngResource', 'module:ngCookies' ].concat(commons, cockpitCore, plugins);

  ngDefine('cockpit', dependencies, function(module, $) {

    var ResponseErrorHandler = function(Notifications, Authentication, $location) {

      this.handlerFn = function(event, responseError) {
        var status = responseError.status,
            data = responseError.data;

        Notifications.clear({ type: "error" });

        switch (status) {
        case 500:
          if (data && data.message) {
            Notifications.addError({ status: "Error", message: data.message, exceptionType: data.exceptionType });
          } else {
            Notifications.addError({ status: "Error", message: "A problem occurred: Try to refresh the view or login and out of the application. If the problem persists, contact your administrator." });
          }
          break;
        case 0:
          Notifications.addError({ status: "Request Timeout", message:  "Your request timed out. Try refreshing the page." });
          break;
        case 401:
          if (Authentication.current()) {
            Notifications.addError({ status: "Unauthorized", message:  "Your session has expired. Please login again." });
          } else {
            Notifications.addError({ status: "Unauthorized", message:  "Login is required to access this page." });
          }

          Authentication.set(null);
          $location.path("/login");

          break;
        default:
          Notifications.addError({ status: "Error", message :  "A problem occurred: Try to refresh the view or login and out of the application. If the problem persists, contact your administrator." });
        }
      };
    };

    var Controller = function ($scope, $location, Notifications, Authentication) {

      $scope.auth = Authentication.auth;

      // needed for form validation
      // DO NOT REMOVE FROM DEFAULT CONTROLLER!
      $scope.errorClass = function(form) {
        return form.$valid || !form.$dirty ? '' : 'error';
      };

      $scope.$on("responseError", new ResponseErrorHandler(Notifications, Authentication, $location).handlerFn);

    };

    Controller.$inject = ['$scope',  '$location', 'Notifications', 'Authentication'];

    var ModuleConfig = function($routeProvider, $httpProvider, UriProvider) {
      $httpProvider.responseInterceptors.push('httpStatusInterceptor');
      $routeProvider.otherwise({ redirectTo: '/dashboard' });

      function getUri(id) {
        var uri = $("base").attr(id);
        if (!id) {
          throw new Error("Uri base for " + id + " could not be resolved");
        }

        return uri;
      }

      UriProvider.replace('app://', getUri("cockpit-base"));
      UriProvider.replace('plugin://', getUri("cockpit-base") + "plugin/");
      UriProvider.replace('engine://', getUri("engine-base"));
    };

    ModuleConfig.$inject = ['$routeProvider', '$httpProvider', 'UriProvider'];

    module
      .config(ModuleConfig)
      .controller('DefaultCtrl', Controller);

    return module;

  });

})();