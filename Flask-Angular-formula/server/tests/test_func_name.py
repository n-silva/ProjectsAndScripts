import unittest

from server import validFormat

class MyTestCase(unittest.TestCase):
    def test_name_success(self):
        text = "para_3023"
        self.assertEqual(validFormat(text), True)

    def test_name_fail(self):
        text = "para-*3023"
        self.assertEqual(validFormat(text), False)

if __name__ == '__main__':
    unittest.main()
