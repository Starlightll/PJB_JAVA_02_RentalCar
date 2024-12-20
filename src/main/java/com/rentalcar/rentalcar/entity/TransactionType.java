package com.rentalcar.rentalcar.entity;

public class TransactionType {
    public static final String WITHDRAW = "Withdraw";
    public static final String TOP_UP = "Top-up";
    public static final String PAY_DEPOSIT = "Pay deposit";
    public static final String RECEIVE_DEPOSIT = "Receive deposit";
    public static final String REFUND_DEPOSIT = "Refund deposit";
    public static final String OFFSET_FINAL_PAYMENT = "Offset final payment";

    public static final String OFFSET_FINAL_PAYMENT_CUSTOMER = "Pay final payment"; //trừ tiền vào ví customer
    public static final String OFFSET_FINAL_PAYMENT_CAR_OWNER = "Receive final payment"; //cộng tiền ví car owner

    public static final String OFFSET_FINAL_BACK_REMAIN_DEPOSIT = "Receive remaining deposit"; //customer nhận tiền dư cọc về ví +
    public static final String OFFSET_FINAL_PAYMENT_BACK_DEPOSIT = "Return remaining deposit"; //car owner trả tiền cọc dư

    public static final String OFFSET_FINAL_PAYMENT_RENTAL_DRIVER = "Pay for driver rental"; //pay rental to driver
    public static final String OFFSET_FINAL_RECEIVE_SALARY = "Receive salary";//pay rental to driver


}
