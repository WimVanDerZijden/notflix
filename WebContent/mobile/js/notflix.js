"use strict";

$(document).on("pageinit", "#home-page", function () {
  $.ajaxSetup({
    headers: { Accept: "application/json" }
  });
  
  var lastQuery;
  
  function loadMovies() {
	var query = $("#movies-input").val();
	if (query === lastQuery) {
	  return;
	}
	lastQuery = query;
    var $ul = $("#listview-movies");
    $ul.html("");
    if (query && query.length > 2) {
      $ul.html("<li><div class='ui-loader'><span class='ui-icon ui-icon-loading'></span></div></li>");
      $ul.listview("refresh");
      $.ajax({
        url: "../resources/movie",
        data: {
          q: query
        },
        success: function (data) {
          var listHtml = "",
            n;
          for (n = 0; n < data.length; n += 1) {
            listHtml += "<li>" + data[n]["title"] + "(" + data[n]["editDistance"] + ")</li>";
          }
          $ul.html(listHtml);
          $ul.listview("refresh");
        },
        error: function () {
          $ul.html("<li>Error connecting to server</li>");
        }
      });
    }
  }

  $("#movies-input").keyup(function (e) {
	loadMovies();
  });
  
  // The search field may be populated already on load
  loadMovies();
  
});