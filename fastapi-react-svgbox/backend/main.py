import logging
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from strawberry.fastapi import GraphQLRouter

# Local import
from db import init_db, stop_db
from core import schema

logger = logging.getLogger(__name__)

graphql_app = GraphQLRouter(schema)

app = FastAPI()

origins = [
    "http://localhost",
    "http://localhost:3000",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(graphql_app, prefix="/graphql")
app.add_websocket_route("/graphql", graphql_app)

# Add event handlers to connect and disconnect from the database
@app.on_event("startup")
async def startup():
    """Connect database event handler"""

    logger.info("Starting database session ... ")
    await init_db()
    logger.info("Database up and running ... ")


@app.on_event("shutdown")
async def shutdown():
    """Stop database event handler"""
    await stop_db()


@app.get("/")
async def start_page():
    """Initial router"""
    return {"check_health": "Ok"}
