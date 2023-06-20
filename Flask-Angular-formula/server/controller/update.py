from ..model import Functions
from .. import db


def update_func(**Kwargs):
    funct = Functions.query.filter(Functions.id == Kwargs.get("id")).first()
    funct.name= str(Kwargs.get("name")).lower()
    funct.description=Kwargs.get("description")
    funct.function=Kwargs.get("function")

    db.session.merge(funct)
    db.session.flush()
    db.session.commit()
    return funct.json()
