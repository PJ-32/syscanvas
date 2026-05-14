package com.syscanvas.syscanvas.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import javax.sql.DataSource;
import java.util.HashMap;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.syscanvas.syscanvas.dao.SC",
    entityManagerFactoryRef = "scEntityManagerFactory",
    transactionManagerRef = "scTransactionManager"
)
public class SC_config {
    @Primary
    @Bean(name = "scEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean scEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {
        HashMap<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", true);
        props.put("hibernate.format_sql", true);
        props.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.syscanvas.syscanvas.model.SC")
                .persistenceUnit("SC")
                .properties(props)
                .build();
    }
    @Primary
    @Bean(name = "scTransactionManager")
    public PlatformTransactionManager scTransactionManager(
            @Qualifier("scEntityManagerFactory") LocalContainerEntityManagerFactoryBean scEntityManagerFactory) {
        return new JpaTransactionManager(scEntityManagerFactory.getObject());
    }
}