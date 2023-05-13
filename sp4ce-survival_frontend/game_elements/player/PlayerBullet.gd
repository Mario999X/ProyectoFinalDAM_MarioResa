extends Area2D

export var speed = 10

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
