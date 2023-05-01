extends Node

func load_Settings():
	var lenguage = SaveSystem.load_value("Lenguages", "Lenguage")
	
	if lenguage == "es":
		TranslationServer.set_locale("es_ES")
	if lenguage == "en":
		TranslationServer.set_locale("en_US")

	var soundMuted = SaveSystem.load_value("Sound", "Muted")
	var bus_idx = AudioServer.get_bus_index("Master")
	
	if soundMuted == "Off":
		AudioServer.set_bus_mute(bus_idx, false)
	if soundMuted == "On":
		AudioServer.set_bus_mute(bus_idx, true)
	
	var fullScreen = SaveSystem.load_value("Screen", "FullScreen")
	if fullScreen == "Off":
		OS.window_fullscreen = false
	if fullScreen == "On":
		OS.window_fullscreen = true
