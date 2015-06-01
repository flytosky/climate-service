package models;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ClimateServiceRepository extends CrudRepository<ClimateService, Long> {
	List<ClimateService> findAllByName(String name);
	ClimateService findFirstByName(String oldName);
    List<ClimateService> findByOrderByCreateTimeDesc();
	// select c.*, sum(s.count) as totalcount from ClimateService c, ServiceEntry s where c.id=s.serviceId group by s.serviceId order by totalcount desc;
	@Query(value = "select c.* from ClimateService c, ServiceEntry s where c.id=s.serviceId group by s.serviceId order by sum(s.count) desc", nativeQuery = true)
	List<ClimateService> getClimateServiceOrderByCount();

	@Query(value = "select c.* from ClimateService c, ServiceEntry s where c.id=s.serviceId group by s.serviceId order by s.latestAccessTimeStamp desc", nativeQuery = true)
	List<ClimateService> getClimateServiceOrderByLatestAccessTime();

}
