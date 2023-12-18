#!/bin/bash
sudo docker run -v $(pwd)/cache:/cache -v $(pwd)/server_settings:/server_settings -itd f1bot
