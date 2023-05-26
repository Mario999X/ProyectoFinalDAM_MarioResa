extends Area2D

export var speed = 150

var im_bullet_t2

func _process(delta):
	position += transform.x * speed * delta


func _on_EnemyBulletT2_body_entered(body):
	if body.has_method("hit_by_enemy"):
		body.hit_by_enemy()
		queue_free()


func _on_VisibilityNotifier2D_screen_exited():
	queue_free()
