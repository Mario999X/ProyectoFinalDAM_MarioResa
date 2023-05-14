extends Control

var is_paused = false setget set_paused


func set_paused(value):
	is_paused = value
	get_tree().paused = is_paused
	visible = is_paused
	

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
	queue_free()
	
	get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")


func _on_OptionsButton_pressed():
	Select1.play()
	$SettingsMenu.show()


func _on_PlayerHUD_pause_button():
	self.is_paused = !is_paused
