
/**
* user.page.js - The script used for when the user.html page is initialized
*/
$(document).ready(function() {

  /**
   *   Hide all DOM-elements inside the body-tag, and then fade them in
   */
  $('body').css('display', 'none');
  $('body').fadeIn(1000);


  /**
   * Loads the user and personalizes the menu header to the user
   */
  var currentUser = SDK.User.current();
  $("#userLabel").text(currentUser.firstName + " " + currentUser.lastName);
  if(currentUser.type === "student"){
    $("#personalLabel").append("<img id='typeImg' src='img/student.png' style='width:15%'/>");
  } else if(currentUser.type === "teacher"){
    $("#personalLabel").append("<img id='typeImg' src='img/teacher.png' style='width:15%'/>");
  }


  /**
   * Clickhandler for the log out anchor that logs the person out.
   */
  $('#logOutAnchor').on("click", function(){
    $('body').fadeOut(500, logout);
    SDK.logOut();
  });


  /**
   * logout - changes the view to index.html
   */
  function logout() {
    window.location.href ="index.html";
  }

});
