
# ncts-frontend

The _New Computerised Transit System Monitoring_ service provides the capability to monitor the platform microservices involved with the receipt and processing of goods transit notifications for goods departing from and arriving in the UK

The service can be seen in production at the following url https://www.tax.service.gov.uk/new-computerised-transit-system-service-availability

## Updating planned downtime

NCTS will occasionally require config changes to alert users to upcoming planned outages that the service will experience.

To update the planned downtime, open `planned-downtime.conf` in this repository and read ALL instructions on usage and operation before adding the planned downtime.


## Running the service locally

The service can be started on its default port with the command `sbt run`

To start all dependent services, please start the service manager profile using `sm2 --start NCTS_ALL`

## Tests

Unit and integration tests can be run using the command `sbt test it/test`

There is also a suite of journey tests in the [ncts-journey-tests](https://github.com/hmrc/ncts-journey-tests/) repository
