/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import thomasc.loananalyzer.BuildConfig;
import thomasc.loananalyzer.R;
import thomasc.loananalyzer.db.LoanReaderContract.LoanEntry;
import thomasc.loananalyzer.fragments.LoansFragment;
import thomasc.loananalyzer.loans.Loan;

public class LoansActivity extends AppCompatActivity implements
        LoansFragment.OnLoanActionListener {

    private static final String TAG = "LoansActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loans);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(
                R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        LoansActivity.this,
                        EditorActivity.class);
                startActivity(intent);
            }
        });

        if (BuildConfig.DEBUG) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels /
                    displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            Log.d(TAG, String.format("Display = %dx%d (dp)", (int) dpWidth,
                    (int) dpHeight));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onView(Loan loan) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra(LoanEntry._ID, loan.getId());
        startActivity(intent);
    }
}
