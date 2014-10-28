(function () {
  "use strict";
  
  angular.module('notflix.movieResults', ['notflix.searchResults'])
  
  .directive('movieResults', function() {
    return {
      restrict: 'E',
      templateUrl: 'search-results/movie-results/movie-results.html'
    };
  })

  .controller('MovieResultsCtrl', ['$scope','$controller',
    function($scope,$controller) {
      // This defines SearchResultsCtrl as a parent controller
      // Reference: http://stackoverflow.com/a/20230720/2947592
      $controller('SearchResultsCtrl',{$scope: $scope});
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
      // Init the parent controller
      $scope.init();
  }]);

  
})();