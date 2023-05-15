extends Node

signal game_over
# signal time_added

var total_enemies
var score

func _ready():
	BackgroundMusic.stream = load("res://assets/sounds/music/loops/Game_Music.mp3")
	BackgroundMusic.playing = true
	

func _on_PlayerHUD_start_game():
	score = 0
	total_enemies = 0
	$Player.start($PlayerSpawnLocation.position)
	$ScoreUpdateTimer.start()
	$GameTimerDuration.start()
	$PlayerHUD.update_score(score)
	$PlayerHUD.update_timer_duration(floor($GameTimerDuration.time_left))
	$PlayerHUD.update_ammo($Player.ammo)


func _on_ScoreUpdateTimer_timeout():
	score += 1
	$PlayerHUD.update_score(score)
	$PlayerHUD.update_timer_duration(floor($GameTimerDuration.time_left))


func _input(event):
	if event.is_action_pressed("shoot"):
		if $Player.ammo > 0:
			$PlayerHUD.update_ammo($Player.ammo -1)
		else:
			return


func _on_Player_reload_complete():
	$PlayerHUD.update_ammo($Player.ammo)
