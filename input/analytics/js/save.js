var Save = {

  /**
   * Initialize the save class with the save form's elements selectors
   */
  init : function (saveForm, titleSelector, abstractSelector, authForm) {
    this.saveForm = saveForm;
    this.titleSelector = titleSelector;
    this.abstractSelector = abstractSelector;
    this.authForm = authForm;
  },


  /**
   * Authenticate the user before showing up the save form
   */
  authenticate : function (button, url, credentials) {
    var that = this;
    $.ajax({
      url: url,
      type: 'POST',
      data: credentials,
      beforeSend: that._setCSRF,
      statusCode: {
        200: function(xhr) {
          console.log(that.authForm);
          $(that.authForm).modal('hide');
          $(that.saveForm).modal('show');
          $(button).attr('href', that.saveForm);
        },
        400: function(xhr) {
          new PNotify({
            title: 'Error',
            text: 'Bad credentials',
            type: 'error'
          });
        },
      }
    });
  },

  /**
   * Save an anlysis with the corresponding data in JSON.
   * Uses the save form to retrieve a title and an abstract.
   */
  save : function (button, data) {
    var title = $(this.titleSelector).val().trim();
    if(title) {
      $(button).off()
      $(this.saveSelector).modal('hide');
      postData = this._retrieveFormElements();
      postData.data = JSON.stringify(data);
      $.ajax({
        url : '/analytics/new/data/',
        type: 'POST',
        data: JSON.stringify(postData),
        dataType: 'json',
        beforeSend: this._setCSRF,
        success: function (data) {
          window.location.replace('/analytics/'+data+'/view/');
        },
        statusCode: {
          401: function(xhr) {
            new PNotify({
              title: 'You have to be logged in to save an Analysis.',
              type: 'error'
            });
          },
        }
      });
    } else {
      $(this.titleSelector).closest('.control-group').addClass('error');
    }
  },

  /**
   * Update the analysis identified by its id with the given data.
   */
  update : function (analysisid, data) {
    var putData = {};
    putData.data = JSON.stringify(data);
    $.ajax({
      url : '/analytics/'+analysisid+'/data/',
      type: 'PUT',
      data: JSON.stringify(putData),
      dataType: 'json',
      beforeSend: this._setCSRF,
      statusCode: {
        200: function(xhr) { 
          new PNotify({
            title: 'Analysis saved !',
            type: 'success'
          }); 
        },
        401: function(xhr) {
          new PNotify({
            title: 'You have to be logged in to update an Analysis.',
            type: 'error'
          });
        },
      }
    });
  },

  // Retrieve title and abstract from the save form.
  _retrieveFormElements : function () {
    var formData = {};
    formData.title = $(this.titleSelector).val();
    formData.abstract = $(this.abstractSelector).val();
    return formData;
  },

  // Set the CSRF token in request header.
  _setCSRF : function (xhr, settings) {    
    xhr.setRequestHeader("X-XSRFToken", Save._getCookie('csrftoken')); // Uses save because this references the Ajax element when called.
  },

  // Used to retrieve the CSRF token from cookies.
  _getCookie : function (name) {
    var cookieValue = null;
    if (document.cookie && document.cookie != '') {
      var cookies = document.cookie.split(';');
      for (var i = 0; i < cookies.length; i++) {
        var cookie = jQuery.trim(cookies[i]);
        // Does this cookie string begin with the name we want?
        if (cookie.substring(0, name.length + 1) == (name + '=')) {
          cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
          break;
        }
      }
    }
    return cookieValue;
  },
}
