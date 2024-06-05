insert into PUBLIC.MPA_RATING (type) select 'G'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'G');
insert into PUBLIC.MPA_RATING (type) select 'PG'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'PG');
insert into PUBLIC.MPA_RATING (type) select 'PG-13'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'PG-13');
insert into PUBLIC.MPA_RATING (type) select 'R'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'R');
insert into PUBLIC.MPA_RATING (type) select 'NC-17'
where not exists (select 1 from PUBLIC.MPA_RATING where type = 'NC-17');
insert into PUBLIC.genres (type) select 'Комедия'
where not exists (select 1 from PUBLIC.genres where type = 'Комедия');
insert into PUBLIC.genres (type) select 'Драма'
where not exists (select 1 from PUBLIC.genres where type = 'Драма');
insert into PUBLIC.genres (type) select 'Мультфильм'
where not exists (select 1 from PUBLIC.genres where type = 'Мультфильм');
insert into PUBLIC.genres (type) select 'Триллер'
where not exists (select 1 from PUBLIC.genres where type = 'Триллер');
insert into PUBLIC.genres (type) select 'Документальный'
where not exists (select 1 from PUBLIC.genres where type = 'Документальный');
insert into PUBLIC.genres (type) select 'Боевик'
where not exists (select 1 from PUBLIC.genres where type = 'Боевик');
insert into PUBLIC.feed_realtions (content, entity_type) select 'На портал добавлен фильм: ', ''
where not exists (select 1 from PUBLIC.feed_realtions where content = 'На портал добавлен фильм: ');
insert into PUBLIC.feed_realtions (content, entity_type) select 'К нам присоединился пользователь: ', ''
where not exists (select 1 from PUBLIC.feed_realtions where content = 'К нам присоединился пользователь: ');
insert into PUBLIC.feed_realtions (content, entity_type) select 'С портала удален фильм: ', ''
where not exists (select 1 from PUBLIC.feed_realtions where content = 'С портала удален фильм: ');
insert into PUBLIC.feed_realtions (content, entity_type) select 'Нас покинул пользователь: ', ''
where not exists (select 1 from PUBLIC.feed_realtions where content = 'Нас покинул пользователь: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' подписался на пользователя: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' подписался на пользователя: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' принял в друзья пользователя: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' принял в друзья пользователя: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' отписался от пользователя: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' отписался от пользователя: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' удалил из друзей пользователя: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' удалил из друзей пользователя: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' прокомментировал фильм: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' прокомментировал фильм: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' поставил лайк фильму: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' поставил лайк фильму: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' обновлен список жанров: ', 'У фильма: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' обновлен список жанров: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' изменилось название с: ', 'У фильма: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' изменилось название с: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' изменилось описание с: ', 'У фильма: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' изменилось описание с: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' изменилась дата выхода с: ', 'У фильма: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' изменилась дата выхода с: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' изменилась продолжительность с: ', 'У фильма: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' изменилась продолжительность с: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' изменилось возрастное ограничение с: ', 'У фильма: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' изменилось возрастное ограничение с: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' изменил имя с: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' изменил имя с: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' изменил почту с: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' изменил почту с: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' поменял дату рождения с: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' поменял дату рождения с: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' порекомендовал фильм: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' порекомендовал фильм: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' отменил лайк у фильма: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' отменил лайк у фильма: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' отредактировал комментарий к фильму: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' отредактировал комментарий к фильму: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' поставил/изменил оценку к фильму: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' поставил/изменил оценку к фильму: ');
insert into PUBLIC.feed_realtions (content, entity_type) select 'Удалён отзыв о фильме: ', ''
where not exists (select 1 from PUBLIC.feed_realtions where content = 'Удалён отзыв о фильме: ');
insert into PUBLIC.feed_realtions (content, entity_type) select ' отменил рекомендацию фильма: ', 'Пользователь: '
where not exists (select 1 from PUBLIC.feed_realtions where content = ' отменил рекомендацию фильма: ');
insert into PUBLIC.feed_types (type) select 1
where not exists (select 1 from PUBLIC.feed_types where type = 1);
insert into PUBLIC.feed_types (type) select 2
where not exists (select 1 from PUBLIC.feed_types where type = 2);
insert into PUBLIC.feed_types (type) select 3
where not exists (select 1 from PUBLIC.feed_types where type = 3);
insert into PUBLIC.feed_types (type) select 4
where not exists (select 1 from PUBLIC.feed_types where type = 4);

--entity1 doing something with entity2 and entity3 (4)
/*
select * from mpa_rating;
select * from genres
update mpa_rating set id = 1 where type = 'G';
*/

