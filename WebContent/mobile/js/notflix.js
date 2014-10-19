jQuery(document).ready(function ($) {
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
    
    function isLoggedIn() {
      return $.cookie("token") !== undefined;
    }
    
    return {
      setMovies: setMovies,
      getMovies: getMovies,
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
      setMovie: setMovie,
      isLoggedIn: isLoggedIn
    };
    
  })();
  
  // *** VIEW *** \\
  
  var view = (function () {

    var $movies = $("#listview-movies");
	
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
      + '<li id="star-li">'
      + '<div id="star-widget"></div>'
      + '<div id="rating-delete" class="ui-btn ui-icon-delete ui-btn-icon-notext ui-btn-inline ui-shadow ui-corner-all" data-inline="true" data-iconpos="notext" data-icon="delete" data-role="button" href="index.html" role="button">Delete</div>'
      + '</li>'
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
      if (model.isLoggedIn()) {
        $(".signout-popup-anchor, #star-li").show();
        $(".signin-popup-anchor").hide();
      } else {
        $(".signout-popup-anchor, #star-li").hide();
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
  	  loadMovies: loadMovies,
  	  loadMovie: loadMovie,
  	  checkLoggedIn: checkLoggedIn
  	};

  })();
  
  // *** CONTROLLER *** \\

  var controller = (function () {

    var minQueryLength = 0;

    // *** AJAX *** \\
    
    /** Tell the server to feed us json */
    $.ajaxSetup({
      headers: { Accept: "application/json" }
    });

    // Set up a global error handler for ajax.
    $(document).ajaxError(function( event, jqxhr, settings, thrownError ) {
      $.mobile.loading("hide");
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
    
    // *** General controller functions *** \\
    
    function loadMovies () {
  	  var query = $("#movies-input").val().trim();
      if (query.length < minQueryLength) {
        model.clearMovies();
        view.loadMovies();
        $(".search-info").hide();
      } else {
        $.mobile.loading("show");
        if (model.getQuery() !== query) {
          model.setQuery(query);
        }
        model.setSort($("#sort-list").val());
        model.setPageSize($("#limit-list").val());
        $.ajax({
          url: "../resources/movie",
          data: model.getSearchParams(),
          success: function (data) {
            $.mobile.loading("hide");
            if (data["movies"] === undefined) {
              // Sometimes jackson will not work and return an empty object.
              // Restarting the server 1 or more times usually solves this issue.
              console.log("Jackson was not properly initialized. Server restart required.");
              $('#server-error-popup').popup("open");
            } else {
              model.setMovies(data);
              view.loadMovies();
            }
          },
          error: function (data) {
            model.setQuery("");
          }
        });
      }
    }
    
    function initStarbox(imdbID, stars) {
      new Starbox("star-widget", stars, {
        rerate: true,
        buttons: 10,
        onRate: function (element, memo) {
          submitRating(imdbID, memo.rated * 2, stars);
        }
      });
      if (!model.isLoggedIn()) {
        $("#star-li").hide();
      }
      if (model.getMovie()["rating"] > 0) {
        $("#rating-delete").show();
      } else {
        $("#rating-delete").hide();
      }
    }
    
    function loadMovie(imdbID) {
      $.mobile.loading("show");
      localStorage.setItem("imdbID", imdbID);
      $.ajax({
        url: "../resources/movie/" + imdbID,
        success: function (data) {
          $.mobile.loading("hide");
          model.setMovie(data);
          view.loadMovie();
          // init the starbox widget
          initStarbox(data["imdbID"], data["rating"] / 2);
          $("#rating-delete").click(function () {
            deleteRating();
          });
        }
      });
    }

    var submittingRating = false;
    
    /** Submit a rating */
    function submitRating(imdbID, halfStars, oldHalfStars) {
      if (submittingRating) {
        return;
      }
      submittingRating = true;
      $.mobile.loading("show");
      var method = oldHalfStars > 0 ? "put" : "post";
      $.ajax({
        type: method,
        url: "../resources/rating/" + imdbID,
        data: { halfStars: halfStars },
        success: function (data) {
          $.mobile.loading("hide");
          model.getMovie()["rating"] = data["halfStars"];
          initStarbox(imdbID, model.getMovie()["rating"] / 2);
          submittingRating = false;
        },
        error: function () {
          initStarbox(imdbID, model.getMovie()["rating"] / 2);
          submittingRating = false;
        }
      });
    }
    
    /** Delete a rating */
    function deleteRating() {
      $.mobile.loading("show");
      $.ajax({
        type: "delete",
        url: "../resources/rating/" + model.getMovie()["imdbID"],
        success: function (data) {
          $.mobile.loading("hide");
          model.getMovie()["rating"] = 0;
          initStarbox(model.getMovie()["imdbID"], 0);
        }
      });
    }
    
    // *** Form submit listeners *** \\ 
    
    /** Perform the login */
    $('#form-signin').submit(function (e) {
      $.mobile.loading("show")
      e.preventDefault();
      $.ajax({
        type: "post",
        url: "../resources/session/login",
        data: $(this).serialize(),
        success: function(data) {
          $.cookie("token", data["token"], { path: "/"} );
          $('#signin').popup("close");        
          $.mobile.loading("hide");
          view.checkLoggedIn();
          // Reload page because there may be user-specific data
          loadPage($("body").pagecontainer("getActivePage").prop("id"), null);
        },
        error: function(data) {
          $('#signin .signin-section .error').show();
          $.mobile.loading("hide");   
        }
      });
    });
    
    /** Register new user */
    $("#form-register").submit(function (e) {
      e.preventDefault();
      $.mobile.loading("show");
      $.ajax({
        type: "post",
        url: "../resources/user",
        data: $(this).serialize(),
        success: function(data) {
          $("#form-signin input[name=username]").val(data["username"]);
          $("#form-signin input[name=password]").val("");
          $("#signin .register-section").hide();
          $("#signin .signin-section").show();
          $.mobile.loading("hide");
        },
        error: function(data) {
          $('#signin .register-section .error').show();
          $.mobile.loading("hide");   
        }
      });
    });
    
    // *** Search movies listeners *** \\\
    
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
    
    $("#sort-list, #limit-list").change(function() {
      loadMovies();
    });
    
    // *** Sign in / Register listeners *** \\
    
    /** Hide error message when sign-in popup is opened */
    $('.signin-popup-anchor').click(function () {
      $('#signin .error').hide();
    });
    
    $(".signout-popup-anchor").click(function () {
      $.removeCookie("token", { path: "/"});
      view.checkLoggedIn();
    });

    $("#signin .toggle").click(function () {
      $("#signin .register-section").toggle();
      $("#signin .signin-section").toggle();
    });
    
    $("#form-register .password-confirm").on("input", function () {
      if ($("#form-register .password").val() !== $(this).val()) {
        this.setCustomValidity('Passwords must match.');
      } else {
        this.setCustomValidity('');
      }
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
    
    $("#signin, #signout, #token-invalidated-popup, #server-error-popup").enhanceWithin().popup();
    
    $("#signin .register-section").hide();
    
  })();

});
