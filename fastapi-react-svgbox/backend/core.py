from typing import List, AsyncGenerator
import strawberry
from strawberry import Schema
from controllers import CreateMutation, Queries, Subscriptions
from schemas import BoxShapeType


@strawberry.type
class Query:
    """
    Create the query services
    """

    box_shapes: List[BoxShapeType] = strawberry.field(
        resolver=Queries.get_all_box_shape
    )
    box_shape: BoxShapeType = strawberry.field(resolver=Queries.get_box_shape)


@strawberry.type
class Mutation:
    """
    Create mutations servcives
    """

    add_box_shape: BoxShapeType = strawberry.mutation(
        resolver=CreateMutation.add_box_shape
    )

    del_box_shape: bool = strawberry.mutation(resolver=CreateMutation.del_box_shape)

    del_all_box_shape: bool = strawberry.mutation(
        resolver=CreateMutation.del_all_box_shape
    )


@strawberry.type
class Subscription:
    """
    Create subscription services
    """

    totalCount: AsyncGenerator[int, None] = strawberry.subscription(
        resolver=Subscriptions.total_count
    )


schema = Schema(query=Query, mutation=Mutation, subscription=Subscription)
