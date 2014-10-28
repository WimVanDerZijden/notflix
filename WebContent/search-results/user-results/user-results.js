(function () {
  "use strict";
  
  angular.module('notflix.userResults',['notflix.searchResults'])
  
  .directive('userResults', function() {
    return {
      restrict: 'E',
      templateUrl: 'search-results/user-results/user-results.html'
    };
  })
  
  .controller('UserResultsCtrl', ['$scope','$controller',
    function($scope,$controller) {
      // This defines SearchResultsCtrl as a parent controller
      // Reference: http://stackoverflow.com/a/20230720/2947592
      $controller('SearchResultsCtrl',{$scope: $scope});
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
      };
      // Init the parent controller
      $scope.init();
  }]);

})();