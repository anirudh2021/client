This is a repo for a Maven-based Java application that reads a file at a directory into HashMap, filters HashMap keys using regex expression and sends it to a server

You require Maven and Java installed on your local machine to build and run this application

This application requires the path of the Java properties file as input. Below are instructions to set the file
1. Create a new file in your local machine
2. Open the file in a text editor
3. Add key-value pairs to it. Each key-value pair should be on a new line and separated by an equal sign (=). For example: property.name=property.value
4. Save the file
5. Note the complete path to the file as this is required for starting the application

Below are the required properties for this application with example values, make sure to follow same format
Use "client.job.enable.scheduler.flag" to true use scheduler or false to execute the currently available file in directory
Use "client.job.cron.expression" to set the scheduler run time
Use "input.file.directory.path" to set the input directory
Use "input.file.name.extention" to filter files by extension/suffix. This property can be left empty to process all file types
Use "input.file.keys.regex.filter" to set regex expression to filter key in the input file
Use "server.url" to set the server URL
Always set "server.request.charset" to "UTF-8" as this required on the server side to decode messages
Always set "server.request.contentType" to "application/json" as this required on the server side to decode messages

Below is an example of the properties file

#input application properties
client.job.enable.scheduler.flag=true
client.job.cron.expression=0 0/5 * * * ?
input.file.directory.path=C:\\Users\\aniru\\clientJobInputDir
input.file.name.extention=.properties
input.file.keys.regex.filter=.*0.*
server.url=http://localhost:8091/
server.request.charset=UTF-8
server.request.contentType=application/json



To build and run this application follow the below steps
1. Pull this repository to your local machine
2. Open the terminal/command prompt and change the path to the recently downloaded repo folder
3. Run the following command to build the application
	mvn package
4. Run the following command to execute the application
	java -jar target\client-1.0-SNAPSHOT-jar-with-dependencies.jar path-to-application-properties-folder
