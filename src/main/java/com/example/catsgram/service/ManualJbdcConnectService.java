package com.example.catsgram.service;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

@Service
public class ManualJbdcConnectService {

    public static final String JDBC_URL="jdbc:postgresql://localhost:5432/cats";
    public static final String JDBC_USERNAME="atos";
    public static final String JDBC_PASSWORD="admincat";
    public static final String JDBC_DRIVER="org.postgresql.Driver";

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
