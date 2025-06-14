DROP TABLE IF EXISTS song_instruments;
DROP TABLE IF EXISTS songs;
DROP TABLE IF EXISTS instruments;
DROP TABLE IF EXISTS subgenres;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS subscriptions;

CREATE TABLE genres (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
);

CREATE TABLE instruments (
                             id SERIAL PRIMARY KEY,
                             category VARCHAR(255),
                             description TEXT,
                             name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE subscriptions (
                               id SERIAL PRIMARY KEY,
                               commercial_use BOOLEAN,
                               end_date TIMESTAMP,
                               high_quality_audio BOOLEAN,
                               max_downloads_per_day INT,
                               max_downloads_per_month INT,
                               max_track_length DOUBLE PRECISION,
                               max_tracks_per_day INT,
                               max_tracks_per_month INT,
                               start_date TIMESTAMP,
                               type VARCHAR(20)
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       subscription_id INT UNIQUE,
                       role VARCHAR(10) NOT NULL,
                       name VARCHAR(255),
                       CONSTRAINT fk_users_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions (id)
);

CREATE TABLE subgenres (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255),
                           genre_id INT NOT NULL,
                           CONSTRAINT fk_subgenre_genre FOREIGN KEY (genre_id) REFERENCES genres (id)
);

CREATE TABLE songs (
                       id SERIAL PRIMARY KEY,
                       created_at TIMESTAMP,
                       duration DOUBLE PRECISION,
                       generated_url TEXT,
                       prompt_text VARCHAR(255),
                       tempo INT,
                       genre_id INT,
                       subgenre_id INT,
                       user_id INT,
                       title VARCHAR(255),
                       CONSTRAINT fk_song_genre FOREIGN KEY (genre_id) REFERENCES genres (id),
                       CONSTRAINT fk_song_subgenre FOREIGN KEY (subgenre_id) REFERENCES subgenres (id),
                       CONSTRAINT fk_song_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE song_instruments (
                                  song_id INT NOT NULL,
                                  instrument_id INT NOT NULL,
                                  CONSTRAINT fk_song_inst_song FOREIGN KEY (song_id) REFERENCES songs (id),
                                  CONSTRAINT fk_song_inst_instr FOREIGN KEY (instrument_id) REFERENCES instruments (id)
);


