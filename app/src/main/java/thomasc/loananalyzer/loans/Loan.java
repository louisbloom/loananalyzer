/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.loans;

import android.os.Bundle;

import java.util.Date;
import java.util.List;

public interface Loan {

    enum LoanType {
        SIMPLE,
        PRE_CALCULATED,
        PRINCIPAL,
        RULE_OF_78,
        UNKNOWN,
    }

    enum LoanError {
        SUCCESS,
        NAME,
        PRINCIPAL,
        TERM,
        AMOUNT,
        PERIODIC_RATE,
        ANNUAL_RATE,
        FEE,
        COMPUTE,
    }

    enum IntervalType {
        YEARLY,
        MONTHLY,
        WEEKLY,
        DAILY,
    }

    enum PaymentType {
        AMOUNT,
        ANNUAL_RATE,
        PERIODIC_RATE,
    }

    Bundle toBundle();

    LoanType getLoanType();

    void setLoanType(LoanType loanType);

    PaymentType getPaymentType();

    void setPaymentType(PaymentType paymentType);

    long getId();

    void setId(long id);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    Date getCreated();

    void setCreated(Date created);

    Date getFirstPayment();

    void setFirstPayment(Date firstPayment);

    double getPrincipal();

    void setPrincipal(double principal);

    int getIntervals();

    void setIntervals(int intervals);

    /**
     * @return The {@link thomasc.loananalyzer.loans
     * .Loan.IntervalType} of the loan.
     */
    IntervalType getIntervalType();

    /**
     * @param intervalType The {@link thomasc.loananalyzer.loans
     * .Loan.IntervalType} of the loan.
     */
    void setIntervalType(IntervalType intervalType);

    int getIntervalTypeTimes();

    void setIntervalTypeTimes(int intervalTypeTimes);

    double getPeriodsPerYear();

    double getAnnualRate();

    void setAnnualRate(double annualRate);

    double getPeriodicRate();

    void setPeriodicRate(double periodicRate);

    double getAmount();

    void setAmount(double payment);

    double getPeriodicFee();

    void setPeriodicFee(double periodicFee);

    double getEap();

    List<Payment> getPayments();

    double getRebate(int n);

    double getSavings(int n);

    LoanError validate();

    LoanError compute();
}
