package com.example.filmorate.dao.impls;

import com.example.filmorate.dao.FeedDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
public class FeedDaoImpl implements FeedDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private void addFeed(int feedTypeId, int relationId, int entity1, Integer entity2, String param1, String param2) {
        Timestamp feedDate = Timestamp.from(Instant.now());
        String sql =
                "insert into feed (feed_date, feed_type_id, realtion_id, entity1 ,entity2, param1 ,param2)" +
                        "values (?, ?, ?, ?, ?, ?, ?);";

        jdbcTemplate.update(sql, feedDate, feedTypeId, relationId, entity1, entity2, param1, param2);
    }

    @Override
    public void addToFeed(int relationId, int entity1) {
        addFeed(1, relationId, entity1, null, null, null);
    }

    @Override
    public void addToFeed(int relationId, int entity1, int entity2) {
        addFeed(2, relationId, entity1, entity2, null, null);
    }

    @Override
    public void addToFeed(int relationId, int entity1, String param1, String param2) {
        addFeed(3, relationId, entity1, null, param1, param2);
    }

    @Override
    public void addToFeed(int relationId, int entity1, int entity2, String param1) {
        addFeed(4, relationId, entity1, entity2, param1, null);
    }

    private void filmParamsUpdate(String columnName, Object oldValue, Object newValue, int entityId) {
        int relationId = switch (columnName) {
            case "name" -> 12;
            case "description" -> 13;
            case "releaseDate" -> 14;
            case "duration" -> 15;
            case "mpa_rating_id" -> 16;
            default -> 0;
        };
        addToFeed(relationId, entityId, oldValue.toString(), newValue.toString());
    }

    private void userParamsUpdate(String columnName, Object oldValue, Object newValue, int entityId) {
        int relationId = switch (columnName) {
            case "name" -> 17;
            case "email" -> 18;
            case "birthday" -> 19;
            default -> 0;
        };

        if (relationId == 0) {
            return;
        }
        addToFeed(relationId, entityId, oldValue.toString(), newValue.toString());
    }

    private void feedbackParamsUpdate(String columnName, Object oldValue, Object newValue, int entityId) {
        int relationId = switch (columnName) {
            case "content" -> 22;
            case "rate" -> 23;
            default -> 0;
        };

        if (relationId == 0) {
            return;
        }
        addToFeed(relationId, entityId, oldValue.toString(), newValue.toString());
    }

    @Override
    public void checkUpdates(String entityName, int entityId, List<String> notNullParamsList, List<Object> paramValues) {
        for (int i = 0; i < notNullParamsList.size(); i++) {
            String columnName = notNullParamsList.get(i).split(" ")[0];
            String sql = STR."select \{columnName} from \{entityName} where id = ?";
            Object oldValue = jdbcTemplate.queryForObject(sql, Object.class, entityId);
            Object newValue = paramValues.get(i);

            if (oldValue == null) {
                oldValue = "пусто";
            }

            if (!oldValue.toString().equals(newValue.toString())){

                if (entityName.equals("films")) {
                    filmParamsUpdate(columnName, oldValue, newValue, entityId);
                }

                if (entityName.equals("users")) {
                    userParamsUpdate(columnName, oldValue, newValue, entityId);
                }

                if (entityName.equals("feedbacks")) {
                    feedbackParamsUpdate(columnName, oldValue, newValue, entityId);
                }
            }
        }
    }
    /*
    feed_date timestamp,
    feed_type_id int NOT NULL,,
    realtion_id int NOT NULL
    entity1 int NOT NULL,
    entity2 int,
    param1 varchar(100),
    param2 varchar(100)

    if t1 => e1+ri
    if t2 => e1+ri+e2
    if t3 => e1+ri+p1+p2
    if t4 => e1+ri+e2+e3

    *

    feed type id
    --entity1 doing something with entity2 and entity3 (4)
    --entity1 change param1 to param2 (3)
    --entity1 doing something with entity2 (2)
    --added/removed entity (1)

    *

     realtions:
1 (content, entity_type) = 'На портал добавлен фильм: ', ''
2 (content, entity_type) = 'К нам присоединился пользователь: ', ''
3 (content, entity_type) = 'С портала удален фильм: ', ''
4 (content, entity_type) = 'Нас покинул пользователь: ', ''
5 (content, entity_type) = ' подписался на пользователя: ', 'Пользователь: '
6 (content, entity_type) = ' принял в друзья пользователя: ', 'Пользователь: '
7 (content, entity_type) = ' отписался от пользователя: ', 'Пользователь: '
8 (content, entity_type) = ' удалил из друзей пользователя: ', 'Пользователь: '
9 (content, entity_type) = ' прокомментировал фильм: ', 'Пользователь: '
10 (content, entity_type) = ' поставил лайк фильму: ', 'Пользователь: '
11 (content, entity_type) = ' обновлен список жанров: ', 'У фильма: '
12 (content, entity_type) = ' изменилось название с: ', 'У фильма: '
13 (content, entity_type) = ' изменилось описание с: ', 'У фильма: '
14 (content, entity_type) = ' изменилась дата выхода с: ', 'У фильма: '
15 (content, entity_type) = ' изменилась продолжительность с: ', 'У фильма: '
16 (content, entity_type) = ' изменилось возрастное ограничение с: ', 'У фильма: '
17 (content, entity_type) = ' изменил имя с: ', 'Пользователь: '
18 (content, entity_type) = ' изменил почту с: ', 'Пользователь: '
19 (content, entity_type) = ' поменял дату рождения с: ', 'Пользователь: '
20 (content, entity_type) = ' порекомендовал фильм: ', 'Пользователь: '
21 (content, entity_type) = ' отменил лайк у фильма: ', 'Пользователь: '
22 (content, entity_type) = ' отредактировал комментарий к фильму: ', 'Пользователь: '
23 (content, entity_type) = ' поставил/изменил оценку к фильму: ', 'Пользователь: '
24 (content, entity_type) = 'Удалён отзыв о фильме: '
25 (content, entity_type) = ' отменил рекомендацию фильма: ', 'Пользователь: '
*/
}
