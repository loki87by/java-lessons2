package com.example.catsgram.dao.impl;

import com.example.catsgram.dao.FollowDao;
import com.example.catsgram.dao.PostDao;
import com.example.catsgram.dao.UserDao;
import com.example.catsgram.exceptions.NotFoundException;
import com.example.catsgram.model.Post;
import com.example.catsgram.model.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FollowDaoImpl implements FollowDao {
    //private static final Logger log = LoggerFactory.getLogger(FollowDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final UserDao userDao;
    private final PostDao postDao;
    public FollowDaoImpl(JdbcTemplate jdbcTemplate, UserDao userDao, PostDao postDao) {
        this.jdbcTemplate=jdbcTemplate;
        this.userDao = userDao;
        this.postDao = postDao;
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

    /*@Override
    User author, String description, String photoUrl
    Integer id, User author, LocalDate creationDate, String description, String photoUrl

    public List<Post> findAllByUser(User user) {
        System.out.println("ok");
        String sql = "SELECT * FROM cat_post WHERE author_id = ? order by creation_date desc;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makePost(user, rs), user.getId());
    }*/

    @Override
    public List<Post> getFollowFeed(String userId, int max) throws NotFoundException {
        User user = userDao.findUserById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        String sql ="select * from cat_post where author_id in " +
                "(SELECT follower FROM cat_follow WHERE author_id = ?) " +
                "order by creation_date desc limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makePost(user, rs), userId, max);
    }
}
