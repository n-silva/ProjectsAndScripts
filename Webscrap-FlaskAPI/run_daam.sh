#!/bin/sh
export FLASK_APP=daam.py
export FLASK_ENV=development
#source $(pipenv --venv)/bin/activate
flask run -h 127.0.0.1 -p 5022