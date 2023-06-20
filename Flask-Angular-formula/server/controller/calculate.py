import math
from . import app
from .search import filter_by_name

allowed_operators = app.config.get("OPERATORS")


class FuncCalculate:
    full_string = ''

    def eval_expression(self, input_string):
        if self.full_string == '': self.full_string = str(input_string).lower()
        code = compile(self.full_string, "<string>", "eval")
        for name in code.co_names:
            get_functions = filter_by_name(name)
            if not get_functions:
                if name not in allowed_operators:
                    return { "result":f"Use of {name} not allowed", "formula": input_string }
            else:
                self.full_string = self.full_string.replace(name, "(" + get_functions["function"] + ")")
                self.eval_expression(self.full_string)
        return { "result":eval(self.full_string), "formula": self.full_string }
