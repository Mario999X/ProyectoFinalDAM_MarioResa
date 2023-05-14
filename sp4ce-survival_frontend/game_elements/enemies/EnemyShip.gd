extends Area2D

var im_enemy_ship

export var speed = 0
var velocity = Vector2.ZERO

func _ready():
	pass
	



func _on_VisibilityNotifier2D_screen_exited():
	queue_free()



func _on_EnemyShip_body_entered(body):
	if body.has_method("hit_by_enemy"):
		body.hit_by_enemy()
		queue_free()
