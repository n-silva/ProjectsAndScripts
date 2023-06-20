from databases import Database
from sqlmodel import create_engine, Session, SQLModel

# Define the database connection
database = Database("sqlite+pysqlite:///:memory:")
# Create the database tables
engine = create_engine(
    str(database.url), echo=True, connect_args={"check_same_thread": False}
)


async def init_db():
    """Start database connection"""
    await database.connect()
    SQLModel.metadata.create_all(engine)


async def get_session():
    """Get database session"""
    with Session(engine) as session:
        yield session


async def stop_db():
    """End database connection"""
    await database.disconnect()
