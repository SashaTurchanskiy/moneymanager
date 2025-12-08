package in.alekproduction.moneymanager.repository;

import in.alekproduction.moneymanager.enitity.Expense;
import in.alekproduction.moneymanager.enitity.Income;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepo extends JpaRepository<Income, Long> {


    //select * from tbl_incomes where profile_id = ?1 order by date desc
    List<Income> findByProfileIdOrderByDateDesc(Long profileId);

    //select * from tbl_incomes where profile_id = ?1 order by date desc limit 5
    List<Income> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    //select * from tbl_incomes where profile_id = ?1 and date between ?2 and ?3 and name like %?4%
    List<Income> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    //select * from tbl_incomes where profile_id = ?1 and date between ?2 and ?3
    List<Income> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    

}
