![Muhhamad Ali](docs/img/muhammad_ali.jpg)

A tribute to Muhammad Ali. The Greatest and one of my lifelong heroes.

This project in no way intends to be the greatest. 
But it will humbly get your Strava activities into BigQuery 
so you can have better dashboards for them.

# The problem

Strava is awesome. I use it to track all my workouts. But frequently I 
find that its dashboards aren't very flexible.

Cassius is a data pipeline that fetches new activities from Strava
and stores them in Google Cloud Storage so they can be analyzed 
with BigQuery and Data Studio.

![Data flow](docs/img/dataflow.png)

# Usage

Create a Strava app. You'll need a token with the `activity:read_all` scope.

Have your app subscribe to Strava's webhooks to get notified when a new activity
is created.

## Importing your data into BigQuery

Import files from your bucket as newline-delimited JSON. 
Name the table "activities".
Copy the included [schema.json](docs/schema.json) for the schema.
Under `Advanced`, select "Ignore unknown fields".

![GCP Import](docs/img/gcp_import.png)

# Auth

There's an activities backfill endpoint that I've secured with [auth0](https://auth0.com/).
To add your own auth, configure the `security` section of `application.yml`.

# Reference

- [Strava API docs](https://developers.strava.com/docs/getting-started/)
- [Subscribing to webhooks](https://developers.strava.com/docs/webhooks/)
- [Micronaut API with JWT authentication](https://www.ivarprudnikov.com/micronaut-kotlin-jwt-secured-api-aws-lambda/)