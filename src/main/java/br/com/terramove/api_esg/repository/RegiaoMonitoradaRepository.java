package br.com.terramove.api_esg.repository;

import br.com.terramove.api_esg.model.RegiaoMonitorada;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegiaoMonitoradaRepository extends MongoRepository<RegiaoMonitorada, String> {
}
