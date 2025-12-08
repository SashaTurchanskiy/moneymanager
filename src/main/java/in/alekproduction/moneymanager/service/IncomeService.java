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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepo categoryRepo;
    private final IncomeRepo incomeRepo;
    private final ProfileServiceImpl profileService;

    //add expense to the database
    public IncomeDto addExpense(IncomeDto incomeDto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        Category category = categoryRepo.findById(incomeDto.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Income income = toEntity(incomeDto, profile, category);
        Income savedIncome = incomeRepo.save(income);
        return toDto(savedIncome);
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
