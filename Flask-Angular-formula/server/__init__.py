import os

from flask import Flask, url_for, request, render_template, abort, Response, Blueprint
from flask_cors import CORS, cross_origin
from flask_sqlalchemy import SQLAlchemy
from .utils import validFormat

import logging

__version_info__ = ('1', '0', '0')
__version__ = '.'.join(__version_info__)

logger = logging.getLogger(__name__)

app = Flask(__name__)

app_settings = os.getenv(
    'APP_SETTINGS',
    'server.config.DevelopmentConfig'
)

app.config.from_object(app_settings)

#set CORS
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'


db = SQLAlchemy(app)

from server.model import Functions
from server.controller import FuncCalculate
from server.view.index import index_blueprint
from server.view.admin import admin_blueprint

app.register_blueprint(index_blueprint)
app.register_blueprint(admin_blueprint)



@index_blueprint.after_request
@admin_blueprint.after_request
def after_request(response):
    header = response.headers
    header['Access-Control-Allow-Origin'] = '*'
    return response
