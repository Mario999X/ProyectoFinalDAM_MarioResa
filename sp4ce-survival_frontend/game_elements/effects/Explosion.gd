extends Particles2D

func _ready():
	one_shot = true

func _on_ExplosionTimer_timeout():
	queue_free()