from typing import Optional
import strawberry


@strawberry.type
class SubscribeType:
    """
    Define subscribe type
    """

    totalcount: Optional[int] = None


@strawberry.type
class BoxShapeType:
    """
    Define BoxShape class type
    """

    id: str
    pos_x: float
    pos_y: float
    size_x: float
    size_y: float
    color: str


@strawberry.input
class BoxShapeInput:
    """
    Define BoxShape class input
    """

    id: str
    pos_x: float
    pos_y: float
    size_x: float
    size_y: float
    color: str
