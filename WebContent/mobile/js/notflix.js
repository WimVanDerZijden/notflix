$(document).on("pageinit", "#home-page", function () {
  "use strict";
  
  // *** The model ***
  
  var model = (function() {
    var movies = [];
    var begin = 0;
    var limit = 10;
    
    function setMovies(data) {
      movies = data;
      if (movies.length < limit) {
        $("#next-movies-search").hide();
      } else {
        $("#next-movies-search").show();
      }
    }
    
    function getMovies() {
      return movies;
    }
    
    function nextPage() {
      begin += 10;
      if (begin > 0) {
        $("#previous-movies-search").show();
      } else {
        $("#previous-movies-search").hide();
      }
    }
    
    function getBegin() {
      return begin;
    }
    
    function getLimit() {
      return limit;
    }
    
    return {
      setMovies: setMovies,
      getMovies: getMovies,
      nextPage: nextPage,
      getBegin: getBegin,
      getLimit: getLimit
    };
    
  })();
  
  // *** Global config vars ***
  
  var MIN_QUERY_LENGTH = 1;
  
  // *** BASIC UI STUFF ***
  
  function checkLoggedIn() {
    if ($.cookie("token")) {
      $("#signout-popup-anchor").show();
      $("#signin-popup-anchor").hide();
    } else {
      $("#signout-popup-anchor").hide();
      $("#signin-popup-anchor").show();
    }
  }
  
  checkLoggedIn();
  
  /** Hide error message when sign-in popup is opened */
  $('#signin-popup-anchor').click(function (e) {
    $('#signin .error').hide();
  });
  
  $("#signout-popup-anchor").click(function (e) {
    $.removeCookie("token", { path: "/"});
    checkLoggedIn();
  })
  
  $("#next-movies-search").click(function (e) {
    model.nextPage();
    loadMovies();
  })
  
  // *** AJAX ***
  // Tell the server to feed us json
  $.ajaxSetup({
    headers: { Accept: "application/json" }
  });

  // Set up a global error handler for ajax.
  $(document).ajaxError(function( event, jqxhr, settings, thrownError ) {
    console.log("Error raised");
    if (jqxhr.status == 401) {
      // This means our token was invalidated
      // Remove it and display a dialog.
      $.removeCookie("token", { path: "/"});
      $("#token-invalidated-popup").popup("open");
    } else {
      $('#server-error-popup').popup("open");
    }
  });
  
  var lastQuery = "";
  

  /** Load movies */
  function loadMovies () {
  	var query = $("#movies-input").val().trim();
  	/*
  	if (lastQuery.length >= MIN_QUERY_LENGTH && query.contains(lastQuery)) {
  	  // If the new query contains the last query, the result-set is 
  	  // a subset of the previous result. So we can filter this client side.
  	  // No need for another Ajax-call.
  	  return;
  	}*/
    var $ul = $("#listview-movies");
    $ul.html("");
    lastQuery = query;
    if (query.length >= MIN_QUERY_LENGTH) {
      lastQuery = query;
      $ul.html("<li><div class='ui-loader'><span class='ui-icon ui-icon-loading'></span></div></li>");
      $ul.listview("refresh");
      $.ajax({
        url: "../resources/movie",
        data: {
          q: query,
          sort: "TitleAsc",
          limit: model.getLimit(),
          begin: model.getBegin()
        },
        success: function (data) {
          model.setMovies(data);
          var html = "",
            n;
          for (n = 0; n < data.length; n += 1) {
            html += "<li class='ui-btn' id='" + data[n]["imdbID"]+ "'>"
              + "<img alt='film poster' src='" + data[n]["poster"] + "' onError=\"this.onerror=null;this.src='media/no-image.svg';\">"
              + "<h3>" + data[n]["title"] + " (" + getYear(data[n]["released"]) + ")</h3>"
              + "<p>" + data[n]["plot"] + "</p>"
              + "</li>";
          }
          $ul.html(html);
          $ul.listview("refresh");
        },
        error: function (data) {
          lastQuery = "";
        }
      });
    }
  };
  
  loadMovies();
  
  $('#search-tab .ui-input-clear').click(function (e) {
    loadMovies();
  });
  
  $("#movies-input").donetyping(function () {
    loadMovies();
  }, 500);
  
  /** Perform the login */
  $('#form-signin').submit(function (e) {
    var $this = $(this);
    e.preventDefault();
    $.mobile.loading("show")
    $.ajax({
      type: "post",
      url: "../resources/session/login",
      data: $(this).serialize(),
      success: function(data) {
        $.cookie("token", data["token"], { path: "/"} );
        $('#signin').popup("close");        
        $.mobile.loading("hide");
        checkLoggedIn();
      },
      error: function(data) {
        $('#signin .error').show();
        $.mobile.loading("hide");   
      }
    });
  });
  
  // *** UTILITY ***
  
  /** Retrieve the year from Unix time */
  function getYear(unixTime) {
    var date = new Date(unixTime * 1000);
    return date.getFullYear();
  }
  
});
