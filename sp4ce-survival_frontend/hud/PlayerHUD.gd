extends CanvasLayer

signal pause_button
signal start_game


func _ready():
	pass
	

func _on_PauseButton_pressed():
	Select1.play()
	emit_signal("pause_button")


func _on_StartButton_pressed():
	Select1.play()
	$PlayerHUDCenterPanel.hide()
	emit_signal("start_game")


func update_score(score: int):
	$PlayerHUDMainPanel/PlayerHUDMainContainer/PlayerHUDLeftContainer/ScoreElementsContainer/ScoreLabelNumber.text = str(score)

func update_ammo(ammo: int):
	$PlayerHUDMainPanel/PlayerHUDMainContainer/PlayerHUDRightContainer/AmmoElementsPanel/AmmoElementsContainer/AmmoLabelNumber.text = str(ammo)
	

func update_timer_duration(time):
	$PlayerHUDMainPanel/PlayerHUDMainContainer/PlayerHUDCenterContainer/PlayerHUDCenterPanel/TimerElementsContainer/TimerLabelNumber.text = str(time)

func update_lives(lives: int):
	$PlayerHUDMainPanel/PlayerHUDMainContainer/PlayerHUDLeftContainer/LivesElementsContainer/LivesLabelNumber.text = str(lives)
