extends Node

signal game_over

const enemy_ship_scene = preload("res://game_elements/enemies/EnemyShip.tscn")

var score
var lives

var _total_enemies


var effect = preload("res://game_elements/effects/Explosion.tscn")


# We change the current music, and set the level variables; also, we check the current registered score if the player is connected.
func _ready():
	print(get_tree().current_scene.name, " | ", OS.get_time().hour, ":", OS.get_time().minute)
	
	BackgroundMusic.stream = load("res://assets/sounds/music/loops/Game_Music.mp3")
	BackgroundMusic.playing = true
	
	lives = GlobalVariables.difficulty_selected
	_total_enemies = 0
	score = 0
	
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	if online_mode != "0" and GlobalVariables.actual_score_registered == 0:
		_obtain_actual_score_query(online_mode)
	
	_global_signals_connect_on_level()

# We start the information of the player and the game.
func _on_PlayerHUD_start_game():
	$Player.start($PlayerSpawnLocation.position)
	$PlayerHUD.update_ammo($Player.ammo)
	$PlayerHUD.update_lives(lives)
	
	$PlayerRespawnTimer.start(); yield($PlayerRespawnTimer, "timeout")
	
	$ScoreUpdateTimer.start()
	$GameTimerDuration.start()
	$PlayerHUD.update_score(score)
	$PlayerHUD.update_timer_duration(floor($GameTimerDuration.time_left))
	$EnemyShipTimer.start()

# For every second, we update the score
func _on_ScoreUpdateTimer_timeout():
	score += 1
	$PlayerHUD.update_score(score)
	$PlayerHUD.update_timer_duration(floor($GameTimerDuration.time_left))
	


func _on_Player_reload_complete():
	$PlayerHUD.update_ammo($Player.ammo)

# We handle the appearance of enemies on the screen.
func _on_EnemyShipTimer_timeout():
	if _total_enemies < 5:
		_total_enemies += 1
		var enemy = enemy_ship_scene.instance()
		var enemy_spawn_location = $MobPath/MobSpawnLocation
		
		enemy_spawn_location.offset = randi()
		
		var direction = enemy_spawn_location.rotation + PI / 2
		enemy.position = enemy_spawn_location.position
		
		direction += rand_range(-PI / 4, PI / 4)
		enemy.rotation = direction
		
		add_child(enemy)
	else:
		return
	

# We act if the player has been hit, this can end the game
func _on_Player_hit_by_enemy():
	var explosion = effect.instance()
	add_child(explosion)
	explosion.global_position = Vector2($Player.position)
	
	PlayerDeath.play()
	
	var enemy_ships = get_tree().get_nodes_in_group("Enemy_Ships")
	for enemy in enemy_ships:
		enemy.queue_free()
		
	var bullets_t1 = get_tree().get_nodes_in_group("Enemy_Bullets_T1")
	for bullet_t1 in bullets_t1:
		bullet_t1.queue_free()
		
	var bullets_t2 = get_tree().get_nodes_in_group("Enemy_Bullets_T2")
	for bullet_t2 in bullets_t2:
		bullet_t2.queue_free()
	
	lives -= 1
	$PlayerHUD.update_lives(lives)
	
	$EnemyShipTimer.stop()
	$ScoreUpdateTimer.stop()
	
	if lives == 0:
		$GameTimerDuration.stop()
		match (GlobalVariables.difficulty_selected):
			1:
				score = score * 7
			2:
				score = score * 4
			3:
				score = score * 2
		
		GlobalVariables.actual_score_obtained = score
		emit_signal("game_over")
	else:
		$GameTimerDuration.paused = true
		
		_reset_locations()

# If the player has not lost all lives, the positions are reset and the game continues.
func _reset_locations():
	$PlayerRespawnTimer.start(); yield($PlayerRespawnTimer, "timeout")
	
	$Player.start($PlayerSpawnLocation.position)
	$PlayerHUD.update_ammo($Player.ammo)
	
	$ScoreUpdateTimer.start()
	$GameTimerDuration.paused = false
	$EnemyShipTimer.start()

# We act every time the player shoots
func _on_Player_shoot():
	if $Player.ammo >= 0:
		$PlayerHUD.update_ammo($Player.ammo)
	else:
		return

# It acts according to the response received from the server
func _on_ActualScoreRegistered_request_completed(result, response_code, headers, body):
	# Timeout
	if response_code == 0:
		SaveSystem.save_value_user("Online", "Account", "0")
	# Connected
	elif response_code == 204:
		return
	else:
		var response = JSON.parse(body.get_string_from_utf8()).result
		
		if (response_code == 200):
			GlobalVariables.actual_score_registered = int(response.scoreNumber)
			print(GlobalVariables.actual_score_registered)

		if (response_code == 403):
			SaveSystem.save_value_user("Online", "Account", "0")
			

# The game ends when the main counter reaches 0
func _on_GameTimerDuration_timeout():
	$EnemyShipTimer.stop()
	$ScoreUpdateTimer.stop()
	
	$Player.hide()
	$Player.set_deferred("disabled", true)
	
	score = score + 1000
	match (GlobalVariables.difficulty_selected):
		1:
			score = score * 7
		2:
			score = score * 4
		3:
			score = score * 2
		
	GlobalVariables.actual_score_obtained = score
	emit_signal("game_over")
	

# We completely finish the game and take the player to the next menu
func _on_MainLevel_game_over():
	BackgroundMusic.playing = false
	$PlayerRespawnTimer.start(); yield($PlayerRespawnTimer, "timeout")
	
	get_tree().change_scene("res://menus/main_menus/PostGameMenu.tscn")
	queue_free()

# We connect the global variables
func _global_signals_connect_on_level():
	GlobalSignals.connect("enemy_t1_hit", self, "_on_enemy_bullet_t1_hit")
	GlobalSignals.connect("enemy_ship_hit", self, "_on_enemy_ship_hit")
	GlobalSignals.connect("enemy_out_of_reach", self, "_on_enemy_out_of_reach")

# It is taken into account when the enemy has left the field of vision
func _on_enemy_out_of_reach():
	print("On level, enemy ship out of reach")
	_total_enemies -= 1

# It is triggered if the player manages to hit an enemy ship
func _on_enemy_ship_hit():
	print("On level, enemy ship hit")
	score += 10
	_total_enemies -= 1

# It is triggered if the player manages to hit an enemy bullet T1
func _on_enemy_bullet_t1_hit():
	print("On level, T1 Hit")
	score += 3

# Request to obtain actual score registered
func _obtain_actual_score_query(token):
	var url = "https://localhost:6969/sp4ceSurvival/score"
	var headers = ["Authorization: Bearer " + token]
	
	$ActualScoreRegistered.request(url, headers, false, HTTPClient.METHOD_GET)
