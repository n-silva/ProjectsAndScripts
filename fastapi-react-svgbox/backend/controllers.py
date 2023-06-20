import asyncio
from typing import List, AsyncGenerator
from sqlmodel import select

from config.settings import OnErrorException
from db import engine, Session
from models import BoxShapes
from schemas import BoxShapeInput, BoxShapeType


class Queries:
    """
    Generate Queries resolvers
    """

    def get_all_box_shape(self) -> List[BoxShapeType]:
        """
        Return all the records
        """
        with Session(engine) as session:
            box_shapes = session.exec(select(BoxShapes)).all()
            return box_shapes

    def get_box_shape(self, box_shape_id: str) -> BoxShapeType:
        """
        Query a single record

        Arguments:
            box_shape_id -- Box Id to query for
        """
        box_shape = None
        with Session(engine) as session:
            box_shape = session.get(BoxShapes, box_shape_id)
        if not box_shape:
            raise OnErrorException(f"Box shape with id: {box_shape_id} not found")
        return box_shape


class CreateMutation:
    """
    Generate Mutation resolvers for create/update and delete data
    """

    def add_box_shape(self, box_shape_data: BoxShapeInput) -> BoxShapeType:
        """
        Add new record to database
        """
        with Session(engine) as session:
            box_shape = session.get(BoxShapes, box_shape_data.id)

            if box_shape:
                box_shape.update(
                    box_shape_data.pos_x,
                    box_shape_data.pos_y,
                    box_shape_data.size_x,
                    box_shape_data.size_y,
                    box_shape_data.color,
                )
            else:
                box_shape = BoxShapes()
                box_shape.id = box_shape_data.id
                box_shape.pos_x = box_shape_data.pos_x
                box_shape.pos_y = box_shape_data.pos_y
                box_shape.size_x = box_shape_data.size_x
                box_shape.size_y = box_shape_data.size_y
                box_shape.color = box_shape_data.color

                session.add(box_shape)
            session.commit()
            session.refresh(box_shape)
            return box_shape

    def del_box_shape(self, box_shape_id: str) -> bool:
        """
        Delete a single record from database
        """
        with Session(engine) as session:
            try:
                box_shape = Queries().get_box_shape(box_shape_id)
                session.delete(box_shape)
                session.commit()
            except OnErrorException:
                return False
            return True

    def del_all_box_shape(self) -> bool:
        """
        Delete all records from database
        """
        with Session(engine) as session:
            delete_q = BoxShapes.__table__.delete()
            session.execute(delete_q)
            session.commit()
            return True


class Subscriptions:
    """Generate a websocket service"""

    async def total_count(
        self,
    ) -> AsyncGenerator[int, None]:
        """Keep updated with the total records"""
        total = len(Queries().get_all_box_shape())
        yield total
        await asyncio.sleep(0.5)
