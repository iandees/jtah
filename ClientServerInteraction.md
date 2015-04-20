# Introduction #

This document is meant to describe the design of the interaction between the clients and the server.

# Details #

  1. Client requests a job from server, giving a user token.
  1. Server checks to see if any requests exist.
    1. If there is a request, send it back to the client, mark it as "out", and note down the user that took the job.
      1. Client requests data for the tile from OSM
      1. For each zoom level from the one requested down to level 17:
        1. Client transcodes the OSM data to SVG using Osmarender for that zoom
        1. Client renders the SVG data to PNG
        1. Client splits the PNG image into 256x256 tiles
        1. Client uploads the PNG images
    1. If there is no request, send back a "no work now" message.