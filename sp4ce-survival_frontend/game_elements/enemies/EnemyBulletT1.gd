extends Area2D

var im_bullet_t1

export var speed = 200

func _process(delta):
	position += transform.x * speed * delta


func _on_EnemyBulletT1_body_entered(body):
	if body.has_method("hit_by_enemy"):
		body.hit_by_enemy()
		queue_free()


func _on_VisibilityNotifier2D_screen_exited():
	queue_free()
