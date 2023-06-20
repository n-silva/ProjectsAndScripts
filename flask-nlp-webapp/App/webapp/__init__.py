import os
from flask import Flask
from flask_sqlalchemy import SQLAlchemy

# Create an Instance of Flask
app = Flask(__name__)

# Include config from config.py
app.config.from_object('config')
# generate a randam key
app.secret_key = os.urandom(12)
# Create an instance of SQLAclhemy
db = SQLAlchemy(app)

from webapp import views, models