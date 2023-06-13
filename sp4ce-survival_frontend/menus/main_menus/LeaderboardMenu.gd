extends CanvasLayer

var base_url = GlobalVariables.api_url

var row = preload("res://menus/main_menus/leaderboard_elements/Row.tscn")
onready var table = get_node("LeaderboardMainContainer/LeaderboardMainPanel/LeaderboardScrollContainer/LeaderboardElementsContainer")

var sn = 0

var page = 0
var content = true

func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	$LoadScreen.show()
	_obtain_leaderboard_query(online_mode)
	


func _on_ObtainLeaderboardUsers_request_completed(result, response_code, headers, body):
		# Timeout
	if response_code == 0:
		GlobalVariables.message_http_request = "TIMEOUT"
	# Connected
	else:
		var response = JSON.parse(body.get_string_from_utf8()).result
		
		if response_code == 200:
			GlobalVariables.message_http_request = "CORRECT"
			
			_set_data(response)
			
		if response_code == 404:
			var error = response.value
			GlobalVariables.message_http_request = error
			
			page -= 1
			$LeaderboardMainContainer/LeaderboardButtonsPanel/LeaderboardButtonsContainer/AdvancePageButton.disabled = true
		if response_code == 403:
			
			var error = response.message
			GlobalVariables.message_http_request = error
			
			SaveSystem.save_value_user("Online", "Account", "0")
		
	
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


func _obtain_leaderboard_query(token):
	if page == 0:
		$LeaderboardMainContainer/LeaderboardButtonsPanel/LeaderboardButtonsContainer/ReturnPageButton.disabled = true
	
	var url = base_url + "/leaderboard?page=" + str(page)
	var headers = ["Authorization: Bearer " + token]
	
	$ObtainLeaderboardUsers.request(url, headers, false, HTTPClient.METHOD_GET)
	

func _set_data(users):
	var rows = get_tree().get_nodes_in_group("rows")
	for oldRow in rows:
		oldRow.queue_free()
	
	for user in users:
		sn = sn + 1
		var instance = row.instance()
		instance.name = str(sn)
		table.add_child(instance)
	
		get_node("LeaderboardMainContainer/LeaderboardMainPanel/LeaderboardScrollContainer/LeaderboardElementsContainer/" + instance.name + "/PositionField").text = user.position
		get_node("LeaderboardMainContainer/LeaderboardMainPanel/LeaderboardScrollContainer/LeaderboardElementsContainer/" + instance.name + "/UsernameField").text = user.username
		get_node("LeaderboardMainContainer/LeaderboardMainPanel/LeaderboardScrollContainer/LeaderboardElementsContainer/" + instance.name + "/ScoreField").text = user.score.scoreNumber
		get_node("LeaderboardMainContainer/LeaderboardMainPanel/LeaderboardScrollContainer/LeaderboardElementsContainer/" + instance.name + "/DateField").text = user.score.dateObtained


func _on_ReturnMainMenuButton_pressed():
	Select2.play()
	get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
	queue_free()


func _on_ReturnPageButton_pressed():
	Select2.play()
	page -= 1
	
	$LeaderboardMainContainer/LeaderboardButtonsPanel/LeaderboardButtonsContainer/AdvancePageButton.disabled = false
	
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	$LoadScreen.show()
	_obtain_leaderboard_query(online_mode)


func _on_AdvancePageButton_pressed():
	Select1.play()
	page += 1
	
	$LeaderboardMainContainer/LeaderboardButtonsPanel/LeaderboardButtonsContainer/ReturnPageButton.disabled = false
	
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	$LoadScreen.show()
	_obtain_leaderboard_query(online_mode)
