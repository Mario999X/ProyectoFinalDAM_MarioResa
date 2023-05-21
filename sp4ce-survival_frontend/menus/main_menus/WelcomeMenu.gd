extends CanvasLayer

# First, we load the Settings of the player with the LoadSettings singleton.
# Then we check the online status, we verify if there is a token, and if it does, we verify that it is not expired
# Finally, if the token is not expired, the player is moved to the Main Scene
func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	
	LoadSettings.load_Settings()
	
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	if online_mode != "0":
		$LoadScreen.show()
		_check_token_query(online_mode)


func _on_ExitButton_pressed():
	get_tree().quit()


func _on_OptionsButton_pressed():
	Select1.play()
	$SettingsMenu.show()

# Function to play offline, the online mode is set to 0
func _on_PlayOfflineButton_pressed():
	Select1.play()
	SaveSystem.save_value_user("Online", "Account", "0")
	get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
	queue_free()

# HTTP Request for the token check
# "Use SSL" must be false because I use Self-Signed Certificate
func _check_token_query(token):
	var url = "https://localhost:6969/sp4ceSurvival/me"
	var headers = ["Authorization: Bearer " + token]
	
	$CheckToken.request(url, headers, false, HTTPClient.METHOD_GET)

# HTTP Request for the Login Check
func _prepare_login_query(username, password):
	var url = "https://localhost:6969/sp4ceSurvival/login"
	var query = {"username": username, "password": password}
	var headers = ["Content-Type: application/json"]
	
	$Login.request(url, headers, false, HTTPClient.METHOD_GET, to_json(query))

# Login Button, we check if the data is logically correct according to the project information.
func _on_LoginButton_pressed():
	Select1.play()
	var username_field = $LoginElementsPanel/LoginElementsContainer/UsernameLineEdit
	var password_field = $LoginElementsPanel/LoginElementsContainer/PasswordLineEdit
	
	var username_message = $LoginElementsPanel/LoginElementsContainer/MessageWarningUsername
	var password_message = $LoginElementsPanel/LoginElementsContainer/MessageWarningPassword
	
	if !username_field.text.strip_edges().empty() and password_field.text.strip_edges().length() >= 5 :
		$LoadScreen.show()
		_prepare_login_query(username_field.text.strip_edges(), password_field.text.strip_edges())
		
	if username_field.text.strip_edges().empty():
		username_message.text = "USERNAME_CANNOT_BE_EMPTY"
		username_message.show()
	else:
		username_message.hide()
	
	if password_field.text.strip_edges().length() <= 4:
		password_message.text = "PASSWORD_MUST_BE_AT_LEAST_5_CHARACTERS"
		password_message.show()
	else:
		password_message.hide()
	

# Login Request completed, we act according to the response code
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

# Check Token Request completed, we act according to the response code
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

# To show the player the message received from the Request, and also to change the scene if the token is saved.
func _on_RequestTimer_timeout():
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	if online_mode != "0":
		get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
		queue_free()
	
	$LoadScreen.hide()
	GlobalVariables.message_http_request = "LOADING"
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request

func _on_RegisterButton_pressed():
	Select1.play()
	get_tree().change_scene("res://menus/main_menus/RegisterMenu.tscn")
	queue_free()
