extends CanvasLayer


func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	set_options()


func set_options():
	var soundMuted = SaveSystem.load_value("Sound", "Muted")
	
	if soundMuted == "Off":
		$SoundElementsPanel/SoundElementsContainer/MutedCheckButton.pressed = false
	else: $SoundElementsPanel/SoundElementsContainer/MutedCheckButton.pressed = true
	
	var fullScreen = SaveSystem.load_value("Screen", "FullScreen")
	
	if fullScreen == "Off":
		$ScreenElementsPanel/ScreenElementsContainer/FullScreenCheckButton.pressed = false
	else: $ScreenElementsPanel/ScreenElementsContainer/FullScreenCheckButton.pressed = true



func _on_ReturnSettingsButton_pressed():
	visible = false


func _on_MutedCheckButton_toggled(button_pressed):
	var bus_idx = AudioServer.get_bus_index("Master")
	
	if button_pressed == false: #off
		AudioServer.set_bus_mute(bus_idx, false)
		SaveSystem.save_value("Sound", "Muted", "Off")
	else:
		AudioServer.set_bus_mute(bus_idx, true)
		SaveSystem.save_value("Sound", "Muted", "On")


func _on_FullScreenCheckButton_toggled(button_pressed):
	if button_pressed == false:
		OS.window_fullscreen = false
		SaveSystem.save_value("Screen", "FullScreen", "Off")
	else:
		OS.window_fullscreen = true
		SaveSystem.save_value("Screen", "FullScreen", "On")


func _on_SpanishButton_pressed():
	TranslationServer.set_locale("es_ES")
	SaveSystem.save_value("Lenguages", "Lenguage", "es")


func _on_EnglishButton_pressed():
	TranslationServer.set_locale("en_US")
	SaveSystem.save_value("Lenguages", "Lenguage", "en")
