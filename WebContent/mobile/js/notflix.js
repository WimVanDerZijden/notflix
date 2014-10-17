//$(document).on("pageinit", "#home-page", function () {
$(document).ready(function () {
  "use strict";
  // MVC Revealing Module Pattern \\
  
  // *** MODEL *** \\
  
  var model = (function() {

    // #home-page vars
    var movies = [];
    var size = 0;
    var page = 0;
    var pageSize = 10;
    var query = "";
    var sort = "TitleAsc";
    
    // #movie-page vars
    var movie;
    
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
      page = 0;
    }
    
    function setSort(newSort) {
      sort = newSort;
    }
    
    function setPageSize(newPageSize) {
      pageSize = newPageSize;
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
    
    function getPagingInfo() {
      var start = page * pageSize + 1;
      var end = (page + 1) * pageSize;
      if (start > size) {
        return "";
      }
      if (end > size) {
        end = size;
      }
      return "(showing " + start + " - " + end + ")";
    }
    
    function getMovie() {
      return movie;
    }
    
    function setMovie(data) {
      movie = data;
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
      previousPage: previousPage,
      setSort: setSort,
      setPageSize: setPageSize,
      getPagingInfo: getPagingInfo,
      getMovie: getMovie,
      setMovie: setMovie
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
          + "<a href='#movie-page?imdbID=" + data[n]["imdbID"] + "' data-transition='slide'>"
  	      + "<img alt='film poster' src='" + data[n]["poster"] + "' onError=\"this.onerror=null;this.src='media/no-image.svg';\">"
  	      + "<h3>" + data[n]["title"] + " (" + getYear(data[n]["released"]) + ")</h3>"
          + "<p class='imdb'>Imdb: " + data[n]["imdbRating"].toLocaleString() + " by " + data[n]["imdbVotes"].toLocaleString() + " users</p>"
  	      + "<p>" + data[n]["plot"] + "</p>"
  	      + "</a></li>";
  	  }
  	  $movies.html(html);
  	  $movies.listview("refresh");
  	  
      $(".search-info").show();
      $(".search-info .count-info").html(model.getSize());
      $(".search-info .paging-info").html(model.getPagingInfo());
  	}
  	
  	function loadMovie() {
  	  var movie = model.getMovie();
      $("#movie-poster").html('<img src="' + movie["poster"] + '">');
      
  	  var html = '<li class="header">' + movie["title"] + '</li>'
      + '<li class="caption">' + movie["runtime"] +' min  -  ' + movie["genre"] +'  -  ' + getDate(movie["released"]) + ' (' + movie["country"] +')</li>'
      + '<li>' + movie["plot"] + '</li>'
      + '<li><b>Directed by:</b> ' + movie["director"] + '</li>'
      + '<li><b>Written by:</b> ' + movie["writer"] + '</li>'
      + '<li><b>Actors:</b> ' + movie["actors"] +'</li>'
      + '<li class="imdb"><a href="http://www.imdb.com/title/' + movie["imdbID"] +'/"><img class="imdb-icon" src="media/imdb.png"><p>' + movie['imdbRating'].toLocaleString() + '/10 from ' + movie["imdbVotes"].toLocaleString() + ' users</p></a></li>'
  	  var $movie = $("#movie-detail");
  	  $movie.html(html);
  	  $movie.listview("refresh");
  	}

    function checkLoggedIn() {
      if ($.cookie("token")) {
        $(".signout-popup-anchor").show();
        $(".signin-popup-anchor").hide();
      } else {
        $(".signout-popup-anchor").hide();
        $(".signin-popup-anchor").show();
      }
    }
  	
    /** Retrieve the year from Unix time */
    function getYear(unixTime) {
      var date = new Date(unixTime * 1000);
      return date.getFullYear();
    }
    
    /** Retrieve full date from Unix time */
    function getDate(unixTime) {
      var date = new Date(unixTime * 1000);
      return date.toLocaleDateString();
    }

  	return {
  	  moviesSetLoading: moviesSetLoading,
  	  loadMovies: loadMovies,
  	  loadMovie: loadMovie,
  	  checkLoggedIn: checkLoggedIn
  	};

  })();
  
  // *** CONTROLLER *** \\

  var controller = (function () {

    var minQueryLength = 0;

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
        view.checkLoggedIn();
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
        if (model.getQuery() !== query) {
          model.setQuery(query);
        }
        model.setSort($("#sort-list").val());
        model.setPageSize($("#limit-list").val());
        $.ajax({
          url: "../resources/movie",
          data: model.getSearchParams(),
          success: function (data) {
            model.setMovies(data);
            view.loadMovies();
          },
          error: function (data) {
            model.setQuery("");
          }
        });
      }
    }
    
    function loadMovie(imdbID) {
      localStorage.setItem("imdbID", imdbID);
      $.ajax({
        url: "../resources/movie/" + imdbID,
        success: function (data) {
          model.setMovie(data);
          view.loadMovie();
        }
      });
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
          view.checkLoggedIn();
        },
        error: function(data) {
          $('#signin .error').show();
          $.mobile.loading("hide");   
        }
      });
    });

    // *** Listeners *** \\\
    
    /** Hide error message when sign-in popup is opened */
    $('.signin-popup-anchor').click(function () {
      $('#signin .error').hide();
    });
    
    $(".signout-popup-anchor").click(function () {
      $.removeCookie("token", { path: "/"});
      view.checkLoggedIn();
    });
    
    $("#search-tab .next").click(function() {
      if (model.nextPage())
        loadMovies();
    });

    $("#search-tab .previous").click(function() {
      if (model.previousPage())
        loadMovies();
    });

    $('#search-tab .ui-input-clear').click(function () {
      loadMovies();
    });
    
    $("#movies-input").donetyping(function () {
      loadMovies();
    }, 500);
    
    $("#sort-list").change(function() {
      loadMovies();
    });
    
    $("#limit-list").change(function() {
      loadMovies();
    });

    
    // *** INIT *** \\
    
    function loadPage(pageId, data) {
      view.checkLoggedIn();
      switch(pageId) {
      case "home-page":
        loadMovies();
        break;
      case "movie-page":
        try {
          loadMovie(data.options.pageData.imdbID);
        } catch (err) {
          // Query param imdbID not found. Try localStorage.
          loadMovie(localStorage.getItem("imdbID"));
        }
        break;
      }
    }

    /** Run the loadPage logic for the initial page */
    loadPage($("body").pagecontainer("getActivePage").prop("id"), null);
    
    /** Run the loadPage logic if a new page is loaded */
    $("body").on("pagecontainerbeforetransition", function(event, data) {
      loadPage(data.toPage[0].id, data);
    });
    
  })();

});
