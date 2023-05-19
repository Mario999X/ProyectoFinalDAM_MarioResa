extends Node

const enemy_ship_scene = preload("res://game_elements/enemies/EnemyShip.tscn")

signal game_over
# signal time_added

var total_enemies
var score
var lives

func _ready():
	BackgroundMusic.stream = load("res://assets/sounds/music/loops/Game_Music.mp3")
	BackgroundMusic.playing = true
	
	lives = GlobalVariables.difficulty_selected
	total_enemies = 0
	score = 0
	
	var online_mode = SaveSystem.load_value_user("Online", "Account")
	
	if online_mode != "0" and GlobalVariables.actual_score == 0:
		_obtain_actual_score_query(online_mode)
	
	_global_signals_connect_on_level()

func _global_signals_connect_on_level():
	GlobalSignals.connect("enemy_t1_hit", self, "_on_enemy_bullet_t1_hit")
	GlobalSignals.connect("enemy_ship_hit", self, "_on_enemy_ship_hit")
	GlobalSignals.connect("enemy_out_of_reach", self, "_on_enemy_out_of_reach")

func _on_enemy_out_of_reach():
	print("On level, enemy ship out of reach")
	total_enemies -= 1

func _on_enemy_ship_hit():
	print("On level, enemy ship hit")
	score += 5
	total_enemies -= 1

func _on_enemy_bullet_t1_hit():
	print("On level, T1 Hit")
	score += 1

func _on_PlayerHUD_start_game():
	$Player.start($PlayerSpawnLocation.position)
	yield(get_tree().create_timer(2), "timeout")
	
	$ScoreUpdateTimer.start()
	$GameTimerDuration.start()
	$PlayerHUD.update_lives(lives)
	$PlayerHUD.update_score(score)
	$PlayerHUD.update_timer_duration(floor($GameTimerDuration.time_left))
	$PlayerHUD.update_ammo($Player.ammo)
	$EnemyShipTimer.start()


func _on_ScoreUpdateTimer_timeout():
	score += 1
	$PlayerHUD.update_score(score)
	$PlayerHUD.update_timer_duration(floor($GameTimerDuration.time_left))
	

func _on_Player_reload_complete():
	$PlayerHUD.update_ammo($Player.ammo)


func _on_EnemyShipTimer_timeout():
	if total_enemies < 5:
		total_enemies += 1
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
	


func _on_Player_hit_by_enemy():
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
	else:
		$GameTimerDuration.paused = true
		
		_reset_locations()


func _reset_locations():
	yield(get_tree().create_timer(2), "timeout")
	
	$Player.start($PlayerSpawnLocation.position)
	
	yield(get_tree().create_timer(2), "timeout")
	
	$ScoreUpdateTimer.start()
	$GameTimerDuration.paused = false
	$PlayerHUD.update_ammo($Player.ammo)
	$EnemyShipTimer.start()


func _on_Player_shoot():
	if $Player.ammo >= 0:
		$PlayerHUD.update_ammo($Player.ammo)
	else:
		return

func _obtain_actual_score_query(token):
	var url = "https://localhost:6969/sp4ceSurvival/score"
	var headers = ["Authorization: Bearer " + token]
	
	$ActualScoreRegistered.request(url, headers, false, HTTPClient.METHOD_GET)


func _on_ActualScoreRegistered_request_completed(result, response_code, headers, body):
	# Timeout
	if response_code == 0:
		SaveSystem.save_value_user("Online", "Account", "0")
	# Connected
	if response_code == 204:
		return
	else:
		var response = JSON.parse(body.get_string_from_utf8()).result
		
		if (response_code == 200):
			GlobalVariables.actual_score = int(response.scoreNumber)
			print(GlobalVariables.actual_score)

		if (response_code == 403):
			SaveSystem.save_value_user("Online", "Account", "0")
			
