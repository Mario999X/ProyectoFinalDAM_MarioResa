extends CanvasLayer
# -- AUTOLOAD --

onready var loading_scene = load("res://loading_screens/LoadingProgressBarScreen.tscn")

# Function to change scenes with a progress bar in the middle of the transition
# The current scene is deleted/queue free, in addition to the transient scene itself.
func load_scene_progress_bar(current_scene, next_scene):
	var loading_scene_instance = loading_scene.instance()
	get_tree().get_root().call_deferred("add_child", loading_scene_instance)
	
	var loader = ResourceLoader.load_interactive(next_scene)
	
	if loader == null:
		print("Error while getting new scene")
		return
		
	
	yield(get_tree().create_timer(0.5), "timeout")
	
	while true:
		var error = loader.poll()
		var progress_bar = loading_scene_instance.get_node("ProgressBar")
			
		if error == OK:
			progress_bar.value = float(loader.get_stage())/loader.get_stage_count() * 100
			
		elif error == ERR_FILE_EOF:
			var scene = loader.get_resource().instance()
			get_tree().get_root().call_deferred("add_child", scene)
			
			current_scene.queue_free()
			loading_scene_instance.queue_free()
			progress_bar.queue_free()
			break
			
		else:
			print("Error while loading data")
			break
