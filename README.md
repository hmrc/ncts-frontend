
# ncts-frontend

The _New Computerised Transit System Monitoring_ service provides the capability to monitor the platform microservices involved with the receipt and processing of goods transit notifications for goods departing from and arriving in the UK

The service can be seen in production at the following url https://www.tax.service.gov.uk/new-computerised-transit-system-service-availability

## Updating planned downtime

NCTS will occasionally require config changes to alert users to upcoming planned outages that the service will experience.

To update the planned downtime, open `planned-downtime.conf` in this repository and read ALL instructions on usage and operation before adding the planned downtime.


## Running dependencies

Using [sm2](https://github.com/hmrc/sm2)
with the service manager profile `NCTS_ALL` will start
all the Intellectual Property microservices as well as the services
that they depend on.

```
sm2 --start NCTS_ALL
```

To stop the microservice from running on service manager (e.g. to run your own version locally), you can run:

```
sm2 -stop NCTS_FRONTEND
```


### Using localhost

To run this microservice locally on the configured port **'9515'**, you can run:

```
sbt run 
```

**NOTE:** Ensure that you are not running the microservice via service manager before starting your service locally (vice versa) or the service will fail to start


## Tests

Unit and integration tests can be run using the command `sbt test it/test`


## Scalafmt and Scalastyle

To check if all the scala files in the project are formatted correctly:
> `sbt scalafmtCheckAll`

To format all the scala files in the project correctly:
> `sbt scalafmtAll`

To check if there are any scalastyle errors, warnings or infos:
> `sbt scalastyle`
