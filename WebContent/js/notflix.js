(function () {
  "use strict";
  
  var $ = jQuery;
  
  var notflix = angular.module('notflix', ['ngRoute','angularLocalStorage']);
  
  notflix.config(['$routeProvider','$httpProvider',
    function($routeProvider, $httpProvider) {
      console.log("Module notflix config");
      
	    $routeProvider
	    .when('/movie/', {
	      template: '<search-results></search-results>',
	      controller: 'SearchMoviesCtrl'
      })
      .when('/movie/:imdbId', {
        templateUrl: 'partials/movie-detail.html',
        controller: 'MovieDetailCtrl'
      })
      .when('/user', {
        template: '<search-results></search-results>',
        controller: 'SearchUsersCtrl'
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

  notflix.directive('searchResults', function() {
    return {
      restrict: 'E',
      templateUrl: 'partials/search-results.html',
      controller: 'SearchResultsCtrl'
    };
  });
  
  notflix.directive('resultsView', function() {
    function getTemplateUrl(type) {
      if (type === 'searchMovies') {
        return 'partials/movie-results.html'
      }
      else if (type === 'searchUsers') {
        return 'partials/user-results.html';
      }
    }
    return {
      restrict: 'E',
      templateUrl: function(element, attr) {
        return getTemplateUrl(attr.ctxName);
      }
    };
  });
  
  /* This sets the movies ctx for the generic SearchResultsCtrl */
  notflix.controller('SearchMoviesCtrl', ['$scope',
    function($scope) {
      console.log("Controller SearchMoviesCtrl init");
      $scope.ctx = {
        defaultSort: 'ImdbVotesDesc',
        name: 'searchMovies',
        url: 'resources/movie',
        resultsName: 'movies',
        detailPath: '/movie/',
        queryPlaceholder: 'Search Movies...',
        sortOrders: [
          ['TitleAsc', 'Title (A-Z)'],
          ['TitleDesc', 'Title (Z-A)'],
          ["ImdbRatingDesc",'IMDb Rating'],
          ['ImdbVotesDesc','IMDb Votes'],
          ['ReleasedDesc','Newer first'],
          ['ReleasedAsc','Older first']
        ],
        displayModes: [
          ['thumb','Show Posters'],
          ['detail','Show Detail']
        ]
      }
  }]);
  
  /* This sets the users ctx for the generic SearchResultsCtrl */
  notflix.controller('SearchUsersCtrl', ['$scope',
    function($scope) {
      console.log("Controller SearchUsersCtrl init");
      $scope.ctx = {
        defaultSort: 'NameAsc',
        name: 'searchUsers',
        url: 'resources/user',
        resultsName: 'users',
        detailPath: '/user/',
        queryPlaceholder: 'Search Users...',
        sortOrders: [
          ['NameAsc', 'Name (A-Z)'],
          ['NameDesc', 'Name (Z-A)']
        ],
        displayModes: [
          ['detail','Show Detail']
        ]
      }
  }]);

  notflix.controller('SearchResultsCtrl', [ '$http', '$scope', '$location', 'storage','$cacheFactory',
    function($http, $scope, $location, storage, $cacheFactory) {
       console.log("Controller SearchResultsCtrl init");
       $scope.size = 0;
       $scope.movies = [];
       $scope.error = false;
       $scope.searching = false;
       
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
         defaultValue: { q: '', sort: $scope.ctx.defaultSort, page: 0, pageSize: defaultPageSize },
         storeName: $scope.ctx.name + '_Params'
       });
       /* Also bind the displayModus */
       storage.bind($scope,'displayModus', { defaultValue: 'detail', storeName: $scope.ctx.name + '_DisplayModus' });
       
       var loadResults = function() {
         $scope.userMessage = 'Searching...';
         $scope.size = 0;
         $scope.movies = [];
         $http.get($scope.ctx.url, { params: $scope.params, cache: true })
         .success(function(data) {
           $scope.size = data["size"];
           $scope.results = data[$scope.ctx.resultsName];
           $scope.userMessage = '';
         })
         .error(function() {
           $scope.userMessage = 'Unable to connect to server';
         });
       };
       
       $scope.go = function (path) {
         $location.path('/movie/' + path);
       };
       
       $scope.switchSearch = function (type) {
         if (type === 'searchUsers'){
           $location.path('/user');
         } else if (type === 'searchMovies'){
           $location.path('/movie');
         }
       }
       
       var getLastPage = function() {
         return Math.ceil($scope.size / $scope.params.pageSize) - 1;
       }
       
       $scope.firstPage = function() {
         $scope.params.page = 0;
         loadResults();
       };
       
       $scope.lastPage = function() {
         $scope.params.page = getLastPage();
         loadResults();
       }
       
       $scope.nextPage = function() {
         if ($scope.params.page < getLastPage()) {
           $scope.params.page += 1;
           loadResults();
         }
       };
       
       $scope.prevPage = function() {
         if ($scope.params.page > 0) {
           $scope.params.page -= 1;
           loadResults();
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
       loadResults();

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
  	    initStarboxAvg($scope.movie.avgRating / 2, $scope.movie.votes);
  	    // Non-functional, just to demonstrate cross-domain ajax
  	    loadImageFromOmdb();
  	  })
  	  .error(function() {
  	    $scope.movie = undefined;
  	    $scope.userMessage = "Movie not found"
  	  });
  	  
  	  /* This following method is just to demonstrate cross-domain ajax 
  	   * The image url we are retrieving is already in our model! */
  	  var loadImageFromOmdb = function() {
  	  $http.get('http://www.omdbapi.com', { params: { i: $scope.movie.imdbID }})
    	  .success(function(data) {
    	    console.log("Retrieved movie data from omdb api.");
    	    console.log(data);
    	  })
    	  .error(function() {
    	    console.log("Could not connect to the omdb api");
    	  });
  	  };
  	  
  	  /* The starbox ui for voting */
      var initStarbox = function (imdbID, stars) {
        var indicator = stars > 0 ? 'your rating' : 'rate this movie';
        new Starbox("star-widget", stars, {
          rerate: true,
          buttons: 10,
          ghosting: true,
          indicator: indicator,
          onRate: function (element, memo) {
            submitRating(imdbID, memo.rated * 2, stars);
          }
        });
      };

      /* The static starbox for showing the average rating */
      var initStarboxAvg = function (stars, votes) {
        var indicator = stars > 0 ? '#{average} stars from #{total} users' : 'not enough votes';
        new Starbox("star-widget-avg", stars, {
          locked: true,
          overlay: 'big.png',
          color: 'gold',
          total: votes,
          indicator: indicator
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
      $scope.toggle = true;
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
      
      function setCookie(c_name,value,exdays) {
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
      
      $scope.regParams = {
        username : '',
        firstName: '',
        namePrepositions: '',
        lastName: '',
        password: ''
      }
      
      $scope.invalidRegister = false;
      
      $scope.register = function () {
        $http.post('resources/user', $.param($scope.regParams))
        .success(function() {
          $('#register-btn').button('reset');
          $scope.invalidRegister = false;
          $scope.params.username = $scope.regParams.username;
          $scope.toggle = !$scope.toggle;
        })
        .error(function() {
          $('#register-btn').button('reset');
          $scope.invalidRegister = true;
        });
      };
      
      $scope.escapeRegExp = function(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
      }
  }]);
  
})();
