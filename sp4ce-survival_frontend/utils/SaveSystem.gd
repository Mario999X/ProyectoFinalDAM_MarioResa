extends Node
# -- AUTOLOAD --

# Paths for the main directory and the save file for settings
var _save_path
var _save_path2

# Save File User with Personal Information, we use the 0S name as a password
var _save_path_user
var _password = OS.get_name()

# Settings File
var _config = ConfigFile.new()

# User Personal File
var _config_user = ConfigFile.new()

# We verify the way of execution of the application and then we decide the storage paths
func _init():
	if OS.has_feature("editor"):
		_save_path = ProjectSettings.globalize_path("res://save_data")
		_save_path2 = ProjectSettings.globalize_path("res://save_data/save-file.cfg")
		
		_save_path_user = ProjectSettings.globalize_path("res://save_data/save-file-user.cfg")
	else:
		_save_path = OS.get_executable_path().get_base_dir().plus_file("save_data")
		_save_path2 = OS.get_executable_path().get_base_dir().plus_file("save_data/save-file.cfg")
		
		_save_path_user = OS.get_executable_path().get_base_dir().plus_file("save_data/save-file-user.cfg")

# We check if the files exists, if not, we set default values
func _ready():
	var _load_response = _config.load(_save_path2)
	
	var directory = Directory.new()
	
	var _load_response_user = _config_user.load_encrypted_pass(_save_path_user, _password)
	if _load_response_user == ERR_FILE_CORRUPT:
		print("Deleting Old User File!")
		directory.new().remove(_save_path_user)
		
	
	if directory.dir_exists(_save_path):
		print("Directory Found!")
	else:
		print("Directory Not Found! | Creating...")
		directory.make_dir_recursive(_save_path)
	
	
	var file = File.new()
	
	if file.file_exists(_save_path2):
		print("Config Found! | Loading...")
	else:
		print("Config Not Found! | Preparing default values...")
		save_value("Lenguages", "Lenguage", "en")
		save_value("Sound", "Muted", "Off")
		save_value("Screen", "FullScreen", "On")
	
	var file_user = File.new()
	
	if file_user.file_exists(_save_path_user):
		print("UserFile Found!")
	else:
		print("UserFile Not Found! | Preparing Default Values...")
		save_value_user("Online", "Account", "0")

# Function to save Personal Information (Encrypted)
func save_value_user(section, key, value):
	_config_user.set_value(section, key, value)
	_config_user.save_encrypted_pass(_save_path_user, _password)

# Function to load Personal Information (Encrypted)
func load_value_user(section, key):
	return _config_user.get_value(section, key)

# Function to save Information
func save_value(section, key, value):
	_config.set_value(section, key, value)
	_config.save(_save_path2)

# Function to load Information
func load_value(section, key):
	return _config.get_value(section, key)

