import sys
import os
from os import path
from io import open
from setuptools import setup

from server import __name__, __version__, __doc__

# Get the long description from the README file
here = path.abspath(path.dirname(__file__))
with open(path.join(here, 'README.md'), encoding='utf-8') as f:
    long_description = f.read()


# this command line option is used when we want to create a deployment package
if "--create_deployment_package" in sys.argv:
    sys.argv.remove("--create_deployment_package")

    # getting the full list of files in the "vendors" folder
    directory = path.abspath(path.join(path.dirname(path.dirname(__file__)), "vendors"))

    # create a recursive list of paths to files in a directory
    extra_files = []
    for (path, directories, filenames) in os.walk(directory):
        for filename in filenames:
            extra_files.append(os.path.join('..', path, filename))

    # removing files with .pyc, .pyo. and __pycache__ in their path
    extra_files = [s for s in extra_files if ".pyc" not in s and ".pyo" not in s and "__pycache__" not in s]

    # creating a parameter to send to setup function
    package_data = {'': extra_files}
else:
    package_data = {'': []}


setup(
    name=__name__,
    version=__version__,
    description=__doc__,
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/n-silva",
    author="Nelido Soares",
    author_email="soaresnelido@gmail.com",
    packages=["server"],
    python_requires=">=3.7",
    install_requires=["flask==1.1.1"],
    package_data=package_data
)
