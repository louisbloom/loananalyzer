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

/**
 * An simple annuity loan.
 * <p/>
 * see
 * <a href="http://www.in.gov/dfi/2602.htm">http://www.in.gov/dfi/2602.htm</a>.
 */
public class SimpleLoan extends BaseLoan {

    @SuppressWarnings("unused")
    private static final String TAG = "SimpleLoan";

    public SimpleLoan() {
        super();
        setLoanType(LoanType.SIMPLE);
    }

    @Override
    public LoanError compute() {
        LoanError error = validate();

        if (error != LoanError.SUCCESS) {
            return error;
        }

        switch (getPaymentType()) {
            case AMOUNT:
                setAnnualRate(LoanMath.calcPeriodicRate(
                        getPrincipal(),
                        getAmount(),
                        getIntervals()
                        ) * getPeriodsPerYear());
                setPeriodicRate(getAnnualRate() / getPeriodsPerYear());
                break;

            case ANNUAL_RATE:
                double r = getAnnualRate() / getPeriodsPerYear();
                setAmount(LoanMath.calcAmountPerPeriod(
                        r,
                        getPrincipal(),
                        getIntervals()));
                setPeriodicRate(getAnnualRate() / getPeriodsPerYear());
                break;

            case PERIODIC_RATE:
                setAnnualRate(getPeriodicRate() * getPeriodsPerYear());
                setAmount(LoanMath.calcAmountPerPeriod(
                        getPeriodicRate(),
                        getPrincipal(),
                        getIntervals()));
                break;
        }

        setEap(LoanMath.calcPeriodicRate(
                getPrincipal(),
                getAmount() + getPeriodicFee(),
                getIntervals()) * getPeriodsPerYear());

        return LoanError.SUCCESS;
    }

    @Override
    public List<Payment> getPayments() {
        ArrayList<Payment> payments = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getFirstPayment());

        double balance = 0;

        for (int no = 0; no <= getIntervals(); no++) {
            Payment p = new Payment();

            p.setNo(no);

            if (p.getNo() == 0) {
                p.setDate(new Date(0));
                p.setAmount(0);
                p.setInterest(0);
            } else {
                p.setDate(calendar.getTime());
                p.setAmount(getAmount());
                p.setInterest(balance * getPeriodicRate());
            }

            balance = LoanMath.PV(
                    getPeriodicRate(),
                    getAmount(),
                    getIntervals() - no);

            p.setBalance(balance);

            payments.add(p);

            if (p.getNo() > 0) {
                LoanUtils.calendarAdd(calendar, getIntervalType(),
                        getIntervalTypeTimes());
            }
        }

        return payments;
    }

    @Override
    public double getSavings(int n) {
        int k = getIntervals() - n;

        return super.getSavings(n) + (k * getAmount()) - LoanMath.PV(
                getPeriodicRate(),
                getAmount(),
                k);
    }
}
