
# ncts-frontend

The _New Computerised Transit System Monitoring_ service provides the capability to monitor the platform microservices involved with the receipt and processing of goods transit notifications for goods departing from and arriving in the UK

The service can be seen in production at the following url https://www.tax.service.gov.uk/new-computerised-transit-system-service-availability

## Running the service locally

The service can be started on its default port with the command `sbt run`

To start all dependent services, please start the service manager profile using `sm --start NCTS_ALL -r`

## Tests

Unit and integration tests can be run using the command `sbt test it:test`

There is also a suite of journey tests in the ncts-journey-tests repository
