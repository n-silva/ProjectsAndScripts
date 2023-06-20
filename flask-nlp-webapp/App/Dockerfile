# Dockerfile
FROM python:3.6

COPY . /App

COPY requirements.txt /App

WORKDIR /App

# Install dependencies
RUN pip install -r requirements.txt

# Download nltk data
RUN python -m nltk.downloader punkt

