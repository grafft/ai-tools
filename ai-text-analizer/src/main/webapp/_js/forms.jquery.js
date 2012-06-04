/*--------------- Labeled input text ---------------*/

(function($) {
   $(document).ready(function() {
      markLabels();
   });
})(jQuery);


function markLabels() {
   (function($) {
      $('input[title], textarea[title]').each(function() {
         if($(this).val() === '') {
            $(this).val($(this).attr('title')).addClass('labeled_empty');
         }

         $(this).focus(function() {
            if($(this).hasClass('labeled_empty')) {
               $(this).val('').removeClass('labeled_empty');
            }
         });

         $(this).blur(function() {
            if($(this).val() === '') {
               $(this).val($(this).attr('title')).addClass('labeled_empty');
            }
         });
      });
   })(jQuery);
}

function removeLabelsOnExit() {
   (function($) {

      $('input[title], textarea[title]').each(function() {
         if($(this).hasClass('labeled_empty')) {
            $(this).val('').removeClass('labeled_empty');
         }
      });
   })(jQuery);

   return true;
}


/*--------------- Labeled checkboxes and radios ---------------*/

(function($) {
   $(document).ready(function() {
      function labelClickEvent() {
         $(this).unbind('click',labelClickEvent);

         var input = $(this).prevAll("input");

         if(!input.is(':disabled')) {
            if(input.is(':checked') && input.is(":checkbox")) {
               input.removeAttr('checked').change();
            } else {
               input.attr('checked','checked').change();
            }
         }

         $(this).click(labelClickEvent);
      }

      $('.toggle_label').click(labelClickEvent);
   });
})(jQuery);

// Logging

jQuery.fn.log = function (msg) {
   console.log("%s: %o",msg,this);
   return this;
};

// Validation

function isEmail(emailAddress) {
   var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
   return pattern.test(emailAddress);
}
