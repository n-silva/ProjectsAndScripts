import re

pattern = re.compile(r"[\w-]+")


def validFormat(text):
    if pattern.fullmatch(text):
        return True
    else:
        return False
