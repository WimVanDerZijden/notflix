(function () {
  "use strict";
  
  var $ = jQuery;
  
  var notflix = angular.module('notflix', ['ngRoute','angularLocalStorage']);
  
  notflix.config(['$routeProvider','$httpProvider',
    function($routeProvider, $httpProvider) {
    console.log("Module notflix config");
	  $routeProvider
	    .when('/movie', {
	      templateUrl: 'partials/movie-search.html'
      })
      .when('/movie/:imdbId', {
        templateUrl: 'partials/movie-detail.html'
      })
      .otherwise({
        redirectTo: '/movie'
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
	}]);
  
  notflix.run(['$http',
    function($http) {
      console.log("Module notflix run");
      /* Needed because Angular defaults to Content-type: application/json.
       * Reference: http://stackoverflow.com/a/11443066/2947592 */
      $http.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8;';
      $http.defaults.headers.put['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8;';
      /*$http.defaults.headers.delete = { 'Content-Type' : 'application/x-www-form-urlencoded; charset=UTF-8;'};*/
  }]);
  
  notflix.controller('MainCtrl', ['$scope','$cookies',
    function($scope, $cookies) {
      console.log("Controller MainCtrl init");
      $scope.signedIn = function() {
        return $cookies.token;
      };
  }]);

  notflix.controller('MovieDetailCtrl', ['$scope', '$routeParams', '$http', '$location',
    function($scope, $routeParams, $http, $location) {
      console.log("Controller MovieDetailCtrl init");
      $scope.userMessage = "Getting movie info...";
      
      $scope.back = function (path) {
        $location.path('/movie');
      };
      
  	  $http.get('resources/movie/' + $routeParams.imdbId )
  	  .success(function(data) {
  	    $scope.movie = data;
  	    $scope.userMessage = "";
  	    initStarbox($scope.movie.imdbID, $scope.movie.rating / 2);
  	  })
  	  .error(function() {
  	    $scope.movie = undefined;
  	    $scope.userMessage = "Movie not found"
  	  });
  	  
      var initStarbox = function (imdbID, stars) {
        new Starbox("star-widget", stars, {
          rerate: true,
          buttons: 10,
          onRate: function (element, memo) {
            submitRating(imdbID, memo.rated * 2, stars);
          }
        });
      };
      
      var submittingRating = false;
      /** Submit a rating */
      var submitRating = function (imdbID, halfStars, oldHalfStars) {
        if (submittingRating) {
          return;
        }
        submittingRating = true;
        var method = oldHalfStars > 0 ? "put" : "post";
        $http({
          url: "resources/rating/" + imdbID,
          method: method,
          data: $.param({ halfStars: halfStars })
        })
        .success(function (data) {
          $scope.movie.rating = data["halfStars"];
        })
        .finally(function () {
          initStarbox(imdbID, $scope.movie.rating / 2);
          submittingRating = false;
        });
      }
      
      /* Delete a rating */
      $scope.deleteRating = function() {
        $http.delete('resources/rating/' + $scope.movie.imdbID)
        .success(function() {
          $scope.movie.rating = 0;
          initStarbox($scope.movie.imdbID, 0);
        });
      };
      
  }]);
  
  notflix.controller('SignInCtrl', ['$scope', '$http', '$cookies', 'storage', '$rootScope','$route','$cacheFactory',
    function($scope, $http, $cookies, storage, $rootScope, $route, $cacheFactory) {
      console.log("Controller SignInCtrl init");
      $scope.invalidUserPass = false;
      var emptyParams = { username: '', password: '' };
      /* This creates a 2-way binding between localStorage and vars in the $scope */
      storage.bind($scope,'params', { defaultValue: emptyParams, storeName: 'signIn_Params' });
      storage.bind($scope,'rememberMe', { defaultValue: true });

      // Global sign in to call from outside of scope
      $rootScope.signIn = function() {
        $http.post('resources/session/login', $.param($scope.params))
        .success(function(data) {
          //$cookies.token = data["token"];
          // Plain javascript here, because we can't afford to wait for
          // angular to serialize this: the reload() must have the cookie immediately
          setCookie("token",data["token"],365);
          $route.reload();
        })
        .error(function() {
          $scope.params.password = "";
          $route.reload();
        });
      };
      
      function setCookie(c_name,value,exdays)
      {
        var exdate=new Date();
        exdate.setDate(exdate.getDate() + exdays);
        var c_value=escape(value) + 
          ((exdays==null) ? "" : ("; expires="+exdate.toUTCString()));
        document.cookie=c_name + "=" + c_value;
      }
      
      // Global sign out to call from outside of scope
      $rootScope.signOut = function() {
        console.log("Signing out and reloading page");
        delete $cookies["token"];
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
          setCookie("token",data["token"],365);
          $route.reload();
          $('#signin').modal('hide');
          if (!$scope.rememberMe) {
            $scope.params = emptyParams;
          }
        })
        .error(function() {
          $('#sign-in-btn').button('reset');
          $scope.invalidUserPass = true;
          $scope.params.password = "";
        });
      };
  }]);
  
  notflix.controller('SearchMoviesCtrl', [ '$http', '$scope', '$location', 'storage',
    function($http, $scope, $location, storage) {
      console.log("Controller MovieDetailCtrl init")
      $scope.size = 0;
      $scope.movies = [];
      $scope.error = false;
      $scope.searching = false;
      $scope.displayModus = 'detail';
      
      // Let the default page size depend on screen width
      var defaultPageSize;
      var width = $(window).width();
      if (width < 992)
        defaultPageSize = 12;
      else if (width < 1200)
        defaultPageSize = 24;
      else
        defaultPageSize = 48;
      
      /* This creates a 2-way binding between localStorage and $scope.params */
      storage.bind($scope,'params', {
        defaultValue: { q: '', sort: 'ImdbVotesDesc', page: 0, pageSize: defaultPageSize },
        storeName: 'searchMovies_Params'
      });
      /* Also bind the displayModus */
      storage.bind($scope,'displayModus', {
        defaultValue: 'thumb', storeName: 'searchMoviesDisplayModus'
      });
      
      var loadMovies = function() {
        $scope.userMessage = 'Searching...';
        $scope.size = 0;
        $scope.movies = [];
        $http.get('resources/movie', { params: $scope.params, cache: true })
        .success(function(data) {
          $scope.size = data["size"];
          $scope.movies = data["movies"];
          $scope.userMessage = '';
        })
        .error(function() {
          $scope.userMessage = 'Unable to connect to server';
        })
      };
      
      $scope.go = function (path) {
        $location.path('/movie/' + path);
      };
      
      var getLastPage = function() {
        return Math.ceil($scope.size / $scope.params.pageSize) - 1;
      }
      
      $scope.firstPage = function() {
        $scope.params.page = 0;
        loadMovies();
      };
      
      $scope.lastPage = function() {
        $scope.params.page = getLastPage();
        loadMovies();
      }
      
      $scope.nextPage = function() {
        if ($scope.params.page < getLastPage()) {
          $scope.params.page += 1;
          loadMovies();
        }
      };
      
      $scope.prevPage = function() {
        if ($scope.params.page > 0) {
          $scope.params.page -= 1;
          loadMovies();
        }
      };
      
      $scope.pageInfo = function() {
        var start = $scope.params.page * $scope.params.pageSize + 1;
        var end = ($scope.params.page + 1) * $scope.params.pageSize;
        if (start > $scope.size) {
          return "";
        }
        if (end > $scope.size) {
          end = $scope.size;
        }
        return "(showing " + start + " - " + end + ")";
      };
      
      // Load on initialization
      loadMovies();
    }]);

})();
