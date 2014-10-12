$(document).on("pageinit", "#home-page", function () {
  "use strict";
  console.log("Init page: #home-page");
  // MVC Revealing Module Pattern \\
  
  // *** MODEL *** \\
  
  var model = (function() {

    var movies = [];
    var size = 0;
    var page = 0;
    var pageSize = 10;
    var query = "";
    var sort = "TitleAsc";
    
    function setMovies(data) {
      movies = data["movies"];
      size = data["size"];
    }

    function clearMovies() {
      movies = [];
      size = 0;
      query = "";
      page = 0;
    }
    
    function getMovies() {
      return movies;
    }
    
    function getPage() {
      return page;
    }
    
    function getPageSize() {
      return pageSize;
    }
    
    function getSize() {
      return size;
    }

    function getQuery() {
      return query;
    }

    function setQuery(newQuery) {
      query = newQuery;
    }
    
    function nextPage() {
      page += 1;
      var maxPage = Math.ceil(size / pageSize) - 1;
      if (page > maxPage) {
    	page = maxPage;
    	return false;
      }
      return true;
    }
    
    function previousPage() {
      page -= 1;
      if (page < 0) {
        page = 0;
        return false;
      }
      return true;
    }

    function getSearchParams() {
      return {
        q: query,
        page: page,
        pageSize: pageSize,
        sort: sort
      };
    }
    
    return {
      setMovies: setMovies,
      getMovies: getMovies,
      getPage: getPage,
      getPageSize: getPageSize,
      getSize: getSize,
      getQuery: getQuery,
      setQuery: setQuery,
      getSearchParams: getSearchParams,
      clearMovies: clearMovies,
      nextPage: nextPage,
      previousPage: previousPage
    };
    
  })();
  
  // *** VIEW *** \\
  
  var view = (function () {

    var $movies = $("#listview-movies");
	
  	function moviesSetLoading() {
  	  $movies.html("<li><div class='ui-loader'><span class='ui-icon ui-icon-loading'></span></div></li>");
        $movies.listview("refresh");
  	}
  	
  	function loadMovies() {
  	  var data = model.getMovies();
  	  var html = "",
  	    n;
  	  for (n = 0; n < data.length; n += 1) {
  	    html += "<li class='ui-btn' id='" + data[n]["imdbID"]+ "'>"
  	      + "<img alt='film poster' src='" + data[n]["poster"] + "' onError=\"this.onerror=null;this.src='media/no-image.svg';\">"
  	      + "<h3>" + data[n]["title"] + " (" + getYear(data[n]["released"]) + ")</h3>"
  	      + "<p>" + data[n]["plot"] + "</p>"
  	      + "</li>";
  	  }
  	  $movies.html(html);
  	  $movies.listview("refresh");
  	}
 
    /** Retrieve the year from Unix time */
    function getYear(unixTime) {
      var date = new Date(unixTime * 1000);
      return date.getFullYear();
    }

  	return {
  	  moviesSetLoading: moviesSetLoading,
  	  loadMovies: loadMovies
  	};

  })();
  
  // *** CONTROLLER *** \\

  var controller = (function () {

    var minQueryLength = 1;

    // *** AJAX ***
    // Tell the server to feed us json
    $.ajaxSetup({
      headers: { Accept: "application/json" }
    });

    // Set up a global error handler for ajax.
    $(document).ajaxError(function( event, jqxhr, settings, thrownError ) {
      if (jqxhr.status == 401) {
        // This means our token was invalidated
        // Remove it and display a dialog.
        $.removeCookie("token", { path: "/"});
        $("#token-invalidated-popup").popup("open");
        checkLoggedIn();
      } else {
        $('#server-error-popup').popup("open");
      }
    });
    
    function loadMovies () {
  	  var query = $("#movies-input").val().trim();
      if (query.length < minQueryLength) {
        model.clearMovies();
        view.loadMovies();
        $(".search-info").hide();
      }
      else {
        view.moviesSetLoading();
        model.setQuery(query);
        $.ajax({
          url: "../resources/movie",
          data: model.getSearchParams(),
          success: function (data) {
            model.setMovies(data);
            view.loadMovies();
            $(".search-info").show();
            $(".search-info span").html(model.getSize());
          },
          error: function (data) {
            model.setQuery("");
          }
        });
      }
    }

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

    $('#search-tab .ui-input-clear').click(function () {
      loadMovies();
    });
    
    $("#movies-input").donetyping(function () {
      loadMovies();
    }, 500);

    function checkLoggedIn() {
      if ($.cookie("token")) {
        $("#signout-popup-anchor").show();
        $("#signin-popup-anchor").hide();
      } else {
        $("#signout-popup-anchor").hide();
        $("#signin-popup-anchor").show();
      }
    }
  
    /** Hide error message when sign-in popup is opened */
    $('#signin-popup-anchor').click(function () {
      $('#signin .error').hide();
    });
    
    $("#signout-popup-anchor").click(function () {
      $.removeCookie("token", { path: "/"});
      checkLoggedIn();
    })
    
    $("#search-tab .next").click(function() {
      if (model.nextPage())
        loadMovies();
    });

    $("#search-tab .previous").click(function() {
        if (model.previousPage())
          loadMovies();
      });

    // *** INIT *** \\
    checkLoggedIn();
    loadMovies();

  })();

});
