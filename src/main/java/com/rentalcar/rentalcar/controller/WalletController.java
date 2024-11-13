package com.rentalcar.rentalcar.controller;

import com.rentalcar.rentalcar.entity.User;
import com.rentalcar.rentalcar.entity.Transaction;
import com.rentalcar.rentalcar.repository.UserRepo;
import com.rentalcar.rentalcar.repository.TransactionRepository;
import com.rentalcar.rentalcar.service.MyWalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private MyWalletService myWalletService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @PostMapping("/top-up")
    @ResponseBody
    public Map<String, Object> processTopUp(HttpSession session, @RequestParam BigDecimal amount) {
        Map<String, Object> response = new HashMap<>();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            response.put("success", false);
            response.put("message", "Invalid amount!");
            return response;
        }

        User user = (User) session.getAttribute("user");
        user = userRepo.getUserByEmail(user.getEmail());

        myWalletService.topUp(user.getId(), amount);


        String formattedWallet = formatWallet(user.getWallet());

        response.put("success", true);
        response.put("message", "Top-up success!");
        response.put("newBalance", formattedWallet);
        return response;
    }

    @PostMapping("/withdraw")
    @ResponseBody
    public Map<String, Object> processWithdraw(HttpSession session, @RequestParam BigDecimal amount) {
        Map<String, Object> response = new HashMap<>();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            response.put("success", false);
            response.put("message", "Invalid amount");
            return response;
        }

        User user = (User) session.getAttribute("user");
        try {
            user = userRepo.getUserByEmail(user.getEmail());

            myWalletService.withdraw(user.getId(), amount);


            String formattedWallet = formatWallet(user.getWallet());

            response.put("success", true);
            response.put("message", "Withdraw success!");
            response.put("newBalance", formattedWallet);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Your account doesn't have enough money!");
        }

        return response;
    }

    @GetMapping("/my-wallet")
    public String myWallet(Model model, HttpSession session,
                           @RequestParam(value = "fromDate", required = false) String fromDate,
                           @RequestParam(value = "toDate", required = false) String toDate,
                           @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        User user = (User) session.getAttribute("user");
        if (user.getWallet() == null) {
            user.setWallet(BigDecimal.valueOf(0));
            userRepo.save(user);
        }
        user = userRepo.getUserByEmail(user.getEmail());
        String formattedWallet = formatWallet(user.getWallet());

        Pageable pageable = PageRequest.of(page - 1, 5);
        Page<Transaction> transactions = Page.empty();

        try {
            if (fromDate != null && toDate != null) {
                LocalDate from = LocalDate.parse(fromDate);
                LocalDate to = LocalDate.parse(toDate);

                LocalDateTime fromDateTime = from.atStartOfDay();
                LocalDateTime toDateTime = to.atTime(23, 59, 59);

                if (fromDateTime.isAfter(toDateTime)) {
                    model.addAttribute("error", "The end date must be later than the start date.");
                } else {
                    transactions = transactionRepo.findByUserAndTransactionDateBetween(user, fromDateTime, toDateTime, pageable);
                    model.addAttribute("fromDate", fromDate);
                    model.addAttribute("toDate", toDate);
                }
            } else {
                transactions = transactionRepo.findByUser(user, pageable);
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Invalid date format. Please use yyyy-MM-dd.");
        }

        model.addAttribute("transactions", transactions.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactions.getTotalPages());
        model.addAttribute("formattedWallet", formattedWallet);
        model.addAttribute("user", user);

        return "UserManagement/wallet/my-wallet";
    }


    private String formatWallet(BigDecimal wallet) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        DecimalFormat formatter = new DecimalFormat("###,###", symbols);
        return wallet != null ? formatter.format(wallet) : "0";
    }
}
