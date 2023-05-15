extends CanvasLayer

signal pause_button
signal start_game

func _ready():
	GlobalVariables.message_player_level = "USER_MESSAGE_LEVEL" # The enemies are coming, get ready!
	$PlayerHUDCenterPanel/PlayerHUDCenterContainer/PlayerMessage.text = GlobalVariables.message_player_level
	

func show_message(status: int):
	if status == 1:
		GlobalVariables.message_player_level = "USER_MESSAGE_LEVEL_1"
	if status == 2:
		GlobalVariables.message_player_level = "USER_MESSAGE_LEVEL_2"
		
	$PlayerHUDCenterPanel/PlayerHUDCenterContainer/PlayerMessage.text = GlobalVariables.message_player_level

func show_game_over():
	show_message(1)
	
	$PlayerHUDCenterPanel.show()
	

func update_score(score: int):
	$PlayerHUDMainPanel/PlayerHUDMainContainer/PlayerHUDLeftContainer/ScoreElementsContainer/ScoreLabelNumber.text = str(score)

func update_ammo(ammo: int):
	$PlayerHUDMainPanel/PlayerHUDMainContainer/PlayerHUDRightContainer/AmmoElementsContainer/AmmoLabelNumber.text = str(ammo)
	

func update_timer_duration(time):
	$PlayerHUDMainPanel/PlayerHUDMainContainer/PlayerHUDCenterContainer/PlayerHUDCenterPanel/TimerElementsContainer/TimerLabelNumber.text = str(time)

func update_lives(lives: int):
	$PlayerHUDMainPanel/PlayerHUDMainContainer/PlayerHUDLeftContainer/LivesElementsContainer/LivesLabelNumber.text = str(lives)

func _on_PauseButton_pressed():
	Select1.play()
	emit_signal("pause_button")


func _on_StartButton_pressed():
	Select1.play()
	$PlayerHUDCenterPanel.hide()
	emit_signal("start_game")
