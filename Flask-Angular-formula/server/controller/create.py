from ..model import Functions


def create_func(**Kwargs):
    funct = Functions(name=str(Kwargs.get("name")).lower(), description=Kwargs.get("description"), function=Kwargs.get("function"))
    funct.save()
    funct.commit()
    return funct.json()