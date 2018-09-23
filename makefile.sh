if [ -n `which java` ]; then
	echo "Environment is already set up"
else
	echo "Java Not Installed"
	sudo apt-get update && apt-get upgrade -y
	sudo apt-get install default-jdk -y
	sudo add-apt-repository ppa:linuxuprising/java -y
	sudo apt-get update
	sudo apt-get install oracle-java10-installer -y
	sudo apt install oracle-java10-set-default -y
	export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:/bin/java::")
fi

javac -cp zookeeper-3.4.12.jar *.java
if [ $# -eq 3 ]; then
	
	if [ $1 = "watcher" ]; then
		java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar ScoreWatcher watcher $2 $(($3 + 1 - 1))	
	elif [ $1 = "player" ]; then
		java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar Player player $2 $3
	else
		echo "Please enter valid inputs"
	fi
	
elif [ $# -eq 6 ]; then

	if [ $1 = "player" ]; then
		java -cp .:zookeeper-3.4.12.jar:slf4j-simple-1.7.25.jar:slf4j-api-1.7.25.jar Player player $2 $3 $(($4 + 1 - 1)) $(($5 + 1 -1)) $(($6 + 1 - 1))
	else
		echo "Please Enter valid arguments"
	fi

else
	echo "Please enter valid arguments"

fi
