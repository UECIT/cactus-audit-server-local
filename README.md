# cactus-audit-server-local
A local server for receiving, logging and querying audits.

This service should be run in conjunction with the other Cactus services on
a local environment only. It can accept incoming SQS-like sendMessage requests and will also respond to specific by-case Elasticsearch POST search queries. 

## Data persistence
By default, this service uses an in-memory H2 database as a backing store for the received SQS message, so any data saved will not persist across restarts. To persist (or easily inspect) the stored data you can change Spring's database connection string to use a file-based connection as follows:

```properties
spring.datasource.url=jdbc:h2:file:~/audits
```