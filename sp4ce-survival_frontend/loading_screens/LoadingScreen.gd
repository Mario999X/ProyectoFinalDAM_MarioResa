extends CanvasLayer

func _ready():
	var viewport_size = get_viewport().get_visible_rect().size
	var sprite_position = Vector2(viewport_size.x / 2, viewport_size.y / 2 - 150)
	$Sprite.position = sprite_position
	

func _on_LoadScreen_visibility_changed():
	if self.visible == true:
		$Sprite.playing = true
	else:
		$Sprite.playing = false
		$Sprite.frame = 0
