extends CanvasLayer

signal confirmation

func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)


func _on_YesButton_pressed():
	Select1.play()
	emit_signal("confirmation")
	self.hide()


func _on_NoButton_pressed():
	Select2.play()
	self.hide()
