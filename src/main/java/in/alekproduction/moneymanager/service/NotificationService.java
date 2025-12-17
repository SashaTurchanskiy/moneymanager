package in.alekproduction.moneymanager.service;

import in.alekproduction.moneymanager.dto.ExpenseDto;
import in.alekproduction.moneymanager.enitity.ProfileEntity;
import in.alekproduction.moneymanager.repository.ExpenseRepo;
import in.alekproduction.moneymanager.repository.ProfileRepo;
import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final ProfileRepo profileRepo;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    //@Scheduled(cron = "0 * * * * ?", zone = "UTC")
    @Scheduled(cron = "0 0 22 * * ?", zone = "UTC")
    public void sendDailyIncomeExpenseReminder(){
         log.info("Starting daily income/expense reminder job");
         List<ProfileEntity> profiles = profileRepo.findAll();
         for (ProfileEntity profile : profiles) {
             String body = "Dear " + profile.getFullName() + ",\n\n" +
                     "This is a friendly reminder to log your income and expenses for today.\n" +
                     "Keeping track of your finances is important for effective money management.\n\n" +
                     "You can log your income and expenses by visiting the following link:\n" +
                     frontendUrl + "/dashboard\n\n" +
                     "Thank you for using our Money Manager application!\n\n" +
                     "Best regards,\n" +
                     "Money Manager Team";
                emailService.sendEmail(profile.getEmail(), "Daily Income/Expense Reminder", body);
         }
            log.info("Daily income/expense reminder job completed");
    }

    //@Scheduled(cron = "0 * * * * ?", zone = "UTC")
    @Scheduled(cron = "0 0 23 * * ?", zone = "UTC")
    public void sendDailyExpenseSummary(){
        log.info("Job started: Sending daily expense summary emails to all users.");
        List<ProfileEntity> profiles = profileRepo.findAll();
        for (ProfileEntity profile : profiles){
            List<ExpenseDto> todayExpenses = expenseService.getExpensesByIdAndDate(profile.getId(), LocalDate.now());
            if (!todayExpenses.isEmpty()){
                StringBuilder table = new StringBuilder();
                table.append("<table border='1' style='border-collapse: collapse;'>");
                table.append("<tr style='background-color: #f2f2f2;'><th style='padding: 8px;'>Name</th><th style='padding: 8px;'>Category</th><th style='padding: 8px;'>Amount</th></tr>");
                int i = 0;
                for (ExpenseDto expense : todayExpenses) {
                    table.append("<tr>");
                    table.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(i++).append("</td>");
                    table.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getName()).append("</td>");
                    table.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getAmount()).append("</td>");
                    table.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");
                String body = "Dear " + profile.getFullName() + ",<br><br>" +
                        "Here is the summary of your expenses for today:<br><br>" +
                        table.toString() +
                        "<br>Thank you for using our Money Manager application!<br><br>" +
                        "Best regards,<br>" +
                        "Money Manager Team";
                emailService.sendEmail(profile.getEmail(), "Daily Expense Summary", body);
            }
        }
            log.info("Job completed: Daily expense summary emails sent to all users.");
    }

}
