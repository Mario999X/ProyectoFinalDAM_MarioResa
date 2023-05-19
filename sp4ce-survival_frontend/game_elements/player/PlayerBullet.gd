extends Area2D

export var speed = 10

var im_bullet_player

signal enemy_hit

var direction := Vector2.ZERO

func _ready():
	pass

func _physics_process(delta):
	if direction != Vector2.ZERO:
		var velocity = direction * speed
		
		global_position += velocity


func set_direction(direction: Vector2):
	self.direction = direction
	self.rotation = direction.angle()


func _on_VisibilityNotifier2D_screen_exited():
	queue_free()


func _on_PlayerBullet_area_entered(area):
	if "im_bullet_t1" in area:
		print("Hit against t1")
		queue_free()
		area.queue_free()
		
	if "im_bullet_t2" in area:
		print("Hit against t2")
		queue_free()
		
	if "im_enemy_ship" in area:
		print("Hit againts enemy ship")
		queue_free()
		area.queue_free()
		
		emit_signal("enemy_hit")

