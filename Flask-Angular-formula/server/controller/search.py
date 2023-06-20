from ..model import Functions


def filter_by_name(func_name):
    return query(Functions.name, func_name)


def filter_by_id(func_id):
    return query(Functions.id, func_id)


def filter_all():
    return query()


def query(field=None, value=None):
    if not field and not value:
        return [func.json() for func in Functions.query.all()]
    else:
        func = Functions.query.filter(field == value).one_or_none()
        return func.json() if func else None
