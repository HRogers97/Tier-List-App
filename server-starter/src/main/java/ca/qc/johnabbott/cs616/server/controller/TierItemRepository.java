package ca.qc.johnabbott.cs616.server.controller;

import ca.qc.johnabbott.cs616.server.model.TierItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "tieritem", excerptProjection = TierItem.class)
public interface TierItemRepository extends CrudRepository<TierItem, String> {
}
