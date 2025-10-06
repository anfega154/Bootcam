CREATE TABLE bootcamp (
                          id BIGSERIAL PRIMARY KEY,
                          nombre VARCHAR(255) NOT NULL,
                          descripcion TEXT,
                          fecha_lanzamiento DATE NOT NULL,
                          duracion INT NOT NULL,
                          capacidades TEXT

);

