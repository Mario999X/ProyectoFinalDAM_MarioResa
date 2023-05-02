extends CanvasLayer

var settings_instance = load("res://menus/secundary_menus/SettingsMenu.tscn").instance()

func _ready():
	add_child(settings_instance)
	settings_instance.visible = false
	
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	LoadSettings.load_Settings()

func _on_ExitButton_pressed():
	get_tree().quit()


func _on_OptionsButton_pressed():
	settings_instance.visible = true


func _on_PlayOfflineButton_pressed():
	get_tree().change_scene("res://menus/main_menus/MainMenu.tscn")
	queue_free()
