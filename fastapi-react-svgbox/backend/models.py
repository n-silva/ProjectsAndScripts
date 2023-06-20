from typing import Optional
from sqlmodel import SQLModel, Field


class BoxShapes(SQLModel, table=True):
    """BoxShape table"""

    id: Optional[str] = Field(primary_key=True, nullable=False, unique=True)
    pos_x: float
    pos_y: float
    size_x: float
    size_y: float
    color: str

    def update(self, pos_x, pos_y, size_x, size_y, color):
        """Update table method"""
        self.pos_x = pos_x
        self.pos_y = pos_y
        self.size_x = size_x
        self.size_y = size_y
        self.color = color
