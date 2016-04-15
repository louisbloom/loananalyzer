/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.common;

public abstract class LoanMath {

    /**
     * Formula for calculating the unearned interest.
     *
     * <a href="https://en.wikipedia.org/wiki/Rule_of_78s">Wikipedia</a>
     *
     * @param f is the total agreed finance charges.
     * @param k is the number of months paying off early.
     * @param n is the total term of loan in months.
     * @return unearned interest.
     */
    public static double ruleOf78(double f, int k, int n) {
        return f * k * ((k + 1.0) / (n * (n + 1.0)));
    }

    /**
     * Get the present value of the annuity after <code>n</code> periods.
     *
     * <a href="http://en.wikipedia.org/wiki/Annuity_(finance_theory)#Annuity-immediate">
     *     Wikipedia
     * </a>
     *
     * @param r Periodic rate.
     * @param A Amount per period.
     * @param n Number of periods.
     *
     * @return Present Value with n periods left.
     */
    public static double PV(double r, double A, int n) {
        if (r == 0) {
            return A * n;
        }
        return A * ((1 - Math.pow(1 + r, -n)) / r);
    }

    /**
     * Get the future value of the annuity with <code>n</code> periods left.
     *
     * <a href="http://en.wikipedia.org/wiki/Annuity_(finance_theory)#Annuity-immediate">
     *     Wikipedia
     * </a>
     *
     * @param r Periodic rate.
     * @param A Amount per period.
     * @param n Number of periods.
     * @return Future Value with <code>n</code> periods left.
     */
    @SuppressWarnings("unused")
    public static double FV(double r, double A, int n) {
        return A * ((Math.pow(1 + r, n) - 1) / r);
    }

    /**
     * Calculate amount per period.
     *
     * <a href="http://en.wikipedia.org/wiki/Fixed_rate_mortgage#Pricing">
     *     Wikipedia
     * </a>
     *
     * @param r Periodic rate.
     * @param P The principal.
     * @param n Number of periods (payments).
     * @return Amount payable per period.
     */
    public static double calcAmountPerPeriod(double r, double P, int n) {
        return P * (r / (1 - Math.pow((1 + r), -n)));
    }

    /**
     * <a href="http://math.stackexchange.com/questions/724469">
     *     Stack Exchange
     * </a>
     *
     * @param r Periodic rate.
     * @param P The principal.
     * @param n Number of periods (payments).
     * @return Amount payable per period.
     */
    @SuppressWarnings("unused")
    public static double calcPaymentPerPeriodSE(double r, double P, int n) {
        return P * ((r * Math.pow(r + 1, n) / (Math.pow(r + 1, n) - 1)));
    }

    /**
     * <a href="http://math.stackexchange.com/questions/724469">
     *     Stack Exchange
     * </a>
     */
    private static double calcNewton(double r, double P, double A, int n) {
        return P * ((r * Math.pow(r + 1, n)) / (Math.pow(r + 1, n) - 1)) - A;
    }

    /**
     * <a href="http://math.stackexchange.com/questions/724469">
     *     Stack Exchange
     * </a>
     */
    private static double calcNewtonDerivative(double r, double P, int n) {
        return P * ((Math.pow(r + 1, n - 1) * (Math.pow(r + 1, n + 1) - (n * r) - r - 1)) / (Math.pow(Math.pow(r + 1, n) - 1, 2)));
    }

    /**
     * <a href="http://math.stackexchange.com/questions/724469">
     *     Stack Exchange
     * </a>
     */
    private static double calcInitialGuess(double P, double A, int n) {
        double k = P / A;
        return (2 * (n - k) * (2 * k * (n + 2) + (n - 1) * n)) / (((k * k) * (n + 2) * (n + 3)) + (2 * k * n * ((n * n) + n - 2) + ((1 - n) * (n * n))));
    }

    /**
     * <a href="http://math.stackexchange.com/questions/724469">
     *     Stack Exchange
     * </a>
     */
    public static double calcPeriodicRate(double P, double A, int n) {
        double r = calcInitialGuess(P, A, n);

        // Use Newton approximation twice
        double v;
        double v_;

        if (r > 0) {
            v = calcNewton(r, P, A, n);
            v_ = calcNewtonDerivative(r, P, n);
            r -= v / v_;

            v = calcNewton(r, P, A, n);
            v_ = calcNewtonDerivative(r, P, n);
            r -= v / v_;
        }

        return r;
    }
}
