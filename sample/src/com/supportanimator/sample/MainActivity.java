
package com.supportanimator.sample;

import static com.supportanimator.sample.PreferencesActivity.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;

public class MainActivity extends FragmentActivity {

    private SharedPreferences mPrefs;
    private Fragment mFrag1, mFrag2;
    private Fragment mCurrent;
    private boolean mVisible = true;

    private FragmentTransaction makeTransaction() {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction().addToBackStack(null);
        String mode = mPrefs.getString(PREF_TRANSITION_MODE, "");
        if(mode.equals("Custom"))
            tx.setCustomAnimations(R.anim.flip_left_in, R.anim.flip_left_out, R.anim.flip_right_in, R.anim.flip_right_out);
        else if(mode.equals("Standard"))
            tx.setTransition(Integer.parseInt(mPrefs.getString(PREF_STANDARD_TRANSITION, "")));
        //       else if(mode.equals("Style"))
        //                tx.setTransitionStyle(R.style.FragStyle);
        return tx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.main);

        Button toggleButton = (Button)findViewById(R.id.toggle_button);
        toggleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction tx = makeTransaction();
                if(mVisible)
                    tx.hide(mCurrent);
                else
                    tx.show(mCurrent);
                tx.commit();
                mVisible = !mVisible;
            }
        });

        Button switchButton = (Button)findViewById(R.id.switch_button);
        switchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrent = mCurrent==mFrag1 ? mFrag2 : mFrag1;
                makeTransaction().replace(R.id.fragment, mCurrent).commit();
            }
        });

        if(savedInstanceState==null) {
            mCurrent = mFrag1 = TextFragment.getInstance("Fragment #1", Color.RED);
            mFrag2 = TextFragment.getInstance("Fragment #2", Color.BLUE);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, mCurrent).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_prefs:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
        }
        return false;
    }

    public static class TextFragment extends Fragment {

        public static TextFragment getInstance(String text, int color) {
            Bundle args = new Bundle();
            args.putString("text", text);
            args.putInt("color", color);
            TextFragment frag = new TextFragment();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.text_fragment, container, false);
            view.setBackgroundColor(getArguments().getInt("color"));
            TextView text = (TextView)view.findViewById(R.id.text1);
            text.setText(getArguments().getString("text"));
            return view;
        }

        @Override
        public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
            String mode = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(PREF_TRANSITION_MODE, "");
            if(mode.equals("Fragment"))
                //            if(MODE.equals(TransitionMode.FRAGMENT))
                return AnimatorInflater.loadAnimator(getActivity(), enter ? R.anim.fade_enter : R.anim.fade_exit);
            else
                return null;
        }
    }
}
