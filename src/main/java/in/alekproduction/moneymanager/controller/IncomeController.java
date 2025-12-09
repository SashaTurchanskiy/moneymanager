package in.alekproduction.moneymanager.controller;

import in.alekproduction.moneymanager.dto.IncomeDto;
import in.alekproduction.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping("/add")
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto incomeDto){
        IncomeDto savedIncome = incomeService.addIncome(incomeDto);
        return ResponseEntity.status(201).body(savedIncome);
    }
    @GetMapping("/getCurrentMonthIncomes")
    public ResponseEntity<List<IncomeDto>> getIncomes(){
        List<IncomeDto> incomeDtos = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomeDtos);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) throws Exception {
        incomeService.deleteIncomeById(id);
        return ResponseEntity.noContent().build();
    }
}
