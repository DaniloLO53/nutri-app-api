ALTER TABLE appointments_status ALTER COLUMN name TYPE VARCHAR(25);
INSERT INTO appointments_status(name) VALUES ('ESPERANDO CONFIRMAÇÃO');