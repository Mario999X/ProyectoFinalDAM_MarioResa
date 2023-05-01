extends Node

var save_path = "res://save_data"
var save_path2 = "res://save_data/save-file.cfg"

var config = ConfigFile.new()

var _load_response = config.load(save_path2)

func _ready():
	
	var directory = Directory.new()
	
	if directory.dir_exists(save_path):
		print("Directory Found!")
	else:
		print("Directory Not Found! | Creating...")
		directory.make_dir_recursive(save_path)
	
	
	var file = File.new()
	
	if file.file_exists(save_path2):
		print("Config Found! | Loading...")
	else:
		print("Config Not Found! | Preparing default values...")
		save_value("Lenguages", "Lenguage", "en")
		save_value("Sound", "Muted", "Off")
		save_value("Screen", "FullScreen", "On")


func save_value(section, key, value):
	config.set_value(section, key, value)
	config.save(save_path2)

func load_value(section, key):
	return config.get_value(section, key)

func reset_value(section):
	config.erase_section(section)

