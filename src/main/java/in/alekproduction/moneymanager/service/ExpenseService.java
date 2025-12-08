package in.alekproduction.moneymanager.service;

import in.alekproduction.moneymanager.dto.ExpenseDto;
import in.alekproduction.moneymanager.enitity.Category;
import in.alekproduction.moneymanager.enitity.Expense;
import in.alekproduction.moneymanager.enitity.ProfileEntity;
import in.alekproduction.moneymanager.repository.CategoryRepo;
import in.alekproduction.moneymanager.repository.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepo categoryRepo;
    private final ExpenseRepo expenseRepo;
    private final ProfileServiceImpl profileService;

    //add expense to the database
    public ExpenseDto addExpense(ExpenseDto expenseDto){
        ProfileEntity profile = profileService.getCurrentProfile();
        Category category = categoryRepo.findById(expenseDto.getId())
                .orElseThrow(()-> new RuntimeException("Category not found"));
        Expense expense = toEntity(expenseDto, profile, category);
        Expense savedExpense = expenseRepo.save(expense);
        return toDto(savedExpense);

    }


    //helper methods
    private Expense toEntity(ExpenseDto dto, ProfileEntity profile, Category category){
        return Expense.builder()
                .name(dto.getName())
                .amount(dto.getAmount())
                .profile(profile)
                .category(category)
                .date(dto.getDate())
                .icon(dto.getIcon())
                .build();
    }
    private ExpenseDto toDto(Expense expense){
        return ExpenseDto.builder()
                .id(expense.getId())
                .name(expense.getName())
                .amount(expense.getAmount())
                .icon(expense.getIcon())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : null)
                .date(expense.getDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
