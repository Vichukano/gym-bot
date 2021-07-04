# gym-bot

Bot helps to store progress in gym
--
Need config file for run. Pass the path to the configuration through arguments like:

java - jar gym-bot.jar path/to/config/bot.properties

Config file example:

~~~~
#bot name
name=MyBotName
#bot token
token=MyBotToken
#path to store where save files with trainings. If not set, then files will store in system temp dir
store=/bot/store/
~~~~

![build](https://github.com/Vichukano/gym-bot/workflows/maven.yaml/badge.svg)