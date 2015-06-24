function getSelectedDate() {
	return $('#calendar_input').val();
}

function repeatPasswordRowHide() {
	if ( $('.repeatPasswordRow').has('.ui-state-error').length == 0 ) {
		$('.repeatPasswordRow').hide('slow');
	}
	
}

function myAlert() {
	alert('Hello World');
}
