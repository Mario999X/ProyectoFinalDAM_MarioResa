extends Area2D

var im_bullet_t2

func _ready():
	pass



func _on_EnemyBulletT2_body_entered(body):
	if body.has_method("hit_by_enemy"):
		body.hit_by_enemy()
		queue_free()
