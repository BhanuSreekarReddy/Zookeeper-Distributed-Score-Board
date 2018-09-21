# Zookeeper-Distributed-Score-Board

Execution Steps
Run Zookeeper Server

Miscellaneous
Compiling all .java files
javac -cp zookeeper-3.4.12.jar *.java

Watcher execution (First Execute Watcher Program and then Player)
java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar ScoreWatcher watcher < host> < size>

Player execution
java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar Player player < host> < name>

java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar Player player < host> <"first last">

java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar Player player < host> < name> < count> < delay> < score>
