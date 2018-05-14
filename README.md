# vulquery
Obtain vulnerabilities details on your dependencies without having to download and scan them or dig through massive security data feeds. Also makes upgrade path recommendations based on known security vulnerabilities allowing developer to compare and contrast the type, severity, and number of vulnerabilities for code library/dependencies.

This web service is supported with the following:
- AngularJS-Material front-end, visualizations with D3
- Spring Framework (Boot) and Tomcat with RESTful API
- SQLite database for translating data feed from ![NVD NIST GOV](https://nvd.nist.gov/vuln/data-feeds)

### Setup Instructions

#### Cloning to local
```
git clone https://github.com/Michaelis105/vulquery.git
cd vulquery
```

#### Building
```
./gradlew build
```

#### Running
```
java -jar /build/libs/vulquery-X.X.X.jar
```
Open browser and enter `localhost:8080`
