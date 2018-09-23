# ZooKeeper-Distributed-Scoreboard
DIC 591 HW 1
# Player execution
java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar Player player < host> < name>

java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar Player player < host> <"first last">

java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar Player player < host> < name> < count> < delay> < score>

# Watcher execution
java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar ScoreWatcher watcher < host> < size>

# Miscellaneous
# Compiling .java
javac -cp zookeeper-3.4.12.jar *.java 



# Exection Steps when Executing with Makefile

# Watcher execution and environment setup

./makefile.sh watcher < host> < size>

# Player Execution for user input of scores

./makefile.sh player < host> < name>

# Player Execution automated

./makefile.sh player < host> < name> < count> < delay> < score>