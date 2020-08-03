package ca.qc.johnabbott.cs616.server.controller;

import ca.qc.johnabbott.cs616.server.model.TierList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "tierlist", excerptProjection = TierList.class)
public interface TierListRepository extends CrudRepository<TierList, String> {
}
