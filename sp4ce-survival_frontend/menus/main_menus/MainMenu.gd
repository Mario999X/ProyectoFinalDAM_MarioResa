extends CanvasLayer

# We reload the difficulty label to show the current data to the player
# Also, we check the online status, if it is "0", the Profile and Leaderboard Buttons are disabled
func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	
	_reloadLabel(GlobalVariables.difficulty_selected)
	
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	if online_mode == "0":
		$OnlineModeLabel.text = "STATUS: OFFLINE"
		$MainElementsPanel/MainElementsContainer/OnlineElementsPanel/OnlineElementsContainer/ProfileButton.disabled = true
		$MainElementsPanel/MainElementsContainer/OnlineElementsPanel/OnlineElementsContainer/LeaderboardButton.disabled = true
	else:
		$OnlineModeLabel.text = "STATUS: ONLINE"


func _reloadLabel(difficulty_selected):
	var original_label = $MainElementsPanel/MainElementsContainer/DifficultyLabelsContainer/DifficultySelectedLabel
	
	if difficulty_selected == 3:
		original_label.text = $MainElementsPanel/MainElementsContainer/DifficultyLevelsContainer/EasyButton.text
	elif difficulty_selected == 2:
		original_label.text = $MainElementsPanel/MainElementsContainer/DifficultyLevelsContainer/MediumButton.text
	else:
		original_label.text = $MainElementsPanel/MainElementsContainer/DifficultyLevelsContainer/HardButton.text


func _on_ReturnWelcomeMenu_pressed():
	Select2.play()
	SaveSystem.save_value_user("Online", "Account", "0")
	
	GlobalVariables.actual_score_registered = 0
	
	get_tree().change_scene("res://menus/main_menus/WelcomeMenu.tscn")
	queue_free()


func _on_OptionsButton_pressed():
	Select1.play()
	$SettingsMenu.show()


func _on_ExitButton_pressed():
	get_tree().quit()


func _on_EasyButton_pressed():
	Select1.play()
	GlobalVariables.difficulty_selected = 3
	_reloadLabel(GlobalVariables.difficulty_selected)


func _on_MediumButton_pressed():
	Select1.play()
	GlobalVariables.difficulty_selected = 2
	_reloadLabel(GlobalVariables.difficulty_selected)


func _on_HardButton_pressed():
	Select1.play()
	GlobalVariables.difficulty_selected = 1
	_reloadLabel(GlobalVariables.difficulty_selected)


func _on_ProfileButton_pressed():
	Select1.play()
	get_tree().change_scene("res://menus/main_menus/ProfileMenu.tscn")
	queue_free()


func _on_LeaderboardButton_pressed():
	Select1.play()
	get_tree().change_scene("res://menus/main_menus/LeaderboardMenu.tscn")
	queue_free()


func _on_PlayButton_pressed():
	Select1.play()
	
	BackgroundMusic.playing = false
	
	# Queue Free done in the below function
	LoadingProgressBarScreen.load_scene_progress_bar(self, "res://levels/MainLevel.tscn")
