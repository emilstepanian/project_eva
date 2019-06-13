
/**
 * modal.js - The script used for handling the modal.
 */
var modal = {


  /**
  * init - Initializes the modal and loads the specific
  * lecture information into its DOM-elements
  *
  * @param  {JSON} lecture The specific lecture that is clicked on and received from the calendar event
  */
  init : function(lecture) {
    $('#modalTitle').html(lecture.title);
    $('#courseId').html(lecture.courseId);
    $("#startTime").html(moment(lecture.start).format('MMM Do h:mm A'));
    $('#lectureId').html(lecture.id);
    $('#location').html(lecture.location);
    $('#type').html(lecture.type);
    $('#fullCalModal').modal();
    modal.setStatistics(lecture);
    modal.getLectureReviews(lecture);
  },


  /**
  * setStatistics - Sets the statistics of the lecture inside the modal.
  * Is only used by init() function.
  *
  * @param  {JSON} lecture The specific lecture that is clicked on and received from the calendar event
  */
  setStatistics : function(lecture) {


    SDK.User.getLectureStatistics(lecture.courseId, lecture.id, function(error, data) {
      if(error) throw error;
      var lectureStatistics = JSON.parse(data);

      $("#rating").rating({
        step:1,
        size:'xs',
        showClear: false,
        showCaption : false,
        disabled : true
      });
      $('#rating').rating('update', lectureStatistics.lecture_average);
      $('#courseAttendants').html(lectureStatistics.course_attendants);
      $('#reviewParticipation').html(lectureStatistics.review_participation);


    });
  },


  /**
  * getLectureReviews - Gets the reviews for the specific lecture that is loaded
  * in the modal.
  *
  * @param  {JSON} lecture The specific lecture that is clicked on and received from the calendar event
  */
  getLectureReviews : function(lecture) {
    var currentUser = SDK.User.current();

    /**
    * SDK.User.getLectureReviews - Calls the getLectureReviews function from the SDK and handles the callback,
    * if any reviews were returned
    */
    SDK.User.getLectureReviews(lecture.id, function (error, data) {


      //If error, display that no comments are found
      if (error) {
        $("#commentList").append(
          "<li id='noCommentsFoundInfo'>" +
          "<div class='commentText'>" +
          "<p class='h6'>" + "Ingen kommentar fundet til denne lektion.." + "</p>" +
          "</div>" +
          "</li>"
        );

        //Else, go through each review and add them as comments
      } else {
        var lectureReviews = JSON.parse(data);
        SDK.Storage.persist("lectureReviews", lectureReviews);

        //Counter to generate a unique id for every li and button DOM-elements generated
        var count = 1;
        lectureReviews.forEach(function(review) {

          //Add the comments to the comment field
          $("#commentList").append(
            "<li id='listItem_" + count +"'>" +
            "<div class='commentText'>" +
            "<p class='h6'> - " + review.comment + "</p>" +
            "</div>" +
            "</li>"
          );


          /**
           * Check if any review matches the current user(student) or if the
           * current user is a teacher. If so, disable the ability to send another comment.
           * Furthermore, it adds the ability to either delete the student's comment or all
           * comments, if it's a teacher.
           */
          if(review.userId === currentUser.id || currentUser.type === "teacher"){

            $("#listItem_" + count).prepend(
              "<button id='deleteCommentButton_"+ count + "' type='button' value='" + review.id + "' class='close'> <span aria-hidden='true'>×</span> </button>"
            );

            $("#deleteCommentButton_" + count).on('click', modal.deleteCommentClickHandler);

            $('#commentInput').prop('disabled', true);
            $("#addRating").rating('refresh', {
              disabled : true
            });
            $("#addReviewButton").prop("disabled",true);

            if(review.userId === currentUser.id){
              $('#commentInput').val(review.comment);
              $("#addRating").rating('update', review.rating);
            }
          }

          count++;
        });
      }
    });
  },


  /**
   * deleteCommentClickHandler - The clickhandler that is assigned to the
   * delete comments buttons that is generated.
   */
  deleteCommentClickHandler : function(button){

    if(confirm("Er du sikker på, at du vil slette denne kommentar?")) {
      SDK.User.deleteReview(button.currentTarget.value);

      $(this).closest('li').remove();
    }
  },



  /**
   * addReview - Function to add a comment and send it to the server
   *
   * @param  {JSON} lecture The specific lecture that is clicked on and received from the calendar event
   * @param  {Integer} rating  A Integer for the rating the user has chosen
   */
  addReview : function(lecture, rating){

    var currentUser = SDK.User.current();

    if(!$('#commentInput').is(":disabled")){
      var newReview = {
        userId: currentUser.id,
        lectureId: lecture.id,
        rating: rating,
        comment:$('#commentInput').val()
      };

      SDK.User.addReview(newReview, function(error, data) {
        if(error) {

          toastr.options = {
            "closeButton": true,
            "debug": false,
            "newestOnTop": false,
            "progressBar": true,
            "positionClass": "toast-bottom-center",
            "preventDuplicates": false,
            "onclick": null,
            "showDuration": "300",
            "hideDuration": "1000",
            "timeOut": "5000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
          };

          toastr["warning"]("Kunne ikke oprette reviewet.");

        }
      });

      $("#commentList").append(
        "<li>" +
        "<div class='commentText'>" +
        "<p class='h6'> - " + newReview.comment + "</p>" +
        "</div>" +
        "</li>"
      );

      $("#noCommentsFoundInfo").remove();
      $('#commentInput').prop('disabled', true);
      $("#addRating").rating('refresh', {
        disabled : true
      });
      $("#addReviewButton").prop("disabled",true);


    } else {
toastr.options = {
  "closeButton": true,
  "debug": false,
  "newestOnTop": false,
  "progressBar": true,
  "positionClass": "toast-bottom-center",
  "preventDuplicates": false,
  "onclick": null,
  "showDuration": "300",
  "hideDuration": "1000",
  "timeOut": "5000",
  "extendedTimeOut": "1000",
  "showEasing": "swing",
  "hideEasing": "linear",
  "showMethod": "fadeIn",
  "hideMethod": "fadeOut"
};

toastr["info"]("Du har allerede svaret.", "Fejl opstod.");

    }


  },



  /**
   * clear - Function called when the modal is closed and needs to be cleared,
   * before another lecture is loaded into it.
   */
  clear : function() {
    $("#addReviewButton").prop("disabled",false);
    $('#commentList').children("li").remove();
    $('#commentInput').val("");
    $('#commentInput').prop('disabled', false);
    $("#addRating").rating('refresh', {
      disabled : false
    });
    $("#addRating").rating('update', 0);

  }
};
