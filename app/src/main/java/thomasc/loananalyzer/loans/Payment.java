/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.loans;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Date;

import thomasc.loananalyzer.db.LoanReaderContract.PaymentEntry;

public class Payment {

    // Database fields
    private Date date = null;
    private double amount = 0;

    // Non database fields
    private int no = -1;
    private double interest = 0;
    private double balance = 0;

    @NonNull
    public static Payment valueOf(@NonNull Bundle bundle) {
        Payment payment = new Payment();

        payment.setDate(new Date(bundle.getLong(PaymentEntry.COLUMN_DATE)));
        payment.setAmount(bundle.getDouble(PaymentEntry.COLUMN_AMOUNT));

        return payment;
    }

    @NonNull
    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putLong(PaymentEntry.COLUMN_DATE, getDate().getTime());
        bundle.putDouble(PaymentEntry.COLUMN_AMOUNT, getAmount());

        return bundle;
    }

    public int getNo() {
        return no;
    }
    public void setNo(int no) {
        this.no = no;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public double getInterest() {
        return interest;
    }
    public void setInterest(double paymentInterest) {
        this.interest = paymentInterest;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
