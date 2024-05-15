package com.example.catsgram.dao.impl;

import com.example.catsgram.dao.PostDao;
import com.example.catsgram.model.Post;
import com.example.catsgram.model.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;


@Component
public class PostDaoImpl implements PostDao {
    //private static final Logger log = LoggerFactory.getLogger(PostDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    public PostDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate=jdbcTemplate;
    }

    private Post makePost(User user, ResultSet rs) throws SQLException {
        Post post = new Post(
                user, rs.getString("description"),
                rs.getString("photo_url"),
                rs.getDate("creation_date").toLocalDate()
        );
        post.setId(rs.getInt("id"));
        return post;
    }

    @Override
    public Collection<Post> findAllByUser(User user) {
        System.out.println("ok");
        String sql = "SELECT * FROM cat_post WHERE author_id = ? order by creation_date desc;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makePost(user, rs), user.getId());
    }

}
