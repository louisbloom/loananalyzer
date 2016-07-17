/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.loans;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import thomasc.loananalyzer.common.LoanMath;
import thomasc.loananalyzer.common.LoanUtils;

public class PrincipalLoan extends BaseLoan {

    public PrincipalLoan() {
        super();
        setLoanType(LoanType.PRINCIPAL);
    }

    @Override
    public LoanError compute() {
        LoanError error = validate();

        if (error != LoanError.SUCCESS) {
            return error;
        }

        switch (getPaymentType()) {
            case AMOUNT:
                double f = (getIntervals() * getAmount()) - getPrincipal();
                setPeriodicRate(f / getIntervals() / getPrincipal());
                setAnnualRate(getPeriodicRate() * getPeriodsPerYear());
                break;

            case ANNUAL_RATE:
                setPeriodicRate(getAnnualRate() / getPeriodsPerYear());
                setAmount((getPrincipal() / getIntervals()) +
                          (getPrincipal() * getPeriodicRate()));
                break;

            case PERIODIC_RATE:
                setAnnualRate(getPeriodicRate() * getPeriodsPerYear());
                setAmount((getPrincipal() / getIntervals()) +
                          (getPrincipal() * getPeriodicRate()));
                break;
        }

        return LoanError.SUCCESS;
    }

    @Override
    public List<Payment> getPayments() {
        ArrayList<Payment> payments = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getFirstPayment());

        double balance = getAmount() * getIntervals();

        for (int no = 0; no <= getIntervals(); no++) {
            Payment p = new Payment();

            p.setNo(no);

            if (p.getNo() == 0) {
                p.setDate(new Date(0));
                p.setAmount(0);
            } else {
                p.setDate(calendar.getTime());
                p.setAmount(getAmount());
            }

            p.setInterest(0);
            p.setBalance(balance - p.getNo() * getAmount());

            payments.add(p);

            if (p.getNo() > 0) {
                LoanUtils.calendarAdd(calendar, getIntervalType(),
                        getIntervalTypeTimes());
            }
        }

        return payments;
    }
}