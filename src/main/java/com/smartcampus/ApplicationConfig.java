package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Resources
        classes.add(com.smartcampus.resource.DiscoveryResource.class);
        classes.add(com.smartcampus.resource.RoomResource.class);
        classes.add(com.smartcampus.resource.SensorResource.class);

        // Exception Mappers
        classes.add(com.smartcampus.exception.RoomNotEmptyExceptionMapper.class);
        classes.add(com.smartcampus.exception.LinkedResourceNotFoundExceptionMapper.class);
        classes.add(com.smartcampus.exception.SensorUnavailableExceptionMapper.class);
        classes.add(com.smartcampus.exception.GlobalExceptionMapper.class);

        // Filters
        classes.add(com.smartcampus.filter.LoggingFilter.class);

        return classes;
    }
}