package in.alekproduction.moneymanager.service;

import in.alekproduction.moneymanager.dto.ExpenseDto;
import in.alekproduction.moneymanager.enitity.Category;
import in.alekproduction.moneymanager.enitity.Expense;
import in.alekproduction.moneymanager.enitity.ProfileEntity;
import in.alekproduction.moneymanager.repository.CategoryRepo;
import in.alekproduction.moneymanager.repository.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepo categoryRepo;
    private final ExpenseRepo expenseRepo;
    private final ProfileServiceImpl profileService;

    //add expense to the database
    public ExpenseDto addExpense(ExpenseDto expenseDto){
        ProfileEntity profile = profileService.getCurrentProfile();
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }

        Long categoryId = expenseDto.getCategoryId();
        if (categoryId == null) {
            throw new RuntimeException("Category id must not be null");
        }

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + categoryId));

        Expense expense = toEntity(expenseDto, profile, category);
        Expense savedExpense = expenseRepo.save(expense);
        return toDto(savedExpense);
    }
    //Retrieves all expenses for the current month/year
    public List<ExpenseDto> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<Expense> expenses = expenseRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return expenses.stream().map(this::toDto).toList();
    }
    //delete expense by id
    public void deleteExpenseById(Long expenseId) throws Exception {
        ProfileEntity profile = profileService.getCurrentProfile();
        Expense expense = expenseRepo.findById(expenseId)
                .orElseThrow(()-> new Exception("Expense not found with id " + expenseId));
        if (!expense.getProfile().getId().equals(profile.getId())){
            throw new Exception("You are not authorized to delete this expense");
        }
        expenseRepo.deleteById(expenseId);
    }
    //Get latest top 5 expenses
    public List<ExpenseDto> getLatestTop5ExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Expense> expenses = expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return expenses.stream().map(this::toDto).toList();
    }
    //Get total expenses for current user
    public BigDecimal totalExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepo.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    //filter expenses by date range
    public List<ExpenseDto> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<Expense> expenses = expenseRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(), startDate, endDate, keyword, sort);
        return expenses.stream().map(this::toDto).toList();
    }

    //notification method
    List<ExpenseDto> getExpensesByIdAndDate(Long profileId, LocalDate date){
        List<ExpenseDto> expenseDtos = expenseRepo.findByProfileIdAndDate(profileId, date)
                .stream()
                .map(this::toDto)
                .toList();
        return expenseDtos;
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
