#!/bin/bash -eux
#
# Backfill script for Cassius. Assumes the CASSIUS_HOST env var is set.
#
# activities.json should be a list of activities returned by the
# activities list API. This script parses out their ids and POSTS them
# to Cassius.
#
# This script is necessary because the schema returned by the Strava
# API for an activity is slightly different between the list endpoint and the
# activity endpoint. If you backfill listed activities directly, there will be a schema
# conflict and BigQuery won't be able to parse it.
#
# Requires jq and httPie

activity_ids=$(cat activities.json | jq '.id')
for id in $activity_ids
do
    http "$CASSIUS_HOST/strava/events" object_type=activity \
      aspect_type=create \
      event_time=1549560669 \
      object_id=$id \
      owner_id=9999999 \
      subscription_id=999999
done
