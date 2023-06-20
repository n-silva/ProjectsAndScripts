# base image
FROM python:3.7.4

# set environment variables
ENV PYTHONUNBUFFERED=1
# add app
COPY . /usr/src/app/

# set working directory
WORKDIR /usr/src/app

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

STOPSIGNAL SIGINT