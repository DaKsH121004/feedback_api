#!/bin/bash
export DB_URL="jdbc:h2:mem:testdb"
export DB_USERNAME="sa"
export DB_PASSWORD=""
export JWT_SECRET="mysecretkeymysecretkeymysecretkeymysecretkey"

# modify application.properties to use H2 and add dependency
sed -i '' 's/org.hibernate.dialect.MySQLDialect/org.hibernate.dialect.H2Dialect/g' src/main/resources/application.properties
sed -i '' 's/spring.datasource.url=${DB_URL}/spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE/g' src/main/resources/application.properties
sed -i '' 's/spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver/spring.datasource.driverClassName=org.h2.Driver/g' src/main/resources/application.properties

cp pom.xml pom.xml.bak
sed -i '' '/<\/dependencies>/i \
		<dependency>\
			<groupId>com.h2database</groupId>\
			<artifactId>h2</artifactId>\
			<scope>runtime</scope>\
		</dependency>' pom.xml

./mvnw clean package -DskipTests
java -jar target/feedback-0.0.1-SNAPSHOT.jar &
PID=$!
while ! nc -z localhost 8080; do sleep 1; done
sleep 2

# We need to test the CURRENT code behavior first!
curl -s -X POST http://localhost:8080/api/v1/auth/register -H "Content-Type: application/json" -d '{"fullName":"Test Admin","email":"test@admin.com","password":"password"}'
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login -H "Content-Type: application/json" -d '{"email":"test@admin.com","password":"password"}' | jq -r .token)

echo "Calling DELETE /api/v1/course/all on NEW server"
curl -s -X DELETE -i http://localhost:8080/api/v1/course/all -H "Authorization: Bearer $TOKEN" > result_new.txt

kill $PID
# Now comment out /all endpoint
sed -i '' 's/@DeleteMapping("\/all")/\/\/@DeleteMapping("\/all")/g' src/main/java/com/feedback/feedback/controllers/CourseController.java
./mvnw clean package -DskipTests
java -jar target/feedback-0.0.1-SNAPSHOT.jar &
PID2=$!
while ! nc -z localhost 8080; do sleep 1; done
sleep 2

curl -s -X POST http://localhost:8080/api/v1/auth/register -H "Content-Type: application/json" -d '{"fullName":"Test Admin2","email":"test2@admin.com","password":"password"}'
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login -H "Content-Type: application/json" -d '{"email":"test2@admin.com","password":"password"}' | jq -r .token)

echo "Calling DELETE /api/v1/course/all on OLD server"
curl -s -X DELETE -i http://localhost:8080/api/v1/course/all -H "Authorization: Bearer $TOKEN" > result_old.txt

kill $PID2
git checkout pom.xml src/main/resources/application.properties src/main/java/com/feedback/feedback/controllers/CourseController.java
