package com.example.filmorate.service;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;


@Service
public class ManualJbdcConnectService {

        public static final String JDBC_URL="jdbc:h2:file:./db/filmorate";
        public static final String JDBC_USERNAME="sa";
        public static final String JDBC_PASSWORD="password";
        public static final String JDBC_DRIVER="org.h2.Driver";

        @Bean
        public JdbcTemplate getTemplate() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(JDBC_DRIVER);
            dataSource.setUrl(JDBC_URL);
            dataSource.setUsername(JDBC_USERNAME);
            dataSource.setPassword(JDBC_PASSWORD);
            return new JdbcTemplate(dataSource);
        }
}
