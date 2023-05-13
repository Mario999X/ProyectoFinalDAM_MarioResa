extends KinematicBody2D

const bullet = preload("res://game_elements/player/PlayerBullet.tscn")

export var speed = 175
export var ammo = 10

var can_shoot = true

var screen_size

func _ready():
	screen_size = get_viewport_rect().size
