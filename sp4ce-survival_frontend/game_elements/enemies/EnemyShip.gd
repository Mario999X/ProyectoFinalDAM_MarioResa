extends Area2D

const bullet_t1_scene = preload("res://game_elements/enemies/EnemyBulletT1.tscn")
const bullet_t2_scene = preload("res://game_elements/enemies/EnemyBulletT2.tscn")

# Fields for the shot pattern
const rotate_speed = 75
const shooter_timer_wait_time = 0.8
const spawn_point_count = 4
const radius = 25

export var speed = 175

var im_enemy_ship
var velocity = Vector2.ZERO

onready var shoot_timer = $ShootTimer
onready var rotater = $Rotater


func _ready():
	var stop = 2 * PI / spawn_point_count
	
	for i in range(spawn_point_count):
		var spawn_point = Node2D.new()
		var pos = Vector2(radius, 0).rotated(stop * i)
		spawn_point.position = pos
		spawn_point.rotation = pos.angle()
		rotater.add_child(spawn_point)
	
	shoot_timer.wait_time = shooter_timer_wait_time
	shoot_timer.start()
	


func _process(delta):
	var new_rotation = rotater.rotation_degrees + rotate_speed * delta
	rotater.rotation_degrees = fmod(new_rotation, 360)
	
	position += transform.x * speed * delta


func _on_VisibilityNotifier2D_screen_exited():
	GlobalSignals.emit_signal("enemy_out_of_reach")
	queue_free()



func _on_EnemyShip_body_entered(body):
	if body.has_method("hit_by_enemy"):
		body.hit_by_enemy()
		queue_free()


func _on_ShootTimer_timeout():
	var random_number = randi() % 2 + 1
	var bullet
	for s in rotater.get_children():
		if random_number == 2:
			bullet = bullet_t1_scene.instance()
		else:
			bullet = bullet_t2_scene.instance()
		get_parent().add_child(bullet)
		bullet.position = s.global_position
		bullet.rotation = s.global_rotation
