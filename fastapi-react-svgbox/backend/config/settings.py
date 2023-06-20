class Base:
    """Start config base class"""

    DEBUG = False
    TESTING = False


class DevelopmentConfig(Base):
    """Development config class"""

    DEBUG = True
    DEVELOPMENT = True
    DATABASE_URI = "sqlite:///:memory:"


class TestingConfig(Base):
    """Test config class"""

    DEBUG = False
    TESTING = True
    DATABASE_URI = "sqlite:///:memory: as TEST"


class ProductionConfig(Base):
    """Production config class"""

    DEBUG = False
    TESTING = False
    DATABASE_URI = "sqlite:///:memory:"


class OnErrorException(Exception):
    """General Exception"""
