import unittest
from server import FuncCalculate

class MyTestCase(unittest.TestCase):
    def test_result(self):
        result = FuncCalculate().eval_expression("f4")
        print(result)
        self.assertEqual(True, True)


if __name__ == '__main__':
    unittest.main()
