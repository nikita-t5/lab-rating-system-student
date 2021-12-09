package ru.labs.grading;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
        System.out.println("hi");

        //создали канал передачи и приема данными
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8090")
                .usePlaintext().build();

        //создание объекта stab - на нем делаем удаленные запросы
        //stub сделает вызов метода по сети и вернет ответ
        GreetingServiceGrpc.GreetingServiceBlockingStub stub =
                GreetingServiceGrpc.newBlockingStub(channel);

        //создаем объект реквеста
        GreetingServiceOuterClass.HelloRequest request = GreetingServiceOuterClass.HelloRequest
                .newBuilder().setName("Ivan").build();

        //удаленный вызов процедуры
        GreetingServiceOuterClass.HelloResponse response = stub.greeting(request);

        System.out.println(response);

        channel.shutdownNow();
    }
}
