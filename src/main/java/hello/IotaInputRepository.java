package hello;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface IotaInputRepository extends MongoRepository<IotaInput, String> {
}
