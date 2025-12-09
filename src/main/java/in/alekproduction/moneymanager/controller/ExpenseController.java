package in.alekproduction.moneymanager.controller;

import in.alekproduction.moneymanager.dto.ExpenseDto;
import in.alekproduction.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/add")
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto expenseDto){
        ExpenseDto savedExpense = expenseService.addExpense(expenseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }
    @GetMapping("/getCurrentMonthExpenses")
    public ResponseEntity<List<ExpenseDto>> getExpenses(){
        List<ExpenseDto> expenseDtos = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenseDtos);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) throws Exception {
        expenseService.deleteExpenseById(id);
        return ResponseEntity.noContent().build();
    }
}
