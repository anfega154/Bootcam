CREATE TABLE bootcamp (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          release_date DATE NOT NULL,
                          duration_weeks INT NOT NULL
);

CREATE TABLE bootcamp_abilities (
                                    bootcamp_id BIGINT NOT NULL,
                                    ability_id BIGINT NOT NULL,
                                    PRIMARY KEY (bootcamp_id, ability_id),
                                    CONSTRAINT fk_bootcamp
                                        FOREIGN KEY (bootcamp_id) REFERENCES bootcamp (id)
                                            ON DELETE CASCADE
);

CREATE INDEX idx_bootcamp_ability_id ON bootcamp_abilities (ability_id);
