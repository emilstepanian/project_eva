
/**
 * calendar.js - The script used for when the calendar is initialized as
 * the user.html is loaded
 */
$(document).ready(function() {

  var calendarEvents = SDK.Storage.load("calendarEvents");
  var currentUser = SDK.User.current();
  var currentLecture;
  var selectedRating;

  /**
  * Set up the rating-stars
  */
  $("#addRating").rating({
    step:1,
    size:'xs',
    showClear: false,
    showCaption : false,
    disabled : false
  });


  /**
  * Set up the calendar
  */
  $('#calendar').fullCalendar({
    theme: false,
    timeFormat: 'H(:mm)',
    background: "black",
    header: {
      left: 'prev, next today',
      center: 'title',
      right: 'month,customWeek,basicDay,listMonth'
    },
    views: {
      customWeek: {
        type: 'agendaWeek',
        //duration: { days: 5 },
        buttonText: 'week',
        minTime : "07:00:00",
        maxTime : "24:00:00"
      }
    },

    /**
     * eventClick - Clickhandler for when a event is clicked.
     * Implemented from FullCalendar. Sends the lecture to the initiation of the modal.
     *
     * @param  {type} lecture The lecture object the event that is clicked contains
     * @param  {type} jsEvent The javascript event - not used.
     * @param  {type} view    The view of the calendar - not used.
     */
    eventClick:  function(lecture, jsEvent, view) {
      currentLecture = lecture;
      modal.init(lecture);

      $('#addRating').on('rating.change', function(lecture, value, caption) {
        selectedRating = value;
      });

    },
    navLinks: true, // can click day/week names to navigate views
    editable: false,
    eventLimit: true, // allow "more" link when too many events
    events: calendarEvents,
    aspectRatio : 2,
    weekends : false,
    windowResizeDelay : 100,
    weekNumbers : true,
    fixedWeekCount : true,
    firstDay : 1,
    defaultView : 'customWeek',
  });


  /**
   * addReviewButton - Clickhandler for when the addReviewButton is clicked.
   * Sends the currentLecture object and the selected rating to the modal AddReview function.
   *
   * @param  {type} 'click'   description
   * @param  {type} function( description
   * @return {type}           description
   */
  $('#addReviewButton').on('click', function() {
    modal.addReview(currentLecture, selectedRating);
  });

  /**
   * Hides the modal
   */
  $('#fullCalModal').on('hidden.bs.modal', function () {
    modal.clear();
  });


});
