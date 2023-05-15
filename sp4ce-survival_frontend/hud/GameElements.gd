extends Control

signal pause_button

func _ready():
	$PlayerHUDMainPanel/PlayerHUDMainContainer/PlayerHUDLeftContainer/LivesElementsContainer/LivesLabelNumber.text = str(GlobalVariables.difficulty_selected)


func _on_PauseButton_pressed():
	Select1.play()
	emit_signal("pause_button")
