(function() {
  "use strict";
  
  var $ = jQuery;
  
  angular.module('notflix.searchResults', ['angularLocalStorage'])
  
  .controller('SearchResultsCtrl', [ '$http', '$scope', '$location', 'storage','$cacheFactory',
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
       
       // Initialization code that has to be run from the child implementation:
       $scope.init = function() {
         /* This creates a 2-way binding between localStorage and $scope.params */
         storage.bind($scope,'params', {
           defaultValue: { q: '', sort: $scope.ctx.defaultSort, page: 0, pageSize: defaultPageSize },
           storeName: $scope.ctx.name + '_Params'
         });
         /* Also bind the displayModus */
         storage.bind($scope,'displayModus', { defaultValue: 'detail', storeName: $scope.ctx.name + '_DisplayModus' });
         // Initial load
         loadResults();
       }

  }]);

})();