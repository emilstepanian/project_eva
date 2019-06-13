/**
 * index.page.js - The script used for when the index.html page is initialized.
 */
$(document).ready(function(){


  /**
   *   Hide all DOM-elements inside the body-tag, and then fade them in
   */
  $('body').css('display', 'none');
  $('body').fadeIn(1000);


  /**
   * loginButton - Clickhandler for when the login button is clicked
   */
  $("#loginButton").on("click", function(event){
    event.preventDefault();


    var cbsMail = $("#usernameField").val();
    var password = $("#passwordField").val();

    /**
     * SDK.login - Calls the login function from the SDK and handles the callback
     */
    SDK.login(cbsMail, password, function(error, data) {

      if(error) {
        return $("loginForm").find(".form-control").addClass("has-error");
      }

      $("loginForm").find(".form-control").addClass("has-success");

      var currentUser = SDK.User.current();

      /**
       * SDK.User.getCourses - Calls the getCourses function from the SDK and handles the callback,
       * if the authentication was successful
       */
      SDK.User.getCourses(currentUser.id, function (error, data) {

       if(error) throw error;

        var userCourses = JSON.parse(data);
        var calendarEvents = Array();

        userCourses.forEach(function (course) {

          //Randomize the colors for every course. Uses the randomColor library downloaded from https://randomcolor.llllll.li/
          var courseColor = randomColor({hue: 'blue', luminosity: 'dark', count: 1});

          course.events.forEach(function (event) {

            //Parses the lectures into objects the FullCalendar uses as calendar events.
            var calendarEvent = {
              id : event.id,
              title : event.description,
              start : event.startDate,
              end : event.endDate,
              courseId : course.databaseId,
              courseCode : course.code,
              location : event.location,
              backgroundColor : courseColor,
              type : event.type,
              textColor : "rgb(247, 247, 247)",
            };

            calendarEvents.push(calendarEvent);


          });
        });

        SDK.Storage.persist("calendarEvents", calendarEvents);
      });

      //Fade out all DOM-elements before changing view.
      $('body').fadeOut(500, login);

    });

  });


  /**
   * login - Changes the view to user.html
   */
  function login() {
    window.location.href="user.html";
  }

});
