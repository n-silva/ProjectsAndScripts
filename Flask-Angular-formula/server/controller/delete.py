from ..model import Functions
from .. import  db
def delete_func(id):
    deleted= Functions.query.filter(Functions.id == id).delete()
    db.session.commit()
    return deleted