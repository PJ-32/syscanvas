package com.syscanvas.syscanvas.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import javax.sql.DataSource;
import java.util.HashMap;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.syscanvas.syscanvas.dao.T",
    entityManagerFactoryRef = "tEntityManagerFactory",
    transactionManagerRef = "tTransactionManager"
)
public class T_config {
    @Bean(name = "tEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean tEntityManagerFactory(
            EntityManagerFactoryBuilder builder, DataSource dataSource) {
        HashMap<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "none"); //T_ no se modifica
        props.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect"); 

        return builder
                .dataSource(dataSource)
                .packages("com.syscanvas.syscanvas.model.T")
                .persistenceUnit("T")
                .properties(props)
                .build();
    }
    @Bean(name = "tTransactionManager")
    public PlatformTransactionManager tTransactionManager(
            @Qualifier("tEntityManagerFactory") LocalContainerEntityManagerFactoryBean tEntityManagerFactory) {
        return new JpaTransactionManager(tEntityManagerFactory.getObject());
    }
}