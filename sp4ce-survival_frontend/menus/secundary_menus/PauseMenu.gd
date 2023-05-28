extends CanvasLayer

var is_paused = false setget set_paused


func set_paused(value):
	is_paused = value
	get_tree().paused = is_paused
	visible = is_paused
	


func _on_PlayerHUD_pause_button():
	self.is_paused = !is_paused


func _unhandled_key_input(event):
	if event.is_action_pressed("pause_game"):
		if $SettingsMenu.visible == true:
			return
		else:
			self.is_paused = !is_paused


func _on_ResumeGameButton_pressed():
	Select1.play()
	self.is_paused = !is_paused



func _on_ExitGameButton_pressed():
	Select2.play()
	self.is_paused = !is_paused
	
	BackgroundMusic.stream = load("res://assets/sounds/music/loops/Menus_Music.mp3")
	BackgroundMusic.playing = true
	
	get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
	
	get_parent().queue_free()
	


func _on_OptionsButton_pressed():
	Select1.play()
	$SettingsMenu.show()
