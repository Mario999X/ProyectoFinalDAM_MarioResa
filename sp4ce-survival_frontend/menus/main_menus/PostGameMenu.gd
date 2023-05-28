extends CanvasLayer

var _new_score

func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	WinMusic.play()
	
	$MainElementsPanel/MainElementsContainer/ScoreObtainedContainer/ScoreObtainedNumber.text = str(GlobalVariables.actual_score_obtained)
	$MainElementsPanel/MainElementsContainer/ActualScoreRegisteredContainer/ScoreRegisteredNumber.text = str(GlobalVariables.actual_score_registered)
	
	_compare_scores(online_mode)
	


func _on_ReturnToMenuButton_pressed():
	Select2.play()
	
	BackgroundMusic.stream = load("res://assets/sounds/music/loops/Menus_Music.mp3")
	BackgroundMusic.playing = true
	
	get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
	queue_free()


func _on_TryAgainButton_pressed():
	Select1.play()
	
	BackgroundMusic.playing = false
	
	# Queue Free done in the below function
	LoadingProgressBarScreen.load_scene_progress_bar(self, "res://levels/MainLevel.tscn")


func _on_UploadNewScore_request_completed(result, response_code, headers, body):
	# Timeout
	if response_code == 0:
		SaveSystem.save_value_user("Online", "Account", "0")
		
		GlobalVariables.message_http_request = "TIMEOUT"
	# Connected
	else:
		var response = JSON.parse(body.get_string_from_utf8()).result
		
		if (response_code == 200):
			GlobalVariables.message_http_request = "CORRECT"
		
		if (response_code == 400):
			var error = response.value
			GlobalVariables.message_http_request = error
		
		if (response_code == 403):
			SaveSystem.save_value_user("Online", "Account", "0")
			
			var error = response.message
			GlobalVariables.message_http_request = error
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request
	$RequestTimer.start()


func _on_RequestTimer_timeout():
	$LoadScreen.hide()
	
	GlobalVariables.message_http_request = "LOADING"
	$LoadScreen/LoadingElementsContainer/LoadingMessage.text = GlobalVariables.message_http_request


func _upload_new_score(token):
	var url = "https://localhost:6969/sp4ceSurvival/me/score?scoreNumber=" + str(GlobalVariables.actual_score_registered)
	var headers = ["Authorization: Bearer " + token]
	
	$UploadNewScore.request(url, headers, false, HTTPClient.METHOD_PUT)

func _compare_scores(online_mode):
	if GlobalVariables.actual_score_obtained > GlobalVariables.actual_score_registered:
		_new_score = true
		
		$MainElementsPanel/MainElementsContainer/ScoreObtainedContainer/ScoreObtainedMessage.show()
		
		GlobalVariables.actual_score_registered = GlobalVariables.actual_score_obtained
	else:
		_new_score = false
	
	
	# Online ON
	if online_mode != "0":
		match(_new_score):
			true:
				$LoadScreen.show()
				_upload_new_score(online_mode)
			false:
				pass
	
	# Reset apply to the score obtained
	GlobalVariables.actual_score_obtained = 0
