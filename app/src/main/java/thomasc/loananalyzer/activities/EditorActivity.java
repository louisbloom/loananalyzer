/*
 * Copyright (c) 2016 Thomas Christensen <christensenthomas@gmail.com>
 *
 * All right reserved.
 */

package thomasc.loananalyzer.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import thomasc.loananalyzer.R;
import thomasc.loananalyzer.db.LoanReaderContract.LoanEntry;
import thomasc.loananalyzer.fragments.EditorFragment;

public class EditorActivity extends AppCompatActivity implements
        EditorFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);

        if (savedInstanceState == null) {
            long loanId = getIntent().getLongExtra(LoanEntry._ID, -1);

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            EditorFragment fragment = EditorFragment.newInstance(loanId);

            transaction.replace(R.id.fragment_holder1, fragment);
            transaction.commit();
        }
    }
}
