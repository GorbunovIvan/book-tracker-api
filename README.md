<h2>Book Tracker API</h2>

<p>"Book Tracker API" is a simple Spring Boot RESTful pet project to manage a personal book collection. 
Users can add books, update details, categorize them</p>

<p>Stack:</p>
<ul>
    <li>Backend: Spring Boot</li>
    <li>Database: PostgreSQL (H2 for tests). Persisting configured via Spring Data JPA</li>
    <li>Database Migrations: Flyway</li>
    <li>Build Tool: Maven</li>
    <li>Testing: JUnit and Mockito</li>
    <li>Documentation: Swagger (OpenAPI) for API documentation.</li>
    <li>Authentication: Spring Security (with JWT)</li>
    <li>Deployment: Jenkins via docker-compose</li>
</ul>

<p>All GET endpoints are available, to access other types of http methods (except "/auth/*" endpoints), you need to get a token.
To obtain it you should use "/auth/login" POST endpoint providing credentials.</p> 
<p>Default credentials for obtaining Json Web Token:</p>
<ul>
    <li>username: admin</li>
    <li>password: admin</li>
</ul>
<p>Then you can add token to headers like 'Authorization: {{token}}' to access secured endpoints.</p>