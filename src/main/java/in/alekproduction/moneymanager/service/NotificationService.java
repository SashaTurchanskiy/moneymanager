package in.alekproduction.moneymanager.service;

import in.alekproduction.moneymanager.enitity.ProfileEntity;
import in.alekproduction.moneymanager.repository.ProfileRepo;
import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    }
}
