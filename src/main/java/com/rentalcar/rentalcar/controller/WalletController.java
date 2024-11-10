package com.rentalcar.rentalcar.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/wallet")
public class WalletController {


    @GetMapping("/top-up")
    public String topUp() {
        return "UserManagement/wallet/top-up";
    }

    @GetMapping("/withdraw")
    public String withdraw() {
        return "UserManagement/wallet/withdraw";
    }

    @GetMapping("my-wallet")
    public String myWallet() {
        return "UserManagement/wallet/my-wallet";
    }

}
