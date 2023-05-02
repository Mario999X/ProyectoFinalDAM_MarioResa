extends CanvasLayer

var settings_instance = load("res://menus/secundary_menus/SettingsMenu.tscn").instance()

func _ready():
	add_child(settings_instance) 
	settings_instance.visible = false
	
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	var onlineMode = SaveSystem.load_value("Online", "Account")
	
	_reloadLabel(GlobalVariables.difficultySelected)
	
	if onlineMode == "Off":
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
	get_tree().change_scene("res://menus/main_menus/WelcomeMenu.tscn")
	queue_free()


func _on_OptionsButton_pressed():
	settings_instance.visible = true


func _on_ExitButton_pressed():
	get_tree().quit()


func _on_EasyButton_pressed():
	GlobalVariables.difficultySelected = 0
	_reloadLabel(GlobalVariables.difficultySelected)


func _on_MediumButton_pressed():
	GlobalVariables.difficultySelected = 1
	_reloadLabel(GlobalVariables.difficultySelected)


func _on_HardButton_pressed():
	GlobalVariables.difficultySelected = 2
	_reloadLabel(GlobalVariables.difficultySelected)
