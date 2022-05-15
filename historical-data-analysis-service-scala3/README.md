# Historical data analysis service

This service provides a single REST endpoint to retrieve aggregated statistics for different sports.
The underlying aggregation service can be swapped. For the sake of simplicity for the initial version,
we have provided an in-memory implementation. But such implementation can be changed to a call to any external service
performing the computation.

## Usage

Start services with sbt:

```
$ sbt
> ~reStart
```

With the service up, you can start sending HTTP requests:

```
$ curl http://localhost:9000/result
{
  "mostWin": {
    "team": "Thailand",
    "amount": 21
  },
  "mostScoredPerGame": {
    "team": "Australia",
    "amount": 31
  },
  "lessReceivedPerGame": {
    "team": "Albania",
    "amount": 0
  }
}
```

### Testing

Execute tests using `test` command:

```
$ sbt
> test
```