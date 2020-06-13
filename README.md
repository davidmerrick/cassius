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

# Usage

Create a Strava app. You'll need a token with the `activity:read_all` scope.

Have your app subscribe to Strava's webhooks to get notified when a new activity
is created.

# Reference

- [Strava API docs](https://developers.strava.com/docs/getting-started/)
- [Subscribing to webhooks](https://developers.strava.com/docs/webhooks/)