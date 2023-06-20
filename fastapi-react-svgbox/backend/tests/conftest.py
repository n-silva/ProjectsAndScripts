import pytest
from fastapi.testclient import TestClient
from sqlmodel import Session, SQLModel
from main import app
from db import engine


@pytest.fixture(scope="session")
def session():
    """Start a db session"""
    db_session = Session(bind=engine)
    yield db_session
    db_session.rollback()
    db_session.close()


@pytest.fixture(autouse=True)
def setup_db():
    """Create the tables"""
    SQLModel.metadata.create_all(engine)


@pytest.fixture(autouse=True, scope="module")
def client():
    """Use Fastapi Testclient"""
    return TestClient(app)
