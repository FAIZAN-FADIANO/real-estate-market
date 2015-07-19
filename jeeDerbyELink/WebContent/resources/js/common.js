function getSelectedDate() {
	return $('#calendar_input').val();
}

function repeatPasswordRowHide() {
	if ( $('.repeatPasswordRow').has('.ui-state-error').length == 0 ) {
		$('.repeatPasswordRow').hide('slow');
	}
	
}

function reloadUserPhoto() {
	if ($('.userPhotoChanged').length > 0) {
		var myTimeout = setTimeout( 
			function(){
				location.reload(true);
				clearTimeout(myTimeout);
			}, 2000);
		
		
	}
}

function resizeUserPhoto() {
	var widthUserPhotoWrapper = $('.userPhotoWrapper').css('width');
    $('.userPhotoWrapper').css({
  	  "height": widthUserPhotoWrapper,
  	  "border-radius": "50%"
    });
}

function onlyOneOpenMenuItem() {
	$('.adminMainMenu .ui-panelmenu-header.ui-corner-all').click(function(){
		$( ".adminMainMenu .ui-panelmenu-header.ui-corner-top" ).not(this).trigger( "click" );
	});
	
}











