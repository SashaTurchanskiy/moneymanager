package in.alekproduction.moneymanager.repository;

import in.alekproduction.moneymanager.enitity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    //select * from tbl_categories where profile_id=?1
    List<Category> findByProfileId(Long profileId);

    //select * from tbl_categories where id=?1 and profile_id=?2
    Optional<Category> findByIdAndProfileId(Long id, Long profileId);

    //select * from tbl_categories where type=?1 and profile_id=?2
    List<Category> findByTypeAndProfileId(String type, Long profileId);

    //select case when count(c)> 0 then true else false end from Category c where c.name=?1 and c.profile.id=?2
    boolean existsByNameAndProfileId(String name, Long profileId);

}







