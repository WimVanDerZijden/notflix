(function () {
  
  var notflix = angular.module('notflix', ['ngRoute','angularLocalStorage']);
  
  notflix.config(['$routeProvider',
    function($routeProvider) {
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
	}]);
  
  notflix.controller('MainCtrl', ['$scope', function($scope) {
    $scope.pagetitle = "Notflix";
  }]);
    
  notflix.controller('MovieDetailCtrl', ['$scope', '$routeParams', '$http',
    function($scope, $routeParams, $http) {
	  $http.get('resources/movie/' + $routeParams.imdbId, { cache: true })
	  .success(function(data) {
		$scope.movie = data;
	  });
  }]);
  
  notflix.controller('SearchMoviesCtrl', [ '$http', '$scope', '$location', 'storage', function($http, $scope, $location, storage) {
    $scope.size = 0;
    $scope.movies = [];
    $scope.error = false;
    $scope.searching = false;
    $scope.displayModus = 'detail';
    
    // The default page size is dependent on screen width
    var defaultPageSize;
    var width = $(window).width();
    if (width < 992)
      defaultPageSize = 12;
    else if (width < 1200)
      defaultPageSize = 24;
    else
      defaultPageSize = 48;
    
    /* This creates a 2-way binding between localStorage.searchMoviesParams and $scope.params */
    storage.bind($scope,'params', {
      defaultValue: {
        q: '',
        sort: 'ImdbVotesDesc',
        page: 0,
        pageSize: defaultPageSize
      },
      storeName: 'searchMoviesParams'
    });
    /* Also bind the displayModus */
    storage.bind($scope,'displayModus', {
      defaultValue: 'thumb', storeName: 'searchMoviesDisplayModus'
    });
    
    var loadMovies = function() {
      $scope.searching = true;
      $scope.size = 0;
      $scope.movies = [];
      $http.get('resources/movie',
          { params: $scope.params, cache: true } )
      .success(function(data) {
        $scope.size = data["size"];
        $scope.movies = data["movies"];
        $scope.error = false;
      })
      .error(function() {
        $scope.error = true;
      })
      .finally(function() {
        $scope.searching = false;
      });
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
