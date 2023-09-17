INSERT INTO genre_name (genre_id, name) VALUES
(1000, 'Комедия'),
(2000, 'Драма'),
(3000, 'Мультфильм'),
(4000, 'Триллер'),
(5000, 'Документальный'),
(6000, 'Боевик');

INSERT INTO MPA_names (mpa_id, name) VALUES
(1000, 'G'),
(2000, 'PG'),
(3000, 'PG-13'),
(4000, 'R'),
(5000, 'NC-17');

insert into director (id, name) values
(1000, 'director 1'),
(2000, 'director 2'),
(4000, 'director 4'),
(3000, 'director 3'),
(5000, 'director 5');

ALTER TABLE films SET REFERENTIAL_INTEGRITY false;
insert into films (id, name,  description,  releasedate,  duration, rating_id) values
(1000, 'Film name 1', 'Description 1', now(), 2, (select mpa_id from MPA_names limit 1)),
(2000, 'Film name 2', 'Description 2', now(), 2, (select mpa_id from MPA_names limit 1)),
(3000, 'Film name 3', 'Description 3', now(), 2, (select mpa_id from MPA_names limit 1)),
(4000, 'Film name 4', 'Description 4', now(), 2, (select mpa_id from MPA_names limit 1)),
(5000, 'Film name 5', 'Description 5', now(), 2, (select mpa_id from MPA_names limit 1)),
(6000, 'Film name 6', 'Description 6', now(), 2, (select mpa_id from MPA_names limit 1));

insert into film_genres (film_id, genre_id) values
(1000, 1000), (1000, 6000), (1000, 3000),
(2000, 1000), (2000, 2000),
(3000, 1000), (3000, 5000),
(4000, 1000), (4000, 4000),
(5000, 1000), (5000, 2000),
(6000, 6000);

insert into films_to_directors (film_id, director_id, order_by) values
(1000, 3000, 2),
(2000, 2000, 1),
(3000, 2000, 1), (3000, 1000, 2),
(4000, 2000, 1),
(5000, 2000, 1), (5000, 3000, 2),
(6000, 2000, 1), (6000, 5000, 3), (6000, 4000, 2);

insert into users (id,email,name,login,birthday) values
(1000  ,  'email1aa@email.ru','Ivan pupkin1  ',  'Ivan_pupkin1',   now()),
(2000  ,  'email2aa@email.ru','Ivan pupkin2  ',  'Ivan_pupkin2',   now()),
(100000,  'email100@email.ru','Ivan pupkin100',  'Ivan_pupkin100', now()),
(100001,  'email101@email.ru','Ivan pupkin101',  'Ivan_pupkin101', now()),
(3000  ,  'email3aa@email.ru','Ivan pupkin3  ',  'Ivan_pupkin3',   now()),
(4000  ,  'email4aa@email.ru','Ivan pupkin4  ',  'Ivan_pupkin4',   now()),
(5000  ,  'email5aa@email.ru','Ivan pupkin5  ',  'Ivan_pupkin5',   now());

insert into likes (user_id, film_id) values
(1000, 1000),               (1000, 3000), (1000, 4000),
(2000, 1000),               (2000, 3000), (2000, 4000),
(3000, 1000), (3000, 2000), (3000, 3000),        (3000, 5000),
(4000, 1000), (4000, 2000), (4000, 3000),        (4000, 5000),
(5000, 1000), (5000, 2000), (5000, 3000),               (5000, 6000);


