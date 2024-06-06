package com.example.filmorate.dao.impls;

import com.example.filmorate.dao.FeedDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.error.MissingEnvironmentVariableException;

import java.rmi.ServerException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                "insert into feed (feed_date, feed_type_id, relation_id, entity1 ,entity2, param1 ,param2)" +
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
    public void addToFeed(int relationId, int entity1, int entity2, String param1, String param2) {
        addFeed(5, relationId, entity1, entity2, param1, param2);
    }

    @Override
    public void addToFeed(int relationId, int entity1, int entity2, String param1) {
        addFeed(4, relationId, entity1, entity2, param1, null);
    }

    private void filmParamsUpdate(String columnName, Object oldValue, Object newValue, int entityId) {
        int relationId = switch (columnName) {
            case "name" -> 12;
            case "description" -> 13;
            case "director" -> 26;
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

    private void feedbackParamsUpdate(String columnName, Object oldValue, Object newValue, int entityId, int entity2Id) {
        int relationId = switch (columnName) {
            case "content" -> 22;
            case "rate" -> 23;
            default -> 0;
        };

        if (relationId == 0) {
            return;
        }
        addToFeed(relationId, entityId, entity2Id, oldValue.toString(), newValue.toString());
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

            if (!oldValue.toString().equals(newValue.toString())) {

                if (entityName.equals("films")) {
                    filmParamsUpdate(columnName, oldValue, newValue, entityId);
                }

                if (entityName.equals("users")) {
                    userParamsUpdate(columnName, oldValue, newValue, entityId);
                }

                if (entityName.equals("feedbacks")) {
                    int filmIdIndex = notNullParamsList.indexOf("film_id = ?");

                    if (filmIdIndex == -1) {
                        throw new MissingEnvironmentVariableException("Необходимо передать id фильма.");
                    }
                    int filmId = Integer.parseInt(paramValues.get(filmIdIndex).toString());
                    feedbackParamsUpdate(columnName, oldValue, newValue, entityId, filmId);
                }
            }
        }
    }

    private String[] getRelationData(int id) {
        String sql = "select * from feed_relations where id = ?;";
        return jdbcTemplate.queryForObject(sql, (rs, _) -> {
            String content = rs.getString("content");
            String entityType = rs.getString("entity_type");
            return new String[]{content, entityType};
        }, id);
    }

    private String getFeed(ResultSet rs) throws SQLException, ServerException {
        Timestamp time = rs.getTimestamp("FEED_DATE");
        int relId = rs.getInt("RELATION_ID");
        String entityType = getRelationData(relId)[1];
        String description = getRelationData(relId)[0];
        int type = rs.getInt("FEED_TYPE_ID");
        int entity1id = rs.getInt("entity1");

        if (type == 1) {
            return STR."\{time}: \{description}\{entity1id}";
        }
        int entity2id = rs.getInt("entity2");

        if (type == 2) {
            return STR."\{time}: \{entityType}\{entity1id}\{description}\{entity2id}";
        }
        String param1content = rs.getString("param1");
        String param2content = rs.getString("param2");

        if (type == 3) {
            return STR."\{time}: \{entityType}\{entity1id}\{description} \{param1content} на: \{param2content}";
        }

        if (type == 4) {
            return STR."\{time}: \{entityType}\{entity1id}\{description}\{entity2id} пользователю c id: \{param1content}";
        }

        if (type == 5) {
            return STR."\{time}: \{entityType}\{entity1id}\{description}\{entity2id} c: \{param1content} на: \{param2content}";
        }
        throw new ServerException("Что-то пошло не так.");
    }

    @Override
    public List<String> getHistory(int limit, int page, String direction) {

        if (page < 1) {
            page = 1;
        }
        String sql = STR."select * from feed order by FEED_DATE \{direction} limit \{limit} offset \{limit * (page - 1)}";
        return jdbcTemplate.query(sql, (rs, _) -> {
            try {
                return getFeed(rs);
            } catch (ServerException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
