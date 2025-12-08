package in.alekproduction.moneymanager.controller;

import in.alekproduction.moneymanager.dto.IncomeDto;
import in.alekproduction.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping("/add")
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto incomeDto){
        IncomeDto savedIncome = incomeService.addExpense(incomeDto);
        return ResponseEntity.status(201).body(savedIncome);
    }
}
