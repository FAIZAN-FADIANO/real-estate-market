function getSelectedDate() {
	return $('#calendar_input').val();
}

function repeatPasswordRowHide() {
	if ( $('.repeatPasswordRow').has('.ui-state-error').length == 0 ) {
		$('.repeatPasswordRow').hide('slow');
	}
	
}

function resizeUserPhoto() {
	var widthUserPhotoWrapper = $('.userPhotoWrapper').css('width');
    $('.userPhotoWrapper').css({
  	  "height": widthUserPhotoWrapper,
  	  "border-radius": "50%"
    });
}
