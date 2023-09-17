CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50),
    login VARCHAR(50) NOT NULL UNIQUE,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS MPA_names (
    mpa_id INTEGER PRIMARY KEY,
    name VARCHAR(50),
    CONSTRAINT MPA_PK PRIMARY KEY (mpa_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id INTEGER,
    friend_id INTEGER,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS films (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    description VARCHAR(200),
    releaseDate DATE,
    duration INTEGER,
    rating_id INTEGER UNIQUE,
    FOREIGN KEY (rating_id) REFERENCES MPA_names(mpa_id)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id INTEGER,
    user_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (film_id) REFERENCES films(id)
);

CREATE TABLE IF NOT EXISTS genre_name (
    genre_id INTEGER PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER,
    genre_id INTEGER,
    FOREIGN KEY (genre_id) REFERENCES genre_name(genre_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR,
    is_positive VARCHAR,
    user_id INTEGER,
    film_id INTEGER,
    useful INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews_like (
    review_id INTEGER REFERENCES reviews(id) ON DELETE CASCADE,
    user_id_like INTEGER REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS reviews_dislike (
    review_id INTEGER REFERENCES reviews(id) ON DELETE CASCADE,
    user_id_dislike INTEGER REFERENCES users(id)
);