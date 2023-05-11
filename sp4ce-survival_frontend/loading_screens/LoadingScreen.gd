extends CanvasLayer


func _on_LoadScreen_visibility_changed():
	if self.visible == true:
		$Sprite.playing = true
	else:
		$Sprite.playing = false
		$Sprite.frame = 0
