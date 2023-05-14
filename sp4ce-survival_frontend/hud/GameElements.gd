extends Control

signal pause_button

func _ready():
	pass


func _on_PauseButton_pressed():
	Select1.play()
	emit_signal("pause_button")
