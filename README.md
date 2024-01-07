# Example for DistributedLogger
Example using logging library [DistributedSystemLogger](https://github.com/vinayakmp007/DistributedSystemLogger)
## Design
![alt text](https://github.com/vinayakmp007/DistributedLoggerExample/blob/master/Design.png?raw=true)

## Steps
1. Build the maven spring boot project using 
   ``` mvn clean install ```
2. Build Docker compose using
   ``` docker-compose build ```
3. Start Docker compose
   ``` docker-compose up  --scale servicelog=3 ```
4. Access the end points at http://localhost:9090/test/first ,http://localhost:9090/test/second , http://localhost:9090/test/third ,http://localhost:9090/test/fourth
5. Access Kibana to see the indexed logs .
![alt text](https://github.com/vinayakmp007/DistributedLoggerExample/blob/master/Kibana.png?raw=true)
