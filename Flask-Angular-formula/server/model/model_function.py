from . import db


class Functions(db.Model):
    id = db.Column(db.Integer(), primary_key=True, autoincrement=True)
    name = db.Column(db.String(255), unique=True)
    description = db.Column(db.String(255))
    function = db.Column(db.Text)

    def __init__(self, name, description, function):
        self.name = name
        self.description = description
        self.function = function

    def __str__(self):
        return self.name

    def __repr__(self):
        return "{}: {}".format(self.id, self.__str__())

    def save(self):
        db.session.add(self)

    @classmethod
    def commit(cls):
        db.session.commit()

    def json(self):
        return {
            "id": self.id,
            "name": self.name,
            "description": self.description,
            "function": self.function
        }

    def get_id(self):
        return self.id