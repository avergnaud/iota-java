package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = IotaInputRepository.class)
public class Application {

    @Autowired
    private IotaInputRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}