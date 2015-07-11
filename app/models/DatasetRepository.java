package models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface DatasetRepository extends CrudRepository<Dataset, Long> {
	List<Dataset> findByNameLikeAndAgencyIdLikeAndGridDimensionLikeAndPhysicalVariableLikeAndInstrument_Id(String name, String agencyId, String gridDimension, String physicalVariable,long instrumentId);
	List<Dataset> findByNameLikeAndAgencyIdLikeAndGridDimensionLikeAndPhysicalVariableLike(String name, String agencyId, String gridDimension, String physicalVariable);
	List<Dataset> findByvariableNameInWebInterface(String variableNameInWebInterface);
}