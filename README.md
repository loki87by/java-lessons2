## [ТЗ здесь:](https://cloud.mail.ru/public/FDzh/qALfZDJyS/%5BBoominfo.ORG%5D%20%D0%A1%D0%BF%D1%80%D0%B8%D0%BD%D1%82%2010/%5BBoominfo.ORG%5D%204%20%D0%9F%D1%80%D0%BE%D0%B5%D0%BA%D1%82/%5BBoominfo.ORG%5D%20%D0%A2%D0%B5%D1%85%D0%BD%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%BE%D0%B5%20%D0%B7%D0%B0%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5.jpg)

### Схема БД:
#### Genres
genreId (int/**unique key**)  
type (int):
- Комедия
- Драма
- Мультфильм
- Триллер
- Документальный
- Боевик
---------

#### MPA_Rating
ratingId (int/**unique key**)  
type (int):
- G - for all
- PG - for child with parents
- PG-13 - 13+
- R -  for 17- with 18+
- NC-17 - 18+
---------

#### Likes
likeId (int/**unique key**)  
filmId (int/*unique key from Film table*)    
userId (int/*unique key from User table*)
---------

#### Film_Genres
genreId (int/*unique key from Genres table*)  
filmId (int/*unique key from Film table*)    
id (int/**unique key**)
---------

#### Friendship
inviteId (int/**unique key**)  
from (int/*unique key from User table*)    
to (int/*unique key from User table*)  
stateId(int):
- Accept
- Decline
---------

#### Film 
filmId (int/**unique key**)  
name (varchar)  
description (varchar(200))  
releaseDate (date)  
duration (int)  
mpaRating (int/*unique key from MPA_Rating table*)
---------

#### User 
userId (int/**unique key**)  
email (varchar)  
login (varchar)  
name (varchar)  
birthday (date)  