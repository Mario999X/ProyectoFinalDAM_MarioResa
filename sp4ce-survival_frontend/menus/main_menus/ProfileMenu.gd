extends CanvasLayer

func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	$LoadScreen.show()
	_obtain_profile_query(online_mode)
	

func _on_ObtainProfile_request_completed(result, response_code, headers, body):
	# Timeout
	if response_code == 0:
		GlobalVariables.message_http_request = "TIMEOUT"
	# Connected
	else:
		var response = JSON.parse(body.get_string_from_utf8()).result
		
		if (response_code == 200):
			GlobalVariables.message_http_request = "CORRECT"
			$ProfileMainPanel/ProfileMainContainer/ProfileUsernameContainer/UsernameLabelUser.text = response.username
			$ProfileMainPanel/ProfileMainContainer/ProfileEmailContainer/EmailLabelUser.text = response.email
			$ProfileMainPanel/ProfileMainContainer/ProfileCreationDateContainer/CreationDateLabelUser.text = response.createdAt
			
			$ProfileMainPanel/ProfileMainContainer/ProfileScoreElementsPanel/ProfileScoreElementsContainer/MaxScoreContainer/MaxScoreLabelNumber.text = response.score.scoreNumber
			$ProfileMainPanel/ProfileMainContainer/ProfileScoreElementsPanel/ProfileScoreElementsContainer/MaxScoreDateContainer/MaxScoreDateLabelUser.text = response.score.dateObtained
			
			GlobalVariables.actual_score_registered = int(response.score.scoreNumber)
			
		if (response_code == 403):
			SaveSystem.save_value_user("Online", "Account", "0")
			
			var error = response.message
			GlobalVariables.message_http_request = error
			
			
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request
	$RequestTimer.start()


func _on_RequestTimer_timeout():
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	if online_mode == "0":
		get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
		queue_free()
	
	$LoadScreen.hide()
	GlobalVariables.message_http_request = "LOADING"
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request



func _on_ReturnMainMenu_pressed():
	Select2.play()
	get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
	queue_free()


func _on_ChangePasswordButton_pressed():
	Select1.play()
	$ChangePasswordMenu.show()


func _on_DeleteAccountButton_pressed():
	Select2.play()
	$ConfirmationMenu.show()


func _on_ConfirmationMenu_confirmation():
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	$LoadScreen.show()
	_delete_account_query(online_mode)


func _on_DeleteAccount_request_completed(result, response_code, headers, body):
	# Timeout
	if response_code == 0:
		GlobalVariables.message_http_request = "TIMEOUT"
	# Connected
	else:
		
		if (response_code == 204):
			GlobalVariables.message_http_request = "CORRECT"
			
		if (response_code == 403):
			var response = JSON.parse(body.get_string_from_utf8()).result
			
			var error = response.message
			GlobalVariables.message_http_request = error
			
		
	SaveSystem.save_value_user("Online", "Account", "0")
	
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request
	$RequestTimer.start()


func _obtain_profile_query(token):
	var url = "https://localhost:6969/sp4ceSurvival/me"
	var headers = ["Authorization: Bearer " + token]
	
	$ObtainProfile.request(url, headers, false, HTTPClient.METHOD_GET)

func _delete_account_query(token):
	var url = "https://localhost:6969/sp4ceSurvival/me"
	var headers = ["Authorization: Bearer " + token]
	
	$DeleteAccount.request(url, headers, false, HTTPClient.METHOD_DELETE)


func _on_ChangePasswordMenu_try_password_change():
	var token = SaveSystem.load_value_user("Online", "Account")
	
	var url = "https://localhost:6969/sp4ceSurvival/me/password"
	var query = {"actualPassword": $ChangePasswordMenu.actual_password, "newPassword": $ChangePasswordMenu.new_password, "repeatNewPassword": $ChangePasswordMenu.repeat_new_password}
	var headers = ["Content-Type: application/json", "Authorization: Bearer " + token]
	
	$LoadScreen.show()
	$ChangePassword.request(url, headers, false, HTTPClient.METHOD_PUT, to_json(query))
	


func _on_ChangePassword_request_completed(result, response_code, headers, body):
	# Timeout
	if response_code == 0:
		GlobalVariables.message_http_request = "TIMEOUT"
	# Connected
	else:
		var response = JSON.parse(body.get_string_from_utf8()).result
		
		if response_code == 200:
			GlobalVariables.message_http_request = "CORRECT"
			
		if response_code == 400:
			var error = response.value
			GlobalVariables.message_http_request = error
			
		if response_code == 403:
			
			var error = response.message
			GlobalVariables.message_http_request = error
			
			SaveSystem.save_value_user("Online", "Account", "0")
		
	
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request
	$RequestTimer.start()
