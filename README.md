# nukr

The future is purple - and connected!
Join us through **nukr**!

# features

Nukr is a web application that provides a RESTful, resource-oriented API.
Use the following endpoints to interact with nukr.

### POST /profiles/create
Create new profile.
The following fields are expected in the payload:

- id
  - `integer`
  - Unique identification number for the profile.

- name
  - `string`
  - Name of the person represented by the profile.

Example:
```
{
 "id": 1
 "name": "Edward Wible"
}
```

### POST /connections/create
Create a new connection between 2 profiles.
The following fields are expected in the payload:

- orig_id
  - `integer`
  - Unique identification number for the first profile.
- dest_id
  - `integer`
  - Unique identification number for the second profile.

Example:
```
{
 "orig_d": 1
 "dest_id": 2
}
```

## Usage

FIXME
