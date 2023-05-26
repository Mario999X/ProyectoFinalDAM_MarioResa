extends CanvasLayer

func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	

# Function to return to the Welcome Menu
func _on_ReturnWelcomeMenu_pressed():
	Select2.play()
	get_tree().change_scene("res://menus/main_menus/WelcomeMenu.tscn")
	queue_free()

# Register Buttoon, we check if the data is logically correct according to the project information.
func _on_RegisterButton_pressed():
	Select1.play()
	var username_register_field = $RegisterElementsPanel/RegisterElementsContainer/UsernameRegisterLineEdit
	var email_register_field = $RegisterElementsPanel/RegisterElementsContainer/EmailRegisterLineEdit
	var password_register_field = $RegisterElementsPanel/RegisterElementsContainer/PasswordRegisterLineEdit
	var repeat_password_register_field = $RegisterElementsPanel/RegisterElementsContainer/RepeatPasswordRegisterLineEdit
	
	var username_register_message = $RegisterElementsPanel/RegisterElementsContainer/MessageWarningUsername
	var email_register_message = $RegisterElementsPanel/RegisterElementsContainer/MessageWarningEmail
	var password_register_message = $RegisterElementsPanel/RegisterElementsContainer/MessageWarningPassword
	var repeat_password_register_message = $RegisterElementsPanel/RegisterElementsContainer/MessageWarningRepeatPassword
	
	var regex = RegEx.new()
	regex.compile("^[\\w\\-\\.]+@[\\w\\-]+\\.[\\w\\-]{2,4}$")
	
	if (!username_register_field.text.strip_edges().empty() and !email_register_field.text.strip_edges().empty()
	and regex.search(email_register_field.text.strip_edges()) != null and password_register_field.text.strip_edges().length() >= 5 
	and repeat_password_register_field.text.strip_edges() == password_register_field.text.strip_edges()): 
		
		$LoadScreen.show()
		_prepare_register_query(username_register_field.text.strip_edges(), email_register_field.text.strip_edges(), 
		password_register_field.text.strip_edges(), repeat_password_register_field.text.strip_edges())
	
	if username_register_field.text.strip_edges().empty():
		username_register_message.text = "USERNAME_CANNOT_BE_EMPTY"
		username_register_message.show()
	else:
		username_register_message.hide()
	
	if email_register_field.text.strip_edges().empty():
		email_register_message.text = "EMAIL_CANNOT_BE_EMPTY"
		email_register_message.show()
	
	elif regex.search(email_register_field.text.strip_edges()) == null:
		email_register_message.text = "EMAIL_INCORRECT"
		email_register_message.show()
	else:
		email_register_message.hide()
	
	if password_register_field.text.strip_edges().length() <= 4:
		password_register_message.text = "PASSWORD_MUST_BE_AT_LEAST_5_CHARACTERS"
		password_register_message.show()
	else:
		password_register_message.hide()

	if repeat_password_register_field.text.strip_edges() != password_register_field.text.strip_edges():
		repeat_password_register_message.text = "PASSWORD_AND_REPEAT_PASSWORD_MUST_BE_THE_SAME"
		repeat_password_register_message.show()
	else:
		repeat_password_register_message.hide()
	
	

# Register Request Completed, we act according to the response code
func _on_Register_request_completed(result, response_code, headers, body):
	if response_code == 0:
		GlobalVariables.message_http_request = "TIMEOUT"
	else:
		var response = JSON.parse(body.get_string_from_utf8()).result
		
		if response_code == 201:
			GlobalVariables.message_http_request = "CORRECT"
			var token = response.value
			SaveSystem.save_value_user("Online", "Account", token)
		
		if response_code == 400 || response_code == 409:
			GlobalVariables.message_http_request = response.value
			
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request
	$RequestTimer.start()

# To show the player the message received from the Request, and also to change the scene if the token is saved.
func _on_RequestTimer_timeout():
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	if online_mode != "0":
		get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
		queue_free()
	
	$LoadScreen.hide()
	GlobalVariables.message_http_request = "LOADING"
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request


# HTTP Request for the register
func _prepare_register_query(username, email, password, repeat_password):
	var url = "https://localhost:6969/sp4ceSurvival/register"
	var query = {"username": username, "email": email, "password": password, "repeatPassword": repeat_password}
	var headers = ["Content-Type: application/json"]
	
	$Register.request(url, headers, false, HTTPClient.METHOD_POST, to_json(query))
