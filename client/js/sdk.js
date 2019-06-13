
/**
 * sdk.js - The script used for handling the AJAX calls to and from the server.
 * Construction of the sdk is inspired by Jesper Bruun Hansen at this git repository:
 * https://github.com/Distribuerede-Systemer-2016/javascript-client
 */
var SDK = {

  serverURL: "http://localhost:5000/api/",


  /**
   * request function - Used to send the ajax calls to the server
   *
   * @param  {JSON} options The specific options specified by the caller of this method.
   * @param  {function} cb      The callback received from the server, when the server responds
   */
  request: function (options, cb) {

    $.ajax({
      url: SDK.serverURL + options.url,
      method: options.method,
      contentType: "application/json",
      dataType: "json",
      data: XORCipher.encode(JSON.stringify(options.data)),
      success: function (data, status, xhr) {
        cb(null, XORCipher.decode(data), status, xhr);
      },
      error: function (xhr, status, errorThrown) {
        cb({xhr: xhr, status: status, error: errorThrown});
      }
    });
  },

  User: {

    /**
     * getCourses - Called from scripts to get courses from the server
     *
     * @param  {Integer} userId The ID of the user who needs the courses
     * @param  {function} cb     The callback function specified at the script that calls the function
     */
    getCourses: function(userId, cb) {
      SDK.request({
        method: "GET",
        url: "course/user/" + userId
      }, cb);
    },


    /**
     * current - Called to get the current user that is stored in the local storage
     */
    current:function() {
      return SDK.Storage.load("user");
    },


    /**
     * getLectureStatistics - Called from scripts to get a lecture's statistics fom the server
     *
     * @param  {Integer} courseId  The ID of the course the lecture belongs to
     * @param  {Integer} lectureId The ID of the lecture that is clicked
     * @param  {function} cb     The callback function specified at the script that calls the function
     */
    getLectureStatistics : function(courseId, lectureId, cb) {
      SDK.request({
        method: "GET",
        url: "course/entity/" + courseId + "/" + lectureId
      }, cb);
    },


    /**
     * getLectureReviews - Called from scripts to get a lecture's reviews from the server
     *
     * @param  {Integer} lectureId The ID of the lecture that is clicked
     * @param  {function} cb     The callback function specified at the script that calls the function
     */
    getLectureReviews : function(lectureId, cb){
      SDK.request({
        method: "GET",
        url: "review/lecture/" + lectureId,
      }, cb);
    },



    /**
     * addReview - Called from scripts to send a review to the server that needs to be stored
     *
     * @param  {JSON} review The review to be send to the server
     * @param  {function} cb     The callback function specified at the script that calls the function
     */
    addReview : function(review, cb){
      SDK.request({
        data : review,
        url: "review/add",
        method: "POST"
      }, cb);
    },


    /**
     * deleteReview - Called from scripts to send a request to the server to delete a review
     *
     * @param  {Integer} reviewId The ID of the review to be send to the server
     * @param  {function} cb       The callback function specified at the script that calls the function
     */
    deleteReview : function(reviewId, cb) {
      SDK.request({
        data: {
          "id": reviewId
        },
        url: "review/delete",
        method: "PUT"
      }, function(error, data) {
        if (error) return cb(error);
      });
    }

  },


  /**
   * logOut - Called from scripts when the user logs out and the stored data needs to be cleared.
   */
  logOut :function() {
    SDK.Storage.remove("user");
    SDK.Storage.remove("userId");
    SDK.Storage.remove("calendarEvents");
  },



  /**
   * login - Called from scripts when a user attempts to log in.
   *
   * @param  {String} cbsMail  The username that is the CBS mail of the user
   * @param  {String} password The password of the user
   * @param  {function} cb     The callback function specified at the script that calls the function
   */
  login: function (cbsMail, password, cb) {
    this.request({
      data: {
        "cbsMail": cbsMail,
        "password": MD5(password)
      },
      url: "login/",
      method: "POST"

    }, function (err, data) {

      var user = JSON.parse(data);

      if (err) return cb(err);

      SDK.Storage.persist("user", user);

     cb(null, data);
    });
  },


  Storage: {
    prefix: "UserStoreSDK",


    /**
     * persist - Called from scripts when data needs to be persisted in the browser's local storage
     *
     * @param  {String} key The key to specify the data to be stored
     * @param  {JSON} value The value of the data to be stored
     */
    persist: function (key, value) {
      window.localStorage.setItem(this.prefix + key, (typeof value === 'object') ? JSON.stringify(value) : value);
    },


    /**
     * load - Called from scripts when data needs to be loaded from the browser's local storage
     *
     * @param  {String} key The key to of the data to be loaded
     * @return {JSON}       Returns the value of the data to be loaded parsed to JSON.
     */
    load: function (key) {
      var val = window.localStorage.getItem(this.prefix + key);
      try {
        return JSON.parse(val);
      }
      catch (e){
        return val;
      }
    },


    /**
     * remove - Called from scripts when data needs to be removed from the browser's local storage
     *
     * @param  {String} key The key of the data to be removed
     */
    remove:function (key) {
      window.localStorage.removeItem(this.prefix + key);
    }
  }


};
