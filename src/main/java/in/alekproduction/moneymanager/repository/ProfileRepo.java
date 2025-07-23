package in.alekproduction.moneymanager.repository;

import in.alekproduction.moneymanager.enitity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepo extends JpaRepository<ProfileEntity, Long> {

    //select * from tbl_profile where email = ?1
    Optional<ProfileEntity> findByEmail(String email);

    //select * from tbl_profile where activation_token = ?1
    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
