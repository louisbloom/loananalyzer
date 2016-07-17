/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.loans;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import thomasc.loananalyzer.common.LoanMath;
import thomasc.loananalyzer.db.LoanReaderContract.LoanEntry;

public class BaseLoan implements Loan {

    private long id = -1;
    private LoanType loanType = LoanType.SIMPLE;
    private String name = "";
    private String description = "";
    private Date created = Calendar.getInstance().getTime();
    private Date firstPayment = Calendar.getInstance().getTime();
    private double principal = 0;
    private IntervalType intervalType = IntervalType.MONTHLY;
    private int intervalTypeTimes = 1;
    private int intervals = 1;
    private PaymentType paymentType = PaymentType.AMOUNT;
    private double amount = 0;
    private double rate = 0;
    private double prate = 0;
    private double periodicFee = 0;

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putLong(LoanEntry._ID, getId());
        bundle.putInt(LoanEntry.COLUMN_TYPE, getLoanType().ordinal());
        bundle.putString(LoanEntry.COLUMN_NAME, getName());
        bundle.putString(LoanEntry.COLUMN_DESCRIPTION, getDescription());
        bundle.putLong(LoanEntry.COLUMN_CREATED, getCreated().getTime());
        bundle.putLong(LoanEntry.COLUMN_FIRST_PAYMENT, getFirstPayment().getTime());
        bundle.putDouble(LoanEntry.COLUMN_PRINCIPAL, getPrincipal());
        bundle.putInt(LoanEntry.COLUMN_INTERVAL_TYPE, getIntervalType().ordinal());
        bundle.putInt(LoanEntry.COLUMN_INTERVAL_TYPE_TIMES, getIntervalTypeTimes());
        bundle.putInt(LoanEntry.COLUMN_INTERVALS, getIntervals());
        bundle.putInt(LoanEntry.COLUMN_PAYMENT_TYPE, getPaymentType().ordinal());
        bundle.putDouble(LoanEntry.COLUMN_AMOUNT, getAmount());
        bundle.putDouble(LoanEntry.COLUMN_RATE, getAnnualRate());
        bundle.putDouble(LoanEntry.COLUMN_PRATE, getPeriodicRate());
        bundle.putDouble(LoanEntry.COLUMN_PERIODIC_FEE, getPeriodicFee());

        return bundle;
    }

    /**
     * Create an instance of the right loan class from the loan type.
     *
     * @param loan A BaseLoan with the loan type set.
     * @return a loan of the class matching the loan type.
     */
    @NonNull
    public static Loan retype(@NonNull Loan loan) {
        Bundle bundle = loan.toBundle();
        return valueOf(bundle);
    }

    /**
     * Create a new loan from a loan type.
     *
     * @param type the loan type
     * @return a new loan of the class corresponding to the loan type.
     */
    @NonNull
    private static Loan newInstance(@NonNull LoanType type) {
        Loan loan;

        switch (type) {
            case SIMPLE:
                loan = new SimpleLoan();
                break;

            case PRE_CALCULATED:
                loan = new PrecomputedLoan();
                break;

            case PRINCIPAL:
                loan = new PrincipalLoan();
                break;

            case RULE_OF_78:
                loan = new Rule78Loan();
                break;

            default:
                loan = new SimpleLoan();
                break;
        }

        return loan;
    }

    @NonNull
    public static Loan valueOf(@NonNull Bundle bundle) {
        int i;
        long l;

        // TODO Validate bundle if it has all the keys needed?
        i = bundle.getInt(LoanEntry.COLUMN_TYPE);
        Loan loan = newInstance(LoanType.values()[i]);

        loan.setId(bundle.getLong(LoanEntry._ID));
        loan.setName(bundle.getString(LoanEntry.COLUMN_NAME));
        loan.setDescription(bundle.getString(LoanEntry.COLUMN_DESCRIPTION));

        l = bundle.getLong(LoanEntry.COLUMN_CREATED);
        loan.setCreated(new Date(l));

        l = bundle.getLong(LoanEntry.COLUMN_FIRST_PAYMENT);
        loan.setFirstPayment(new Date(l));
        loan.setPrincipal(bundle.getDouble(LoanEntry.COLUMN_PRINCIPAL));

        i = bundle.getInt(LoanEntry.COLUMN_INTERVAL_TYPE);
        loan.setIntervalType(IntervalType.values()[i]);
        loan.setIntervalTypeTimes(bundle.getInt(LoanEntry.COLUMN_INTERVAL_TYPE_TIMES));
        loan.setIntervals(bundle.getInt(LoanEntry.COLUMN_INTERVALS));

        i = bundle.getInt(LoanEntry.COLUMN_PAYMENT_TYPE);
        loan.setPaymentType(PaymentType.values()[i]);
        loan.setAmount(bundle.getDouble(LoanEntry.COLUMN_AMOUNT));
        loan.setAnnualRate(bundle.getDouble(LoanEntry.COLUMN_RATE));
        loan.setPeriodicRate(bundle.getDouble(LoanEntry.COLUMN_PRATE));
        loan.setPeriodicFee(bundle.getDouble(LoanEntry.COLUMN_PERIODIC_FEE));

        return loan;
    }

    @NonNull
    public static Loan valueOf(@NonNull Cursor cursor) {
        int i;

        i = cursor.getColumnIndex(LoanEntry.COLUMN_TYPE);
        i = cursor.getInt(i);
        Loan loan = newInstance(LoanType.values()[i]);

        i = cursor.getColumnIndex(LoanEntry._ID);
        loan.setId(cursor.getLong(i));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_NAME);
        loan.setName(cursor.getString(i));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_DESCRIPTION);
        loan.setDescription(cursor.getString(i));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_CREATED);
        loan.setCreated(new Date(cursor.getLong(i)));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_FIRST_PAYMENT);
        loan.setFirstPayment(new Date(cursor.getLong(i)));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_PRINCIPAL);
        loan.setPrincipal(cursor.getDouble(i));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_INTERVAL_TYPE);
        i = cursor.getInt(i);
        loan.setIntervalType(IntervalType.values()[i]);

        i = cursor.getColumnIndex(LoanEntry.COLUMN_INTERVAL_TYPE_TIMES);
        loan.setIntervalTypeTimes(cursor.getInt(i));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_INTERVALS);
        loan.setIntervals(cursor.getInt(i));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_PAYMENT_TYPE);
        i = cursor.getInt(i);
        loan.setPaymentType(PaymentType.values()[i]);

        i = cursor.getColumnIndex(LoanEntry.COLUMN_AMOUNT);
        loan.setAmount(cursor.getDouble(i));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_RATE);
        loan.setAnnualRate(cursor.getDouble(i));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_PRATE);
        loan.setPeriodicRate(cursor.getDouble(i));

        i = cursor.getColumnIndex(LoanEntry.COLUMN_PERIODIC_FEE);
        loan.setPeriodicFee(cursor.getDouble(i));

        return loan;
    }

    @Override
    public PaymentType getPaymentType() {
        return paymentType;
    }

    @Override
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public LoanType getLoanType() {
        return loanType;
    }

    @Override
    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getCreated() {
        return created;
    }

    @Override
    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public Date getFirstPayment() {
        return firstPayment;
    }

    @Override
    public void setFirstPayment(Date firstPayment) {
        this.firstPayment = firstPayment;
    }

    @Override
    public double getPrincipal() {
        return principal;
    }

    @Override
    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    @Override
    public int getIntervals() {
        return intervals;
    }

    @Override
    public void setIntervals(int intervals) {
        this.intervals = intervals;
    }

    @Override
    public IntervalType getIntervalType() {
        return intervalType;
    }

    @Override
    public void setIntervalType(IntervalType intervalType) {
        this.intervalType = intervalType;
    }

    @Override
    public void setIntervalTypeTimes(int intervalTypeTimes) {
        this.intervalTypeTimes = intervalTypeTimes;
    }

    @Override
    public int getIntervalTypeTimes() {
        return intervalTypeTimes;
    }

    @Override
    public double getPeriodsPerYear() {
        switch (getIntervalType()) {
            case YEARLY:
                return 1.0 / getIntervalTypeTimes();

            case MONTHLY:
                return 12.0 / getIntervalTypeTimes();

            case WEEKLY:
                return  52.0 / getIntervalTypeTimes();

            case DAILY:
                return 365.0 / getIntervalTypeTimes();

            default:
                return 12;
        }
    }

    @Override
    public double getAnnualRate() {
        return rate;
    }

    @Override
    public void setAnnualRate(double annualRate) {
        rate = annualRate;
    }

    @Override
    public double getPeriodicRate() {
        return prate;
    }

    @Override
    public void setPeriodicRate(double periodicRate) {
        prate = periodicRate;
    }

    @Override
    public double getAmount() {
        return amount;
    }

    @Override
    public void setAmount(double payment) {
        this.amount = payment;
    }

    @Override
    public double getPeriodicFee() {
        return periodicFee;
    }

    @Override
    public void setPeriodicFee(double periodicFee) {
        this.periodicFee = periodicFee;
    }

    @Override
    public double getEap() {
        return LoanMath.rateToApr(getAnnualRate(), (int) getPeriodsPerYear());
    }

    @Override
    public LoanError validate() {
        return validate(false);
    }

    protected LoanError validate(boolean strict) {
        if (strict && getName().trim().equals("")) {
            return LoanError.NAME;
        }

        if (getPrincipal() <= 0) {
            return LoanError.PRINCIPAL;
        }

        if (getIntervals() < 1) {
            return LoanError.TERM;
        }

        if (getIntervalTypeTimes() < 1) {
            return LoanError.TERM;
        }

        switch (getPaymentType()) {
            case AMOUNT:
                if (getAmount() <= 0) {
                    return LoanError.AMOUNT;
                }
                break;

            case ANNUAL_RATE:
                if (getAnnualRate() < 0) {
                    return LoanError.ANNUAL_RATE;
                }
                break;

            case PERIODIC_RATE:
                if (getPeriodicRate() < 0) {
                    return LoanError.PERIODIC_RATE;
                }
                break;
        }

        if (getPeriodicFee() < 0) {
            return LoanError.FEE;
        }

        return LoanError.SUCCESS;
    }

    @Override
    public LoanError compute() {
        return null;
    }

    @Override
    public List<Payment> getPayments() {
        return null;
    }

    /**
     * Rebate on balance by repaying in full after n periods.
     *
     * @param n Number of periods.
     * @return Rebate.
     */
    @Override
    public double getRebate(int n) {
        return 0;
    }

    /**
     * Interest and fee saved by repaying in full after n periods.
     *
     * @param n Number of periods.
     * @return Interest and fee saved.
     */
    @Override
    public double getSavings(int n) {
        // -1 because when paying of the fee is also applied.
        int k = getIntervals() - n - 1;
        return k * getPeriodicFee();
    }
}
