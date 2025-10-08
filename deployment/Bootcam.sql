CREATE TABLE bootcamp (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(255) NOT NULL,
                          descripcion TEXT,
                          fecha_lanzamiento DATE NOT NULL,
                          duracion INT NOT NULL

);

CREATE TABLE capacidad_bootcamp (
    bootcamp_id BIGINT NOT NULL,
    capacidad_id BIGINT NOT NULL,
    PRIMARY KEY (bootcamp_id, capacidad_id)
);