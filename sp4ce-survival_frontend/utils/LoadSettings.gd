extends Node
# -- AUTOLOAD --

var screen_size = OS.get_screen_size()
var window_size = OS.get_window_size()

# Function to load the settings from the user.
func load_Settings():
	var lenguage = SaveSystem.load_value("Lenguages", "Lenguage")
	
	if lenguage == "es":
		TranslationServer.set_locale("es_ES")
	if lenguage == "en":
		TranslationServer.set_locale("en_US")

	var sound_muted = SaveSystem.load_value("Sound", "Muted")
	var bus_idx = AudioServer.get_bus_index("Master")
	
	if sound_muted == "Off":
		AudioServer.set_bus_mute(bus_idx, false)
	else:
		AudioServer.set_bus_mute(bus_idx, true)
	
	var fullScreen = SaveSystem.load_value("Screen", "FullScreen")
	
	if fullScreen == "Off":
		OS.window_fullscreen = false
		
		OS.set_window_position(screen_size*0.5 - window_size*0.5)
		
	else:
		OS.window_fullscreen = true
	
