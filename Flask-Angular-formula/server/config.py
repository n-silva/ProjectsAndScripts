import os
import logging

basedir = os.path.abspath(os.path.dirname(__file__))

logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)

PORT = os.getenv('PORT', '5000')
DEBUG = os.getenv('DEBUG', True)

#server/config.py
postgres_local_base = 'postgresql://postgres:postgres@db:5432/'
database_name = 'formula'
DATABASE_CONFIG_DICT = {
    'user': 'postgres',
    'password': 'postgres',
    'hostname': 'db',
    'port': 5432,
    'database': database_name,
    'sslmode': 'disable'
}

class BaseConfig:
    """Base configuration."""
    SECRET_KEY = 'rzf7Oxn9oIQcPARCWNq1q3wLMEAIOc0Bs'
    DEBUG = False
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    #Allowed math function operator
    OPERATORS = ["sum", "sqrt", "pow", "math"]


class DevelopmentConfig(BaseConfig):
    """Development configuration."""
    SECRET_KEY = 'rzf7Oxn9oIQcPARCWNq1q3wLMEAIOc0Bs'
    DEBUG = True
    SQLALCHEMY_DATABASE_URI = postgres_local_base + database_name + '_dev'


class TestingConfig(BaseConfig):
    """Testing configuration."""
    SECRET_KEY = 'rzf7Oxn9oIQcPARCWNq1q3wLMEAIOc0Bs'
    DEBUG = True
    TESTING = True
    SQLALCHEMY_DATABASE_URI = postgres_local_base + database_name + '_test'
    PRESERVE_CONTEXT_ON_EXCEPTION = False


class ProductionConfig(BaseConfig):
    """Production configuration."""
    SECRET_KEY = 'rzf7Oxn9oIQcPARCWNq1q3wLMEAIOc0Bs'
    DEBUG = False
    SQLALCHEMY_DATABASE_URI =postgres_local_base + database_name
