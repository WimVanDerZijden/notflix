(function() {
  "use strict";
  
  var $ = jQuery;
  
  angular.module('notflix.movieDetail', [])
  
  .directive('person', function() {
	  return {
		  restrict: 'E',
		  templateUrl: 'movie-detail/person.html',
		  scope: {
			  items: '=items',
			  collid: '=collid'
		  }
	  }
  })
  
  .controller('MovieDetailCtrl', ['$scope', '$routeParams', '$http', '$location',
    function($scope, $routeParams, $http, $location) {
      console.log("Controller MovieDetailCtrl init");
      $scope.userMessage = "Getting movie info...";
      
      $scope.back = function (path) {
        $location.path('/movie');
      };
      
      $scope.loadMovie = function loadMovie(){
	  	  $http.get('resources/movie/' + $routeParams.imdbId + '?lang=' + $scope.lang.selected)
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
      };
      $scope.loadMovie();
  	  /* This following method is just to demonstrate cross-domain ajax 
  	   * The image url we are retrieving is already in our model! */
  	  var loadImageFromOmdb = function() {
  		  /* This Works, but deactivated, because it is purely demonstrational
  	  $http.get('http://www.omdbapi.com', { params: { i: $scope.movie.imdbID }})
    	  .success(function(data) {
    	    console.log("Retrieved movie data from omdb api.");
    	    console.log(data);
    	  })
    	  .error(function() {
    	    console.log("Could not connect to the omdb api");
    	  });
    	  */
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
  
})();