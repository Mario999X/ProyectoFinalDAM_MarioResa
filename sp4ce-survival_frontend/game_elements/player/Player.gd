extends KinematicBody2D

const BULLET = preload("res://game_elements/player/PlayerBullet.tscn")

export var speed = 175
export var ammo = 10

var can_shoot = true

var screen_size

func _ready():
	screen_size = get_viewport_rect().size

func _physics_process(delta):
	look_at(get_global_mouse_position())
	
	var velocity = Vector2.ZERO
	
	if Input.is_action_pressed("move_up"):
		velocity.y -=1
	if Input.is_action_pressed("move_down"):
		velocity.y +=1  
	if Input.is_action_pressed("move_left"):
		velocity.x -=1  
	if Input.is_action_pressed("move_right"):
		velocity.x +=1  
	move_and_collide(velocity * speed * delta)
	
	position += velocity * delta
	position.x = clamp(position.x, 0, screen_size.x)
	position.y = clamp(position.y, 0, screen_size.y)
	
	if Input.is_action_just_pressed("shoot"):
		shoot()
	

func reload():
	can_shoot = false
	PlayerGun.stream = load("res://assets/sounds/effects/Reload.wav")
	$ReloadTimer.start()


func shoot():
	if can_shoot:
		PlayerGun.play()
		print(str(ammo))
		ammo -=1
		
		var bullet_instance = BULLET.instance()
	
		get_parent().add_child(bullet_instance)
	
		bullet_instance.global_position = $BulletsSpawn.global_position
	
		var target = get_global_mouse_position()
	
		var direction = bullet_instance.global_position.direction_to(target).normalized()
	
		bullet_instance.set_direction(direction)
		
		if ammo < 1:
			reload()
	else:
		PlayerGun.play()
		print("Reloading...")
	


func _on_ReloadTimer_timeout():
	PlayerGun.stream = load("res://assets/sounds/effects/Shoot.wav")
	ammo = 10
	can_shoot = true
