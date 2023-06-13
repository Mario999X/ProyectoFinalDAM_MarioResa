extends CanvasLayer

signal try_password_change


var actual_password
var new_password
var repeat_new_password

func _ready():
	pass


func _on_ReturnProfileMenu_pressed():
	Select2.play()
	self.hide()


func _on_ChangePasswordButton_pressed():
	Select1.play()
	
	var actual_password_field = $ChangePasswordMainPanel/ChangePasswordMainContainer/ActualPasswordLineEdit
	var new_password_field = $ChangePasswordMainPanel/ChangePasswordMainContainer/ChangePasswordSecondPanel/ChangePasswordSecondPanel/NewPasswordLineEdit
	var repeat_new_password_field = $ChangePasswordMainPanel/ChangePasswordMainContainer/ChangePasswordSecondPanel/ChangePasswordSecondPanel/RepeatNewPasswordLineEdit
	
	var actual_password_message = $ChangePasswordMainPanel/ChangePasswordMainContainer/ActualPasswordWarningMessage
	var new_password_message = $ChangePasswordMainPanel/ChangePasswordMainContainer/ChangePasswordSecondPanel/ChangePasswordSecondPanel/NewPasswordWarningMessage
	var repeat_new_password_message = $ChangePasswordMainPanel/ChangePasswordMainContainer/ChangePasswordSecondPanel/ChangePasswordSecondPanel/RepeatNewPasswordWarningMessage
	
	
	if (!actual_password_field.text.strip_edges().empty() and !new_password_field.text.strip_edges().empty()
	and actual_password_field.text.strip_edges().length() >= 5 and new_password_field.text.strip_edges().length() >= 5
	and repeat_new_password_field.text.strip_edges() == new_password_field.text.strip_edges()
	and actual_password_field.text.strip_edges() != new_password_field.text.strip_edges()):
		actual_password = actual_password_field.text.strip_edges()
		new_password = new_password_field.text.strip_edges()
		repeat_new_password = repeat_new_password_field.text.strip_edges()
		
		emit_signal("try_password_change")
		self.hide()
	
	
	if actual_password_field.text.strip_edges().empty():
		actual_password_message.text = "CURRENT_PASSWORD_EMPTY"
		actual_password_message.show()
	elif actual_password_field.text.strip_edges().length() < 5:
		actual_password_message.text = "PASSWORD_MUST_BE_AT_LEAST_5_CHARACTERS"
		actual_password_message.show()
	else:
		actual_password_message.hide()
	
	if new_password_field.text.strip_edges().empty():
		new_password_message.text = "NEW_PASSWORD_EMPTY"
		new_password_message.show()
	elif new_password_field.text.strip_edges().length() < 5:
		new_password_message.text = "PASSWORD_MUST_BE_AT_LEAST_5_CHARACTERS"
		new_password_message.show()
	elif actual_password_field.text.strip_edges() == new_password_field.text.strip_edges():
		new_password_message.text = "REPEATED_PASSWORD"
		new_password_message.show()
	else:
		new_password_message.hide()
	
	if repeat_new_password_field.text.strip_edges() != new_password_field.text.strip_edges():
		repeat_new_password_message.text = "PASSWORD_AND_REPEAT_PASSWORD_MUST_BE_THE_SAME"
		repeat_new_password_message.show()
	else:
		repeat_new_password_message.hide()
	
	

