extends Node
# -- AUTOLOAD --

# Paths for the main directory and the save file for settings
var save_path = "res://save_data"
var save_path2 = "res://save_data/save-file.cfg"

# Save File User with Personal Information, we use the 0S unique ID as a password
var save_path_user = "res://save_data/save-file-user.cfg"
var password = OS.get_unique_id()

# Settings File
var config = ConfigFile.new()

# User Personal File
var config_user = ConfigFile.new()

# Load of both files
# If the load of the encrypted Personal file fails, it is deleted and a new one is generated.
func _init():
	var _load_response = config.load(save_path2)
	
	var _load_response_user = config_user.load_encrypted_pass(save_path_user, password)
	if _load_response_user == ERR_FILE_CORRUPT:
		print("Deleting Old User File!")
		Directory.new().remove(save_path_user)
	

# We check if the files exists, if not, we set default values
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
	
	var file_user = File.new()
	
	if file_user.file_exists(save_path_user):
		print("UserFile Found!")
	else:
		print("UserFile Not Found! | Preparing Default Values...")
		save_value_user("Online", "Account", "0")

# Function to save Personal Information (Encrypted)
func save_value_user(section, key, value):
	config_user.set_value(section, key, value)
	config_user.save_encrypted_pass(save_path_user, password)

# Function to load Personal Information (Encrypted)
func load_value_user(section, key):
	return config_user.get_value(section, key)

# Function to save Information
func save_value(section, key, value):
	config.set_value(section, key, value)
	config.save(save_path2)

# Function to load Information
func load_value(section, key):
	return config.get_value(section, key)

