extends Node
# -- AUTOLOAD --

# -- Global variables at runtime --

# Difficulty Selected By The Player / This will be also the number of Lives in-game
# 3 - Easy (default) | 2 - Medium | 1 - Hard
var difficulty_selected = 3

# Message to show to the player in the load screen for HTTP Request
var message_http_request = null


# Score Registered for User [ONLINE] - 0 Default
var actual_score_registered = 0

# Score obtained for User in One Game, this is set after every game completed
var actual_score_obtained = null
