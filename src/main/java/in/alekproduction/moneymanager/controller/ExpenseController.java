package in.alekproduction.moneymanager.controller;

import in.alekproduction.moneymanager.dto.ExpenseDto;
import in.alekproduction.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
