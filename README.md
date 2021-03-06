Toy Robot Simulator
===================
## Prerequisites
The application being JVM based - it requires `Java 8` and `sbt` to run (alternatively the `jar` can be run w/o `sbt`).

## Assumptions
The default table size is 5x5 but can be changed.  
Invalid input will be ignored (such as empty line, misspelt command or direction, etc)  
Given the two overlapping instructions:
- `The application should discard all commands in the sequence until a valid PLACE command has been executed.`
- `A robot that is not on the table can choose the ignore the MOVE, LEFT, RIGHT and REPORT commands.`  
the robot / application will ignore ALL commands until a valid PLACE command has been issued.

## 12 factor app - environment variables
The application is configured using environment variables so that settings can easily be changed based on environment or needs.  
Alternative Reporter implementation (fully qualified class name) can be passed as `REPORTER`. Default implementation is `Console`. The other option is `String`.  
Input file with commands can be specified using `INPUT_FILE` property. Unless specified input from STDIN will be used. To end inputting press CTRL+D.  
Size of the table can be specified using `TABLE_BOUNDS` property, which has format `x:y`, with default `5:5`. A table must have size of at least 1x1 (one position only: 0,0).  

## Processing
The application processes a stream of commands by parsing text, filtering out invalid input, discarding illegal commands and executing the correct ones.  
Reporting will be done straight away by writing to STDOUT.   

## Testing
To test - run `sbt test` (for unit tests) and `sbt it:test` (for integration tests) from root of the project.  

## Running
In order to build an uberjar (containing programme + dependencies) run: `./scripts/build.sh`.  
Then run ./script/run.sh setting any required environment variables, eg: `INPUT_FILE=commands.txt ./scripts/run.sh`