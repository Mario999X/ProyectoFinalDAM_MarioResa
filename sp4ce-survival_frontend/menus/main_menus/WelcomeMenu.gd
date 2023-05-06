extends CanvasLayer


func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	LoadSettings.load_Settings()
	
	var onlineMode = SaveSystem.load_value_user("Online", "Account")
	
	if onlineMode != "0":
		$LoadScreen.show()
		_check_token_query(onlineMode)


func _on_ExitButton_pressed():
	get_tree().quit()


func _on_OptionsButton_pressed():
	$SettingsMenu.show()


func _on_PlayOfflineButton_pressed():
	SaveSystem.save_value_user("Online", "Account", "0")
	get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
	queue_free()


# "Use SSL" must be false because I use Self-Signed Certificate
func _check_token_query(token):
	var url = "https://localhost:6969/sp4ceSurvival/me"
	var headers = ["Authorization: Bearer " + token]
	
	$CheckToken.request(url, headers, false, HTTPClient.METHOD_GET)


func _prepare_login_query(username, password):
	var url = "https://localhost:6969/sp4ceSurvival/login"
	var query = {"username": username, "password": password}
	var headers = ["Content-Type: application/json"]
	
	$Login.request(url, headers, false, HTTPClient.METHOD_GET, to_json(query))


func _on_LoginButton_pressed():
	var usernameField = $LoginElementsPanel/LoginElementsContainer/UsernameLineEdit
	var passwordField = $LoginElementsPanel/LoginElementsContainer/PasswordLineEdit
	
	var usernameMessage = $LoginElementsPanel/LoginElementsContainer/MessageWarningUsername
	var passwordMessage = $LoginElementsPanel/LoginElementsContainer/MessageWarningPassword
	
	if !usernameField.text.strip_edges().empty() && passwordField.text.strip_edges().length() >= 5 :
		$LoadScreen.show()
		_prepare_login_query(usernameField.text.strip_edges(), passwordField.text.strip_edges())
		
	if usernameField.text.strip_edges().empty():
		usernameMessage.text = "USERNAME_CANNOT_BE_EMPTY"
		usernameMessage.show()
	else:
		usernameMessage.hide()
	
	if passwordField.text.strip_edges().length() <= 4:
		passwordMessage.text = "PASSWORD_MUST_BE_AT_LEAST_5_CHARACTERS"
		passwordMessage.show()
	else:
		passwordMessage.hide()
	


func _on_Login_request_completed(result, response_code, headers, body):
	# Timeout
	if response_code == 0:
		GlobalVariables.message_http_request = "TIMEOUT"
	# Connected
	else:
		var response = JSON.parse(body.get_string_from_utf8()).result
		
		if (response_code == 200):
			GlobalVariables.message_http_request = "CORRECT"
			var token = response.value
			SaveSystem.save_value_user("Online", "Account", token)
			
		if (response_code == 400):
			var error = response.value
			GlobalVariables.message_http_request = error
			
		if (response_code == 403):
			var error = response.message
			GlobalVariables.message_http_request = error
			
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


func _on_CheckToken_request_completed(result, response_code, headers, body):
	# Timeout
	if response_code == 0:
		SaveSystem.save_value_user("Online", "Account", "0")
		
		GlobalVariables.message_http_request = "TIMEOUT"
	# Connected
	else:
		var response = JSON.parse(body.get_string_from_utf8()).result
		
		if (response_code == 200):
			GlobalVariables.message_http_request = "CORRECT"

		if (response_code == 403):
			SaveSystem.save_value_user("Online", "Account", "0")
			
			var error = response.message
			GlobalVariables.message_http_request = error
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request
	$RequestTimer.start()


func _on_RegisterButton_pressed():
	get_tree().change_scene("res://menus/main_menus/RegisterMenu.tscn")
	queue_free()
