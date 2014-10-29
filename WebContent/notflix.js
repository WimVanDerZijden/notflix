(function () {
  "use strict";
  
  var $ = jQuery;
  
 angular.module('notflix', [
    'ngRoute',
    'notflix.signIn',
    'notflix.movieResults',
    'notflix.userResults',
    'notflix.movieDetail'
  ])
  
  .config(['$routeProvider','$httpProvider',
    function($routeProvider, $httpProvider) {
      console.log("Module notflix config");
      
	  $routeProvider
	  .when('/movie/', {
	    templateUrl: 'search-results/search-results.html',
	    controller: 'MovieResultsCtrl'
      })
      .when('/user', {
        templateUrl: 'search-results/search-results.html',
        controller: 'UserResultsCtrl'
      })
      .when('/movie/:imdbId', {
        templateUrl: 'movie-detail/movie-detail.html',
        controller: 'MovieDetailCtrl'
      })
      .when('/', {
        redirectTo: '/movie'
      })
      .otherwise({
        template: '<h1>404 <small>not found</small><h1>'
      });
      
	    // Interceptor to handle the 401 Unauthorized response 
	    $httpProvider.interceptors.push(function($q, $cookies, $rootScope) {
	      return {
	        'responseError': function(rejection) {
	          if (rejection.status === 401) {
	            // If I have a token, the 401 was caused by invalid token
	            if ($cookies.token) {
	              console.log("Invalid token. Re-attempting login");
	              // Remove the cookie and try to get a new one.
	              delete $cookies["token"];
	              $rootScope.signIn();
	            }
	            // Otherwise, it was caused by a failed login
	            // No action required - this is handled by the SignInCtrl
	          }
	          return $q.reject(rejection);
	        }
	      };
	    });
	}])
  
  .run(['$http',
    function($http) {
      console.log("Module notflix run");
      /* Needed because Angular defaults to Content-type: application/json.
       * Reference: http://stackoverflow.com/a/11443066/2947592 */
      $http.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8;';
      $http.defaults.headers.put['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8;';
      /*$http.defaults.headers.delete = { 'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8;'};*/
  }])
  
  .controller('NotflixCtrl', ['$scope','$cookies',
    function($scope, $cookies) {
      console.log("Controller NotflixCtrl init");
      $scope.signedIn = function() {
        return $cookies.token;
      };
  }]);

})();
