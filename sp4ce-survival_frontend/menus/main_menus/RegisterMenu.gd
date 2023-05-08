extends CanvasLayer

func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	


func _prepare_register_query(username, email, password, repeatPassword):
	var url = "https://localhost:6969/sp4ceSurvival/register"
	var query = {"username": username, "email": email, "password": password, "repeatPassword": repeatPassword}
	var headers = ["Content-Type: application/json"]
	
	$Register.request(url, headers, false, HTTPClient.METHOD_POST, to_json(query))


func _on_ReturnMainMenu_pressed():
	get_tree().change_scene("res://menus/main_menus/WelcomeMenu.tscn")
	queue_free()


func _on_RegisterButton_pressed():
	var usernameRegisterField = $RegisterElementsPanel/RegisterElementsContainer/UsernameRegisterLineEdit
	var emailRegisterField = $RegisterElementsPanel/RegisterElementsContainer/EmailRegisterLineEdit
	var passwordRegisterField = $RegisterElementsPanel/RegisterElementsContainer/PasswordRegisterLineEdit
	var repeatPasswordRegisterField = $RegisterElementsPanel/RegisterElementsContainer/RepeatPasswordRegisterLineEdit
	
	var usernameRegisterMessage = $RegisterElementsPanel/RegisterElementsContainer/MessageWarningUsername
	var emailRegisterMessage = $RegisterElementsPanel/RegisterElementsContainer/MessageWarningEmail
	var passwordRegisterMessage = $RegisterElementsPanel/RegisterElementsContainer/MessageWarningPassword
	var repeatPasswordRegisterMessage = $RegisterElementsPanel/RegisterElementsContainer/MessageWarningRepeatPassword
	
	var regex = RegEx.new()
	regex.compile("^[\\w\\-\\.]+@[\\w\\-]+\\.[\\w\\-]{2,4}$")
	
	if (!usernameRegisterField.text.strip_edges().empty() and !emailRegisterField.text.strip_edges().empty()
	and regex.search(emailRegisterField.text.strip_edges()) != null and passwordRegisterField.text.strip_edges().length() >= 5 
	and repeatPasswordRegisterField.text.strip_edges() == passwordRegisterField.text.strip_edges()): 
		
		$LoadScreen.show()
		_prepare_register_query(usernameRegisterField.text.strip_edges(), emailRegisterField.text.strip_edges(), 
		passwordRegisterField.text.strip_edges(), repeatPasswordRegisterField.text.strip_edges())
	
	if usernameRegisterField.text.strip_edges().empty():
		usernameRegisterMessage.text = "USERNAME_CANNOT_BE_EMPTY"
		usernameRegisterMessage.show()
	else:
		usernameRegisterMessage.hide()
	
	if emailRegisterField.text.strip_edges().empty():
		emailRegisterMessage.text = "EMAIL_CANNOT_BE_EMPTY"
		emailRegisterMessage.show()
	
	elif regex.search(emailRegisterField.text.strip_edges()) == null:
		emailRegisterMessage.text = "EMAIL_INCORRECT"
		emailRegisterMessage.show()
	else:
		emailRegisterMessage.hide()
	
	if passwordRegisterField.text.strip_edges().length() <= 4:
		passwordRegisterMessage.text = "PASSWORD_MUST_BE_AT_LEAST_5_CHARACTERS"
		passwordRegisterMessage.show()
	else:
		passwordRegisterMessage.hide()

	if repeatPasswordRegisterField.text.strip_edges() != passwordRegisterField.text.strip_edges():
		repeatPasswordRegisterMessage.text = "PASSWORD_AND_REPEAT_PASSWORD_MUST_BE_THE_SAME"
		repeatPasswordRegisterMessage.show()
	else:
		repeatPasswordRegisterMessage.hide()
	
	


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


func _on_RequestTimer_timeout():
	var onlineMode = SaveSystem.load_value_user("Online", "Account")
	
	if onlineMode != "0":
		get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
		queue_free()
	
	$LoadScreen.hide()
	GlobalVariables.message_http_request = "LOADING"
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request
