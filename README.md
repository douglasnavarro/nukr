# nukr

The future is purple - and connected!
Join us through **nukr**!

## running locally
To run the application locally download dependencies and run using [leiningen](https://leiningen.org/):
```
$ lein deps
$ lein run 3000
```

then use your preferred browser to access **localhost:3000**. You may run with `port` 3000 or any other if necessary.

## run the tests
`$ lein test` to run all tests with standard  leiningen test runner

`$ lein test nukr.profile-logic-test` to run pure functions unit tests

`$ lein test nukr.profile-state-test` to run impure functions unit tests

`$ lein test nukr.integration-test` to run integration tests (these may take a bit longer as the server is actually started)

The `eftest` plugin is already in `project.clj` so if you want prettier output during further development, run `lein eftest` instead.

## style checker
The [cljfmt](https://github.com/weavejester/cljfmt) is used to style check this project and is already included in the project plugins.

Run `$ lein cljfmt fix` to check and apply fixes on all files or `$ lein cljfmt fix $FILE` to run only on file specified by `$FILE`.

Setting your editor to run this on file save might be a good idea.

# endpoints
These are the current endpoints to interact with Nukr, implemented with the minimum code necessary to fulfill initial requirements along with error handling, logging, reasonable test coverage and MVC-like structure.

### POST /profiles
Create new profile.
The following fields are expected:

- name
    - `string`.
    - Name of the person represented by the profile.

- hidden
   - `boolean`.
   - Represents whether profile is or is not shown in suggestions.

Example:
```
$ curl -X POST --data "name=Cris&hidden=false" localhost:3000/profiles -v
$ curl -X POST --data "name=Ed&hidden=false" localhost:3000/profiles -v

> POST /profiles HTTP/1.1
> Host: localhost:3000
> User-Agent: curl/7.58.0
> Accept: */*
> Content-Length: 19
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 19 out of 19 bytes
< HTTP/1.1 302 Found
< Date: Tue, 07 May 2019 02:00:22 GMT
< Location: /profiles/Ed
< Content-Type: application/json; charset=utf8
< Connection: close
< Server: Jetty(9.2.21.v20170120)
<
* Closing connection 1
```

If profile with a certain name already exists, a `409 Conflict` response is produced and the application state remains the same:
```
$ curl -X POST --data "name=Ed&hidden=false" localhost:3000/profiles -v

> POST /profiles HTTP/1.1
> Host: localhost:3000
> User-Agent: curl/7.58.0
> Accept: */*
> Content-Length: 19
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 19 out of 19 bytes
< HTTP/1.1 409 Conflict
< Date: Tue, 07 May 2019 02:01:01 GMT
< Connection: close
< Server: Jetty(9.2.21.v20170120)
<
* Closing connection 1
```

### GET /profiles
Returns list of profiles currently stored.
```
$ curl -X GET localhost:3000/profiles

[{"name":"Cris","connections":[],"hidden":false},
 {"name":"Ed","connections":[],"hidden":false}]
```

### GET /profiles/:name
Returns profile identified by `:name`.
```
$ curl -X GET localhost:3000/profiles/Cris

{"name":"Cris","connections":[],"hidden":false}
```

### GET /profiles/:name/suggestions
Returns suggested connections to be made with profile identified by `:name`.

Each suggestion is modeled by an entity with two attributes:
 - target: Name identifier for the profile with which the connection should be made.
 - relevance: Integer number that represents the strength of the suggestion i.e how many connections the two profiles have in common.

Every other non-hidden profile that the profile identified by `:name` is not connected with is considered a suggestion, even if they have no connections in common.
```
$ curl -X GET localhost:3000/profiles/Cris/suggestions

[{"relevance":0,"target":"Ed"}]
```

### PUT profiles/:name/connections
Create a new connection between 2 profiles, updating both profiles' `connections` field.

The following fields are expected:

- target
  - `string`
  - Profile identifier to be connected with profile identified by `:name`.

Example:
```
$ curl -X PUT --data "target=Cris" localhost:3000/profiles/Ed/connections -v
> PUT /profiles/Ed/connections HTTP/1.1
> Host: localhost:3000
> User-Agent: curl/7.58.0
> Accept: */*
> Content-Length: 11
> Content-Type: application/x-www-form-urlencoded
>
* upload completely sent off: 11 out of 11 bytes
< HTTP/1.1 200 OK
< Date: Tue, 07 May 2019 02:04:36 GMT
< Content-Type: text/plain
< Content-Length: 32
< Server: Jetty(9.2.21.v20170120)
<

$ curl -X GET localhost:3000/profiles

[{"name":"Cris","connections":["Ed"],"hidden":false},
 {"name":"Ed","connections":["Cris"],"hidden":false}]

```

### GET /
HTML view. Returns HTML for list of profiles, their connections and suggestions, as well as forms/scripts for POST and PUT requests to other endpoints.

## Building the jar
The project is already configured for building the jar file, so if you want to take it to production by any means:
```
$ lein uberjar
$ java -jar target/nukr-0.1.0-SNAPSHOT-standalone.jar
```
This assumes your machine or containerized environment has Java 8 installed.
