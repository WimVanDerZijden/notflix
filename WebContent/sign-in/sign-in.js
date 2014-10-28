(function() {
  "use strict";
  
  var $ = jQuery;

  angular.module('notflix.signIn', ['angularLocalStorage'])

  .directive('signIn', function() {
    return {
      restrict: 'E',
      templateUrl: 'sign-in/sign-in-directive.html',
      controller: 'SignInCtrl'
    }
  })
  
  .controller('SignInCtrl', ['$scope', '$http', 'storage', '$rootScope','$route','$cacheFactory',
    function($scope, $http, storage, $rootScope, $route, $cacheFactory) {
      console.log("Controller SignInCtrl init");
      $scope.invalidUserPass = false;
      $scope.toggle = true;
      
      storage.bind($scope,'params', { defaultValue: {username: '', password: ''}, storeName: 'signIn_Params' }); 
      storage.bind($scope,'rememberMe', { defaultValue: true , storeName: 'signIn_rememberMe'});
      
      // Global sign in to call from outside of scope
      $rootScope.signIn = function() {
        $http.post('resources/session/login', $.param($scope.params))
        .success(function(data) {
          //$cookies.token = data["token"];
          // We use jquery here, because we can't afford to wait for
          // angular to serialize this: the reload() must have the cookie immediately
          $.cookie("token", data["token"], { path: "/"} );
          $route.reload();
        })
        .error(function() {
          $scope.params.password = "";
          $route.reload();
        });
      };
      
      // Global sign out to call from outside of scope
      $rootScope.signOut = function() {
        console.log("Signing out and reloading page");
        $.removeCookie("token", { path: "/"});
        $scope.params.password = "";
        // Remove the cache to force a reload
        $cacheFactory.get("$http").removeAll();
        $route.reload();
      }
      
      // Local sign in
      $scope.signIn = function() {
        /* The browser might autocomplete the password. Angular will not notice this */
        $scope.params.password = $('#signin input[name=password]').val();
        $('#sign-in-btn').button('loading');
        /* $.param() is needed because Angular doesn't default to form-encoded params like jQuery's $ajax
         * Reference: http://stackoverflow.com/a/11443066/2947592 */
        $http.post('resources/session/login', $.param($scope.params))
        .success(function(data) {
          $('#sign-in-btn').button('reset');
          $scope.invalidUserPass = false;
          // We use jquery here, because we can't afford to wait for
          // angular to serialize this: the reload() must have the cookie immediately
          $.cookie("token", data["token"], { path: "/"} );
          $route.reload();
          $('#signin').modal('hide');
          if (!$scope.rememberMe) {
            $scope.params = {username: '', password: ''};
          }
        })
        .error(function() {
          $('#sign-in-btn').button('reset');
          $scope.invalidUserPass = true;
          $scope.params.password = "";
        });
      };
       
      var initRegParams = function() {
        $scope.regParams = {
          username : '',
          firstName: '',
          namePrepositions: '',
          lastName: '',
          password: ''
        }
      };
      
      initRegParams();
      
      $scope.invalidRegister = false;
      
      $scope.register = function () {
        $http.post('resources/user', $.param($scope.regParams))
        .success(function() {
          $('#register-btn').button('reset');
          $scope.invalidRegister = false;
          $scope.params.username = $scope.regParams.username;
          initRegParams();
          $scope.toggle = !$scope.toggle;
        })
        .error(function() {
          $('#register-btn').button('reset');
          $scope.invalidRegister = true;
        });
      };
      
      var escapeRegExp = function(str) {
        if (str === undefined || str === null)
          return '';
        return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
      }
      
      $scope.repeatPasswordPattern = function() {
        // To "abuse" the pattern attribute: the regex to match the string exactly.
        return '^' + escapeRegExp($scope.regParams.password) + '$';
      };
  }]);

})();