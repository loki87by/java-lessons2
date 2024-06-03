CREATE TABLE IF NOT EXISTS users (
    id int generated by default as identity primary key,
    name VARCHAR(50) default 'noname user',
    email VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL,
    birthday date
);

CREATE TABLE IF NOT EXISTS mpa_rating (
    id int generated by default as identity primary key,
    type VARCHAR(50) NOT NULL CHECK (type IN ('G', 'PG', 'PG-13', 'R', 'NC-17'))
);

CREATE TABLE IF NOT EXISTS films (
    id int generated by default as identity primary key,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    releaseDate date NOT NULL,
    duration int CHECK (duration > 0),
    mpa_rating_id int NOT NULL,
    CONSTRAINT fk_films_mpa
        foreign key (mpa_rating_id)
            references mpa_rating (id)
);

CREATE TABLE IF NOT EXISTS feedbacks (
    id int generated by default as identity primary key,
    content VARCHAR(200),
    feedback_date TIMESTAMP NOT NULL,
    rate int CHECK (rate >= 0 and rate < 10),
    film_id int NOT NULL,
    author_id int NOT NULL,
    CONSTRAINT fk_users
        foreign key (author_id)
            references users (id),
    CONSTRAINT fk_films
        foreign key (film_id)
            references films (id)
    );

CREATE TABLE IF NOT EXISTS recommendations (
    id int generated by default as identity primary key,
    recommendation_date TIMESTAMP NOT NULL,
    film_id int NOT NULL,
    author_id int NOT NULL,
    friend_id int NOT NULL,
    CONSTRAINT fk_friends
        foreign key (friend_id)
            references users (id),
    CONSTRAINT fk_users_recomendations
        foreign key (author_id)
            references users (id),
    CONSTRAINT fk_films_recomendations
        foreign key (film_id)
            references films (id)
    );

CREATE TABLE IF NOT EXISTS feed_types (
    id int generated by default as identity primary key,
    type int CHECK (type > 0 and type < 5)
    --entity1 doing something with entity2 and entity3 (4)
    --entity1 change param1 to param2 (3)
    --entity1 doing something with entity2 (2)
    --added/removed entity (1)
    );

CREATE TABLE IF NOT EXISTS feed_realtions (
    id int generated by default as identity primary key,
    content varchar(100) NOT NULL,
    entity_type varchar(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS feed (
    id int generated by default as identity primary key,
    feed_date timestamp,
    feed_type_id int NOT NULL,
    entity1 int NOT NULL,
    entity2 int,
    param1 varchar(100),
    param2 varchar(100),
    realtion_id int NOT NULL,
    CONSTRAINT fk_feed_type
        foreign key (feed_type_id)
            references feed_types (id),
    CONSTRAINT fk_feed_text
        foreign key (realtion_id)
            references feed_realtions (id)
    );

CREATE TABLE IF NOT EXISTS friendship (
    id int generated by default as identity primary key,
    stateId int NOT NULL default 0,
    --0 = Decline
    --1 = Accept
    from_user int NOT NULL,
    to_user int NOT NULL,
    CONSTRAINT fk_friendship_from
        foreign key (from_user)
            references users (id),
    CONSTRAINT fk_friendship_to
        foreign key (to_user)
            references users (id)
);

CREATE TABLE IF NOT EXISTS genres (
    id int generated by default as identity primary key,
    type VARCHAR(50) NOT NULL CHECK (type IN ('Комедия', 'Драма', 'Мультфильм', 'Триллер', 'Документальный', 'Боевик'))
);

CREATE TABLE IF NOT EXISTS film_genres (
    id int generated by default as identity primary key,
    film_id int NOT NULL,
    genre_id int NOT NULL,
    CONSTRAINT fk_genre_film_id
        foreign key (film_id)
            references films (id),
    CONSTRAINT fk_genre_genre_id
        foreign key (genre_id)
            references genres (id)
);

CREATE TABLE IF NOT EXISTS likes (
    id int generated by default as identity primary key,
    film_id int NOT NULL,
    user_id int NOT NULL,
    CONSTRAINT fk_like_film_id
        foreign key (film_id)
            references films (id),
    CONSTRAINT fk_like_user_id
        foreign key (user_id)
            references users (id)
);
