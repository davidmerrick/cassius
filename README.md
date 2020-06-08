# Strava oAuth flow

## 1. Get auth code

Need `activity:read_all` scope.

https://www.strava.com/oauth/authorize?client_id=[yourClientId$]&response_type=code&redirect_uri=http://localhost&approval_prompt=force&scope=activity:read_all

## 2. Exchange auth code for token


