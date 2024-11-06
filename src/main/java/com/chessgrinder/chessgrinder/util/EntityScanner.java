package com.chessgrinder.chessgrinder.util;

import jakarta.persistence.Entity;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.HashSet;
import java.util.Set;

public class EntityScanner {

    public Set<Class<?>> findEntityClasses(String basePackage) {
        Set<Class<?>> entityClasses = new HashSet<>();

        // Unit to find candidate classes using our filters
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        // Add filter to find classes annotates with `@Entity` annotation
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));

        // Scan packages for classes which pass our filters
        scanner.findCandidateComponents(basePackage).forEach(beanDefinition -> {
            try {
                // Add classes to our Set of entity classes
                entityClasses.add(Class.forName(beanDefinition.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                // You can choose to produce a runtime error or silently ignore this exception
                throw new RuntimeException(e);
            }
        });

        return entityClasses;
    }
}
