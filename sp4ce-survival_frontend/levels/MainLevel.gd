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
	score = 0
	total_enemies = 0
	
	$Player.start($PlayerSpawnLocation.position)
	yield(get_tree().create_timer(1), "timeout")
	
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
	lives -= 1
	if lives == 0:
		$EnemyShipTimer.stop()
		$ScoreUpdateTimer.stop()
	else:
		$PlayerHUD.update_lives(lives)


func _on_Player_shoot():
	if $Player.ammo >= 0:
		$PlayerHUD.update_ammo($Player.ammo)
	else:
		return
