(function () {
  
  var notflix = angular.module('notflix', ['ngRoute']);
  
  notflix.config(['$routeProvider',
    function($routeProvider) {
	  $routeProvider
	    .when('/movie', {
		  templateUrl: 'partials/movie-search.html',
          controller: 'SearchMoviesCtrl'
        })
        .when('/movie/:imdbId', {
          templateUrl: 'partials/movie-detail.html',
          controller: 'MovieDetailCtrl'
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
  
  notflix.controller('SearchMoviesCtrl', [ '$http', '$scope', function($http, $scope) {
    $scope.size = 0;
    $scope.movies = [];
    $scope.error = false;
    $scope.searching = false;
    
    $scope.params = {
        q: '',
        sort: 'TitleAsc',
        page: 0,
        pageSize: 10
    };
    
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
    
    $scope.firstPage = function() {
      $scope.params.page = 0;
      loadMovies();
    };
    
    $scope.nextPage = function() {
      var maxPage = Math.ceil($scope.size / $scope.params.pageSize) - 1;
      if ($scope.params.page < maxPage) {
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
