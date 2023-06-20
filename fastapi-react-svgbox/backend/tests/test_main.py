from typing import Dict
import pytest
from httpx import AsyncClient

from main import app
from core import schema


@pytest.mark.usefixtures
class TestGraphlRouter:
    """
    Test class for CRUD
    """

    @staticmethod
    def get_query() -> Dict:
        """
        Get data static method
        """
        query = """
            query TestQuery {
                boxShapes {
                    id
                    color
                    sizeX
                    sizeY
                }
            }
        """

        result = schema.execute_sync(
            query,
        )

        return result

    def test_query(self):
        """
        Test query endpoint
        """
        result = self.get_query()
        assert result.errors is None
        assert result.data["boxShapes"] == []

    def test_crud(self):
        """
        This method perform a CRUD test to the endpoints
        """
        box1: str = """{
            id: \"box-1\",
            posX: 1.5,
            posY: 1.5,
            sizeX: 1.5,
            sizeY: 1.5,
            color: \"\",
        }
        """

        box2: str = """{
            id: \"box-2\",
            posX: 1.5,
            posY: 1.5,
            sizeX: 1.5,
            sizeY: 1.5,
            color: \"blue\",
        }
        """

        box3: str = """{
            id: \"box-3\",
            posX: 1.5,
            posY: 1.5,
            sizeX: 1.5,
            sizeY: 1.5,
            color: \"green\",
        }
        """

        # pylint: disable=consider-using-f-string
        mutation = """
            mutation MyMutation {
                addBoxShape(
                    boxShapeData: %s
                ) {
                    id
                    color
                    posX
                    posY
                    sizeX
                    sizeY
                }
                }
        """ % (
            box1
        )

        # Test database before mutation
        response = self.get_query()
        # Validate db session
        assert response.errors is None

        # Should pass, there is no data yet
        assert len(response.data["boxShapes"]) == 0

        ############################################
        # TEST ADD BOX API
        ############################################
        # Add new box
        response = schema.execute_sync(
            mutation,
        )

        assert response.errors is None
        # Verifying the the data inserted
        assert response.data["addBoxShape"] == {
            "id": "box-1",
            "color": "",
            "posX": 1.5,
            "posY": 1.5,
            "sizeX": 1.5,
            "sizeY": 1.5,
        }

        ############################################
        # TEST UPDATE BOX IF EXIST API
        ############################################
        update_mutation = """
            mutation MyMutation {
                addBoxShape(
                    boxShapeData: {id: "box-1", posX: 1.5, posY: 1.5, sizeX: 1.5, sizeY: 1.5, color: "red"}
                ) {
                    id
                    color
                    posX
                    posY
                    sizeX
                    sizeY
                }
            }
        """

        # Update box color
        response = schema.execute_sync(
            update_mutation,
        )

        assert response.errors is None
        # Verifying the data inserted
        assert len(response.data) == 1

        # Check updated field
        assert response.data["addBoxShape"].get("color") == "red"

        ############################################
        # ADDING MORE BOX
        ############################################
        # Add new box
        mutation2 = """
            mutation MyMutation2 {
                addBoxShape(
                    boxShapeData: %s
                ) {
                    id
                    color
                    posX
                    posY
                    sizeX
                    sizeY
                },
            }
        """ % (
            box2,
        )

        mutation3 = """
            mutation MyMutation3 {
                addBoxShape(
                    boxShapeData: %s
                ) {
                    id
                    color
                    posX
                    posY
                    sizeX
                    sizeY
                },
            }
        """ % (
            box3,
        )

        # Add new box
        response = schema.execute_sync(
            mutation2,
        )
        assert response.errors is None

        # Add new box
        response = schema.execute_sync(
            mutation3,
        )
        assert response.errors is None

        # Test total records
        response = self.get_query()

        # Should pass, because there 2 box added
        assert len(response.data["boxShapes"]) == 3

        ############################################
        # TEST DELETE ONE BOX API
        ############################################
        delete_one_mutation = """
            mutation MyMutation {
                delBoxShape(boxShapeId: "box-1")
            }
        """
        # Delete box-1
        response = schema.execute_sync(
            delete_one_mutation,
        )
        assert response.errors is None
        # Test total records
        response = self.get_query()
        assert len(response.data["boxShapes"]) == 2

        ############################################
        # TEST DELETE ALL RECORDS API
        ############################################
        delete_all_mutation = """
            mutation MyMutation {
                delAllBoxShape
            }
        """
        # Delete box-1
        response = schema.execute_sync(
            delete_all_mutation,
        )
        assert response.errors is None
        # Test total records
        response = self.get_query()
        assert len(response.data["boxShapes"]) == 0


@pytest.mark.asyncio
async def test_root_api():
    """
    Simple test to validate async call to the root endpoint
    """
    async with AsyncClient(app=app, base_url="http://localhost:8000") as async_root:
        response = await async_root.get("/")
    assert response.status_code == 200
    assert response.json() == {"check_health": "Ok"}


@pytest.mark.asyncio
async def test_graphql():
    """
    Simple test to validate async call to the graphql endpoint
    """
    async with AsyncClient(app=app, base_url="http://localhost:8000") as async_graphql:
        response = await async_graphql.get("/graphql")
    assert response.status_code == 200
