pathToGitSrc=/home/mario/Desktop/tmp/java-1-hse-2021/
current_directory=$(pwd)
args=${*:1}
$pathToGitSrc/gradlew -p $pathToGitSrc :run --args="$current_directory $args"