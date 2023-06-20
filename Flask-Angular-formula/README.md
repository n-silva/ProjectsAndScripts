Fullstack App with Flask+Angular that solve a math formula and return the result.

# Server (API with Flask)

## Run project with

- docker-compose up -d

## Run migrations with

To create migrations:
> docker-compose exec web python app.py db migrate

To apply migrations:
> docker-compose exec web python app.py db upgrade


# Client web angular

## Run with command


Install dependencies

> npm install

Run  the client

> npm start


