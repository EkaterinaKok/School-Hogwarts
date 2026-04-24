CREATE TABLE car (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(255),
    model VARCHAR(255),
    price INT
);

CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    age INT,
    has_license BOOLEAN,
    car_id INT REFERENCES car(id)
);
