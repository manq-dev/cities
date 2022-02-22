# Cities - The Codest Task

### Building and running

* In order to build and run backend you need Java 17 http://jdk.java.net/17
* To run project run `./mvnw spring-boot:run`
* Login:Password for Basic Auth `admin:admin`
* Backend will start at `http:localhost:8080/cities`

* To run frontend you need Node.js https://nodejs.org/en/download
* After installing run `npm install -g @angular/cli` and after that `npm install`
* To run app run `ng serve --open`
* Authentication for update in Angular is hardcoded
* Frontend will start at `http:localhost:4200`

### Backlog

#### Backend
- DONE browse through the paginated list GET /cities?page={page}&size={size}
- DONE search by name GET /cities?name={name}
- DONE edit the city PATCH /cities/{id}
- DONE init list of cities from cities.csv file 
- DONE Spring Boot
- DONE Maven
- DONE H2 Database 
- DONE Spring Security ROLE_ALLOW_EDIT 
- DONE GitHub
- DONE validation
- DONE validation tests
- DONE run it with little-to-zero effort

#### Frontend
- DONE Angular 
- DONE browse through the paginated list of cities with the corresponding photos 
- DONE search by name
- DONE edit the city
- DONE run it with little-to-zero effort
- authentication
- validation
- Docker?

#### Review
- DONE Running instruction update
- Check roles in frontend
- DONE remove @EqualsAndHashCode from @Entity
- DONE remove if in controller
- Components segregation in FE (CityList, City, Search)
- Get rid of PageDeserializer