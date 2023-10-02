package com.incorta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class MyAppRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MyAppRunner.class);

    @Autowired
    private ConfigurableEnvironment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Application started! listing configurations:");
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) propertySource;
                String[] propertyNames = enumerablePropertySource.getPropertyNames();

                if (propertyNames != null) {
                    Arrays.stream(propertyNames)
                            .filter(propertyName -> {
                                return (propertyName.startsWith("server.") ||
                                        propertyName.startsWith("web-app.") ||
                                        propertyName.startsWith("incorta.") ||
                                        propertyName.startsWith("gcs."));
                            })
                            .forEach(propertyName -> {
                                Object propertyValue = environment.getProperty(propertyName);
                                logger.info("{} = {}", propertyName, propertyValue);
                            });
                }
            }
        }

    }
}
