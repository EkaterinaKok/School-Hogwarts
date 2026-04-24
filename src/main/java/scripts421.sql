-- Возраст студента не может быть меньше 16 лет
ALTER TABLE student ADD CONSTRAINT age_check CHECK (age >= 16);

-- Имена студентов должны быть уникальными и не равны нулю
ALTER TABLE student ALTER COLUMN name SET NOT NULL;
ALTER TABLE student ADD CONSTRAINT name_unique UNIQUE (name);

-- При создании студента без возраста ему автоматически присваивается 20 лет
ALTER TABLE student ALTER COLUMN age SET DEFAULT 20;

-- Пара «название — цвет факультета» должна быть уникальной
ALTER TABLE faculty ADD CONSTRAINT color_name_unique UNIQUE (color, name);