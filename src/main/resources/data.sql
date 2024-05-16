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

/*
select * from mpa_rating;
select * from genres
update mpa_rating set id = 1 where type = 'G';
*/
create table MPA_RATING
(
    "'G'" int
);

