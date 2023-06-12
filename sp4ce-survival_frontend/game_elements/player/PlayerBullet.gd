extends Area2D

export var speed = 10

var im_bullet_player
var direction := Vector2.ZERO


func _ready():
	pass

func _physics_process(delta):
	if direction != Vector2.ZERO:
		var velocity = direction * speed
		
		global_position += velocity

func _on_VisibilityNotifier2D_screen_exited():
	queue_free()


func _on_PlayerBullet_area_entered(area):
	var level_node = get_parent()
	
	if "im_bullet_t1" in area:
		#print("Hit against t1")
		queue_free()
		area.queue_free()
		
		GlobalSignals.emit_signal("enemy_t1_hit")
		
	if "im_bullet_t2" in area:
		#print("Hit against t2")
		queue_free()
		
	if "im_enemy_ship" in area:
		#print("Hit againts enemy ship")
		queue_free()
		area.queue_free()
		
		GlobalSignals.emit_signal("enemy_ship_hit")


func set_direction(direction: Vector2):
	self.direction = direction
	self.rotation = direction.angle()




