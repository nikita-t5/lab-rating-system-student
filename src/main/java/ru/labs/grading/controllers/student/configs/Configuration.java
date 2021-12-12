package ru.labs.grading.controllers.student.configs;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public ManagedChannel managedChannel(){
        return ManagedChannelBuilder.forTarget("localhost:8090")
                .usePlaintext()
                .build();
    }
}
