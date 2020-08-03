package ca.qc.johnabbott.cs616.server.controller;

import ca.qc.johnabbott.cs616.server.model.TierRow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "tierrow", excerptProjection = TierRow.class)
public interface TierRowRepository extends CrudRepository<TierRow, String> {

}
