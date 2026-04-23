# Smart Campus API

A RESTful API for managing Rooms and Sensors across a university Smart Campus infrastructure, built with JAX-RS (Jersey), Tomcat, and Jackson for JSON serialization.

---

## Technology Stack

- Java 11
- JAX-RS (javax.ws.rs)
- Jersey 2.32 (JAX-RS Implementation)
- Apache Tomcat 9.x (Servlet Container)
- Jackson 2.32 (JSON Serialization)
- Maven (Build Tool)

---

## Project Structure

```
smart-campus-api/
├── src/main/java/com/smartcampus/
│   ├── ApplicationConfig.java
│   ├── model/
│   │   ├── Room.java
│   │   ├── Sensor.java
│   │   └── SensorReading.java
│   ├── resource/
│   │   ├── DiscoveryResource.java
│   │   ├── RoomResource.java
│   │   ├── SensorResource.java
│   │   └── SensorReadingResource.java
│   ├── exception/
│   │   ├── RoomNotEmptyException.java
│   │   ├── RoomNotEmptyExceptionMapper.java
│   │   ├── LinkedResourceNotFoundException.java
│   │   ├── LinkedResourceNotFoundExceptionMapper.java
│   │   ├── SensorUnavailableException.java
│   │   ├── SensorUnavailableExceptionMapper.java
│   │   └── GlobalExceptionMapper.java
│   ├── filter/
│   │   └── LoggingFilter.java
│   ├── store/
│   │   └── DataStore.java
│   └── WEB-INF/
│      └── web.xml
└── pom.xml
```

---

## How to Build and Run

### Prerequisites
- Java 11 or higher
- Apache Tomcat 9.x ([Download here](https://tomcat.apache.org/download-90.cgi))
- Maven 3.6 or higher
- NetBeans IDE (recommended)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/theseekerofficial/Smart-Campus-API.git
cd smart-campus-api
```

**2. Add Tomcat 9 to your IDE**

For NetBeans:
- Go to Services tab → Right click Servers → Add Server
- Select Apache Tomcat or TomEE
- Browse to your extracted Tomcat 9 folder
- Set a username and password and click Finish

**3. Open the project**

For NetBeans:
- File → Open Project → select the cloned folder

**4. Build the project**
```bash
mvn clean package
```
Or right click project → Clean and Build in your IDE

**5. Run the project**

Right click project → Run

Tomcat will start and deploy the application automatically.

**6. The API will be available at**
```
http://localhost:8080/api/v1
```

---

### Configure Base URL (Remove Context Path)

By default, when deploying the application on Apache Tomcat, the context path is derived from the WAR file name (e.g., `smart-campus-api`). This results in URLs like:

```
http://localhost:8080/smart-campus-api/api/v1
```

To remove the `/smart-campus-api` segment and expose the API at the root context:

#### Change Context Path in NetBeans

* Right click the project → **Properties**
* Navigate to **Run**
* Delete anything in **Context Path**

This will also make the API available at:

```
http://localhost:8080/api/v1
```


---

## API Endpoints

| Method   | Endpoint                            | Description             |
|----------|-------------------------------------|-------------------------|
| GET      | /api/v1                             | Discovery endpoint      |
| GET      | /api/v1/rooms                       | Get all rooms           |
| POST     | /api/v1/rooms                       | Create a room           |
| GET      | /api/v1/rooms/{roomId}              | Get room by ID          |
| DELETE   | /api/v1/rooms/{roomId}              | Delete a room           |
| GET      | /api/v1/sensors                     | Get all sensors         |
| GET      | /api/v1/sensors?type={type}         | Filter sensors by type  |
| POST     | /api/v1/sensors                     | Create a sensor         |
| GET      | /api/v1/sensors/{sensorId}          | Get sensor by ID        |
| DELETE   | /api/v1/sensors/{sensorId}          | Delete a sensor         |
| GET      | /api/v1/sensors/{sensorId}/readings | Get readings for sensor |
| POST     | /api/v1/sensors/{sensorId}/readings | Add reading for sensor  |

---

## Sample curl Commands

**1. Get API discovery info**
```bash
curl -X GET http://localhost:8080/api/v1
```

**2. Create a new room**
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"HALL-201","name":"Main Hall","capacity":200}'
```

**3. Create a new sensor linked to a room**
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-002","type":"Temperature","status":"ACTIVE","currentValue":0.0,"roomId":"HALL-201"}'
```

**4. Add a sensor reading**
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":24.5}'
```

**5. Filter sensors by type**
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

**6. Attempt to delete a room that has sensors (expect 409)**
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

**7. Attempt to post reading to MAINTENANCE sensor (expect 403)**
```bash
curl -X POST http://localhost:8080/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":10.0}'
```

---

## Report: Question Answers

### Part 1 — Service Architecture & Setup

**Q: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance created per request or is it a singleton?**

> The default set up of JAX-RS generates a new instance of a resource class with each incoming HTTP request. This is referred to as the per-request lifecycle. While this approach is thread-safe by default since each request gets its own object instance, it means that instance variables cannot be used to store shared state between requests.

> In this project, the shared state (rooms, sensors, readings) is controlled in the DataStore class, which is a singleton by a static final instance. This guarantees that instances of all classes of resources classes, including the number of instances created per request, will share the identical in-memory and completely eliminates race conditions when innumerable threads are reading and writing to a data structure at the same time,thread safeguards without blocking the synchronization.

> The API base path /api/v1 is configured via the servlet URL mapping in web.xml, with @ApplicationPath("/") set on the Application class. This achieves the same result as using @ApplicationPath("/api/v1") directly, as the URL pattern in web.xml handles the base path routing to Jersey.

**Q: Why is HATEOAS considered a hallmark of advanced RESTful design? How does it benefit client developers?**

> HATEOAS (Hypermedia As The Engine Of Application State) is that the responses of the API do not contain bare data, instead, they contain links to other related resources and the actions that can be taken. This will enable the clients to use the API dynamically without coding of the URLs. An example of this would be a response to a room which would have a link to its sensors and a link to remove it. This has a number of benefits to client developers: it minimizes the degree of bonding between client and server, is self-documenting, and the server can modify URL structure without breaking clients provided the link relations do not change. HATEOAS provides the clients with a live map of what they can do next, depending on the book of the resource, rather than on static documentation.

---

### Part 2 — Room Management

**Q: When returning a list of rooms, what are the implications of returning only IDs versus full room objects?**

> Retrieving only IDs is lightweight, low bandwidth which would be useful when the client only requires to show a hitlist or do a look up. But it causes the client to repeat requests to get information about the rooms and causes a related problem of N+1 where a list request results in dozens of follow-up requests. which full room objects doubles up the size of the payload but provides the client with all it requires in one request to lower latency and making the client-side logic easier. In this API, full objects are a more acceptable option as rooms are not complex POJOs with many fields, and the cost associated with it is pretty low in relation to the convenience provided.

**Q: Is the DELETE operation idempotent in your implementation? Justify.**

> Yes, the DELETE is idempotent here. Idempotency refers to the fact that repeated requests can return the same state of the server as a single request. When any client submits the request: DELETE /api/v1/rooms/HALL-201 and the room is present it gets deleted and a 200 OK response is sent. When delivered a second time, the room does not exist anymore and a 404 Not Found is sent. The state of the server is the same following the two calls the room is lost. Response codes are different with the initial and the second call, and the resource states remain the same after the initial deletion, meaning that it meets the definition of idempotency.

---

### Part 3 — Sensor Operations & Linking

**Q: What happens if a client sends data in a format other than JSON to a @Consumes(APPLICATION_JSON) endpoint?**

> When the client makes a request with either content-type of text/plain or application/xml to an endpoint marked with annotation of @Consumes(MediaType.APPLICATION_JSON), JAX-RS rejects the request before it can even be delivered to the resource method. By default, the runtime will reply with an Unsupported Media Type (HTTP 415). The reason behind this is that JAX-RS applies content negotiation it compares the content type header on the request received to the strategy of media types as stated in the media types in the annotation linking media type in the annotation. In case of no match, the request is rejected at the framework level and the resource method is spared of encountering malformed or unexpected input formats.

**Q: Why is @QueryParam preferred over a path segment for filtering?**

> Semantically more appropriate query parameters such as to filter by type of sensor such as: GET /api/v1/sensors are better represented with query parameters where the query parameters being optional and variable, instead of defining a resource identity. The existence of a path such as /api/v1/sensors/type/CO2 suggests that /type/CO2 is an addressable resource and this is misguided. Multi-criteria filtering (e.g., type=CO2 and status=ACTIVE) can also be easily combined using query parameters without altering the URL structure. Path segments are considered identifiers to the resource, and query parameters are considered as modifiers; therefore, the usage of filtering with the help of the @QueryParam is the correct way to act according to the principle of a RESTful design.

---

### Part 4 — Deep Nesting with Sub-Resources

**Q: Discuss the architectural benefits of the Sub-Resource Locator pattern.**

> Sub-Resource Locator pattern enables a class representing a resource to take on a sub-path by delegating it to a single specialized class. Here, SensorResource forwards `/sensors/{ sensorId}/readings to SensorReadingResource. There are a number of advantages to this practice. First, it facilitates **separation of concerns - all classes contain one task and the codebase can be easily read and supported. Second, it enhances things in regard to scalability - the API can be expanded and new sub-resources can be added without making the existing classes get fat. Third, it permits re-utilization, i.e. the sub-resource type can theoretically be re-utilized in many parent resources. The locator pattern avoids highly nested conditional logic and maintains smaller, testable classes and well aligned with the Single Responsibility Principle as compared to defining all nested paths within a single giant controller.

---

### Part 5 — Error Handling, Exception Mapping & Logging

**Q: Why is HTTP 422 more semantically accurate than 404 when a referenced resource is missing inside a payload?**

> A response with a 404 Not Found status means that the URL requested is not present in the server. A 422 Unprocessable Entity response means the server has comprehended the request and the URL is legitimate, but the content to the request body is semantically wrong. A client POSTing the new sensor via roomId that is not present results in the URL being absolutely valid ( /api/v1/sensors will work ) the issue being within the contents of the JSON itself. The 404 should not be used as it would be considered misleading since it is saying that the endpoint is not located. A 422 will convey the correct message that the request was correct and arrived at the appropriate endpoint, but could not be proven by business logic because a broke reference was found in the payload.

**Q: What are the security risks of exposing internal Java stack traces to API consumers?**

>Several reasons make exposing raw stack traces to external clients a big security risk. First, in stack traces, the attackers get a map of what codebase by showing them the internal package structure and names of the classes to which the application belongs. Second, they can reveal names and versions of libraries (e.g., Jersey 2.32, Tomcat 9), which attackers can use to attempt to find known CVEs in those specific versions. Third, stack trace could include file paths and directory structures to files on servers, which helps in targeted attacks. Fourth, traces may be leaked via error messages containing fragments of database queries, value of variables or business logic. The API accomplishes this by providing a global instance of ExceptionMapper<Throwable> which would give a generic 500 response but be logged server side to aid in debugging.

**Q: Why use JAX-RS filters for logging instead of manual Logger.info() calls in every method?**

> The benefits of using JAX-RS filters to add cross-cutting features, such as logging are better than manual in-version due to various reasons. First, it adheres to the principle of **DRY** - no logic is written more than once in a class instead of repeated in all resource methods. Second, filtrer is automatically and uniformly applied to each request and response by default and incurs no developer or implementation costs; nor does it risk leaving out logging to a new method. Third, it enhances the maintainability - in case the logging format must vary one only class has to be altered. Fourth, it implements the idea of separation of concerns - resource methods deal only with business logic whereas infrastructure issues such as logging, authentication, and CORS are addressed in the filter layer. It is identical to middleware middleware such as in Express.js or interceptors in Spring.