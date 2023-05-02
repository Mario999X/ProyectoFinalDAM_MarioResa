extends CanvasLayer

func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	LoadSettings.load_Settings()

func _on_ExitButton_pressed():
	get_tree().quit()


func _on_OptionsButton_pressed():
	$SettingsMenu.visible = true


func _on_PlayOfflineButton_pressed():
	get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
	queue_free()
