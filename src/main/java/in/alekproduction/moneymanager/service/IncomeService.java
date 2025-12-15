package in.alekproduction.moneymanager.service;

import in.alekproduction.moneymanager.dto.ExpenseDto;
import in.alekproduction.moneymanager.dto.IncomeDto;
import in.alekproduction.moneymanager.enitity.Category;
import in.alekproduction.moneymanager.enitity.Expense;
import in.alekproduction.moneymanager.enitity.Income;
import in.alekproduction.moneymanager.enitity.ProfileEntity;
import in.alekproduction.moneymanager.repository.CategoryRepo;
import in.alekproduction.moneymanager.repository.ExpenseRepo;
import in.alekproduction.moneymanager.repository.IncomeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepo categoryRepo;
    private final IncomeRepo incomeRepo;
    private final ProfileServiceImpl profileService;

    //add income to the database
    public IncomeDto addIncome(IncomeDto incomeDto){
        ProfileEntity profile = profileService.getCurrentProfile();
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }

        Long categoryId = incomeDto.getCategoryId();
        if (categoryId == null) {
            throw new RuntimeException("Category id must not be null");
        }

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));

        Income income = toEntity(incomeDto, profile, category);
        Income savedIncome = incomeRepo.save(income);
        return toDto(savedIncome);
    }
    //Retrieves all incomes for the current month/year
    public List<IncomeDto> getCurrentMonthIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Income> incomes = incomeRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return incomes.stream().map(this::toDto).toList();
    }
    //delete income by id
    public void deleteIncomeById(Long incomeId) throws Exception {
        ProfileEntity profile = profileService.getCurrentProfile();
        Income income = incomeRepo.findById(incomeId)
                .orElseThrow(()-> new Exception("Income not found with id " + incomeId));
        if (!income.getProfile().getId().equals(profile.getId())){
            throw new Exception("You are not authorized to delete this income");
        }
        incomeRepo.deleteById(incomeId);
    }
    //Get latest top 5 incomes
    public List<IncomeDto> getLatestTop5IncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Income> incomes = incomeRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return incomes.stream().map(this::toDto).toList();
    }
    //Get total expenses for current user
    public BigDecimal getTotalIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepo.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    //filter incomes by date range
    public List<IncomeDto> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Income> incomes = incomeRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(), startDate, endDate, keyword, sort);
        return incomes.stream().map(this::toDto).toList();
    }


    //helper methods
    private Income toEntity(IncomeDto dto, ProfileEntity profile, Category category){
        return Income.builder()
                .name(dto.getName())
                .amount(dto.getAmount())
                .profile(profile)
                .category(category)
                .date(dto.getDate())
                .icon(dto.getIcon())
                .build();
    }
    private IncomeDto toDto(Income income){
        return IncomeDto.builder()
                .id(income.getId())
                .name(income.getName())
                .amount(income.getAmount())
                .icon(income.getIcon())
                .categoryId(income.getCategory() != null ? income.getCategory().getId() : null)
                .categoryName(income.getCategory() != null ? income.getCategory().getName() : null)
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}
