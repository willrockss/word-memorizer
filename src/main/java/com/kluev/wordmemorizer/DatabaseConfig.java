package com.kluev.wordmemorizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {

        String dbUrlKeyName;
        if (WordMemorizerApplication.useRealDb) {
            dbUrlKeyName = "DATABASE_URL(REAL)";
        } else {
            //Имя ключа, используемое на хостинге Heroku по-умолчанию.
            dbUrlKeyName = "DATABASE_URL";
        }
        String databaseUrl = System.getenv(dbUrlKeyName);
        UrlParser parser = new UrlParser(databaseUrl);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(parser.getUrl());
        dataSource.setUsername(parser.getUsername());
        dataSource.setPassword(parser.getPassword());
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.POSTGRESQL);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(getClass().getPackage().getName());
        factory.setDataSource(dataSource());

        return factory;
    }

    @Bean
    @Autowired
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return txManager;
    }

    static class UrlParser {
        private String url;
        private String username;
        private String password;

        public UrlParser(String fullUrl) {
            int pos1 = fullUrl.indexOf("//");
            int pos2 = fullUrl.indexOf("@");
            String loginPass = fullUrl.substring(pos1 + 2, pos2);
            String[] pair = loginPass.split(":");
            username = pair[0];
            password = pair[1];
            url = fullUrl.replace(loginPass + "@", "");

            url = url.replace("postgres:", "postgresql:");

            if (!url.startsWith("jdbc:")) {
                url = "jdbc:" + url;
            }

            if (WordMemorizerApplication.useRealDb) {
                url += "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
            }
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
