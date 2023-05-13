extends CanvasLayer

func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	
	_reloadLabel(GlobalVariables.difficultySelected)
	
	var onlineMode = SaveSystem.load_value_user("Online", "Account")
	
	if onlineMode == "0":
		$MainElementsPanel/MainElementsContainer/OnlineElementsPanel/OnlineElementsContainer/ProfileButton.disabled = true
		$MainElementsPanel/MainElementsContainer/OnlineElementsPanel/OnlineElementsContainer/LeaderboardButton.disabled = true


func _reloadLabel(difficultySelected):
	var originalLabel = $MainElementsPanel/MainElementsContainer/DifficultyLabelsContainer/DifficultySelectedLabel
	
	if difficultySelected == 0:
		originalLabel.text = $MainElementsPanel/MainElementsContainer/DifficultyLevelsContainer/EasyButton.text
	elif difficultySelected == 1:
		originalLabel.text = $MainElementsPanel/MainElementsContainer/DifficultyLevelsContainer/MediumButton.text
	else:
		originalLabel.text = $MainElementsPanel/MainElementsContainer/DifficultyLevelsContainer/HardButton.text


func _on_ReturnMainMenu_pressed():
	Select2.play()
	SaveSystem.save_value_user("Online", "Account", "0")
	get_tree().change_scene("res://menus/main_menus/WelcomeMenu.tscn")
	queue_free()


func _on_OptionsButton_pressed():
	Select1.play()
	$SettingsMenu.show()


func _on_ExitButton_pressed():
	get_tree().quit()


func _on_EasyButton_pressed():
	Select1.play()
	GlobalVariables.difficultySelected = 0
	_reloadLabel(GlobalVariables.difficultySelected)


func _on_MediumButton_pressed():
	Select1.play()
	GlobalVariables.difficultySelected = 1
	_reloadLabel(GlobalVariables.difficultySelected)


func _on_HardButton_pressed():
	Select1.play()
	GlobalVariables.difficultySelected = 2
	_reloadLabel(GlobalVariables.difficultySelected)


func _on_ProfileButton_pressed():
	Select1.play()


func _on_LeaderboardButton_pressed():
	Select1.play()


func _on_PlayButton_pressed():
	Select1.play()
	get_tree().change_scene("res://levels/MainLevel.tscn")
	queue_free()
