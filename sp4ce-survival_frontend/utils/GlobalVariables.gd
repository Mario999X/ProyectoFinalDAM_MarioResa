extends Node
# -- AUTOLOAD --

# -- Global variables at runtime --

# Difficulty Selected By The Player / This will be also the number of Lives in-game
# 3 - Easy (default) | 2 - Medium | 1 - Hard
var difficulty_selected = 3

# Message to show to the player in the load screen for HTTP Request
var message_http_request = null

# Message to show to the player in the start of the level
var message_player_level = null

# Main Timer time for the initial duration of the level
var main_timer_duration = null

