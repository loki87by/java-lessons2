insert into PUBLIC.MPA_RATING (type)
select 'G'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'G');
insert into PUBLIC.MPA_RATING (type)
select 'PG'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'PG');
insert into PUBLIC.MPA_RATING (type)
select 'PG-13'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'PG-13');
insert into PUBLIC.MPA_RATING (type)
select 'R'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'R');
insert into PUBLIC.MPA_RATING (type)
select 'NC-17'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'NC-17');
insert into PUBLIC.genres (type)
select 'Комедия'
where not exists (select 1 from PUBLIC.genres where type = 'Комедия');
insert into PUBLIC.genres (type)
select 'Драма'
where not exists (select 1 from PUBLIC.genres where type = 'Драма');
insert into PUBLIC.genres (type)
select 'Мультфильм'
where not exists (select 1 from PUBLIC.genres where type = 'Мультфильм');
insert into PUBLIC.genres (type)
select 'Триллер'
where not exists (select 1 from PUBLIC.genres where type = 'Триллер');
insert into PUBLIC.genres (type)
select 'Документальный'
where not exists (select 1 from PUBLIC.genres where type = 'Документальный');
insert into PUBLIC.genres (type)
select 'Боевик'
where not exists (select 1 from PUBLIC.genres where type = 'Боевик');
insert into PUBLIC.feed_relations (content, entity_type)
select 'На портал добавлен фильм с id=', ''
where not exists (select 1 from PUBLIC.feed_relations where content = 'На портал добавлен фильм с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select 'К нам присоединился пользователь с id=', ''
where not exists (select 1 from PUBLIC.feed_relations where content = 'К нам присоединился пользователь с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select 'С портала удален фильм с id=', ''
where not exists (select 1 from PUBLIC.feed_relations where content = 'С портала удален фильм с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select 'Нас покинул пользователь с id=', ''
where not exists (select 1 from PUBLIC.feed_relations where content = 'Нас покинул пользователь с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' подписался на пользователя с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' подписался на пользователя с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' принял в друзья пользователя с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' принял в друзья пользователя с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' отписался от пользователя с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' отписался от пользователя с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' удалил из друзей пользователя с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' удалил из друзей пользователя с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' прокомментировал фильм с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' прокомментировал фильм с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' поставил лайк фильму с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' поставил лайк фильму с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' обновлен список жанров с:', 'У фильма с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' обновлен список жанров с:');
insert into PUBLIC.feed_relations (content, entity_type)
select ' изменилось название с: ', 'У фильма с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' изменилось название с: ');
insert into PUBLIC.feed_relations (content, entity_type)
select ' изменилось описание с: ', 'У фильма с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' изменилось описание с: ');
insert into PUBLIC.feed_relations (content, entity_type)
select ' изменилась дата выхода с: ', 'У фильма с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' изменилась дата выхода с: ');
insert into PUBLIC.feed_relations (content, entity_type)
select ' изменилась продолжительность с: ', 'У фильма с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' изменилась продолжительность с: ');
insert into PUBLIC.feed_relations (content, entity_type)
select ' изменилось возрастное ограничение с: ', 'У фильма с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' изменилось возрастное ограничение с: ');
insert into PUBLIC.feed_relations (content, entity_type)
select ' изменил имя с: ', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' изменил имя с: ');
insert into PUBLIC.feed_relations (content, entity_type)
select ' изменил почту с: ', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' изменил почту с: ');
insert into PUBLIC.feed_relations (content, entity_type)
select ' поменял дату рождения с: ', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' поменял дату рождения с: ');
insert into PUBLIC.feed_relations (content, entity_type)
select ' порекомендовал фильм с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' порекомендовал фильм с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' отменил лайк у фильма с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' отменил лайк у фильма с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' отредактировал комментарий к фильму с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' отредактировал комментарий к фильму с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' поставил/изменил оценку к фильму с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' поставил/изменил оценку к фильму с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select 'Удалён отзыв о фильме с id=', ''
where not exists (select 1 from PUBLIC.feed_relations where content = 'Удалён отзыв о фильме с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' отменил рекомендацию фильма с id=', 'Пользователь с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' отменил рекомендацию фильма с id=');
insert into PUBLIC.feed_relations (content, entity_type)
select ' изменился режиссёр с: ', 'У фильма с id='
where not exists (select 1 from PUBLIC.feed_relations where content = ' изменился режиссёр с: ');
insert into PUBLIC.feed_types (type)
select 1
where not exists (select 1 from PUBLIC.feed_types where type = 1);
insert into PUBLIC.feed_types (type)
select 2
where not exists (select 1 from PUBLIC.feed_types where type = 2);
insert into PUBLIC.feed_types (type)
select 3
where not exists (select 1 from PUBLIC.feed_types where type = 3);
insert into PUBLIC.feed_types (type)
select 4
where not exists (select 1 from PUBLIC.feed_types where type = 4);
insert into PUBLIC.feed_types (type)
select 5
where not exists (select 1 from PUBLIC.feed_types where type = 5);
