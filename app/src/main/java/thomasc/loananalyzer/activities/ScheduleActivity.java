/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.db.LoanReaderContract.LoanEntry;
import thomasc.loananalyzer.db.LoanReaderUtils;
import thomasc.loananalyzer.fragments.DetailsFragment;
import thomasc.loananalyzer.fragments.ScheduleFragment;
import thomasc.loananalyzer.loans.Payment;

public class ScheduleActivity extends AppCompatActivity implements
        ScheduleFragment.OnFragmentInteractionListener,
        DetailsFragment.OnFragmentInteractionListener {

    private DetailsFragment details = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule);

        // App bar state is not saved, so it has to be recreated on every
        // onCreate()
        long loanId = getIntent().getLongExtra(LoanEntry._ID, -1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // The fragments are saved so only create them initially.
        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            ScheduleFragment fragment = ScheduleFragment.newInstance(loanId);
            transaction.add(R.id.fragment_holder1, fragment);

            details = DetailsFragment.newInstance(loanId);
            transaction.add(R.id.fragment_holder2, details);

            transaction.commit();
        } else {
            details = (DetailsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_holder2);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.schedule, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPaymentSelected(Payment payment) {
        if (details != null) {
            details.setPayment(payment);
        }
    }

    @Override
    public void onEdit(long loanId) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(LoanEntry._ID, loanId);
        startActivity(intent);
    }

    @Override
    public void onDelete(long loanId) {
        new LoanReaderUtils(this).deleteLoan(loanId);
        finish();
    }
}
