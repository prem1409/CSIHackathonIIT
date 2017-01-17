package com.example.ramesh.myapplication;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class MainActivity2 extends AppCompatActivity {

    TextView fragment_change;
    Fragment fragment = null;
    Class fragmentClass;
    int flag=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Firebase.setAndroidContext(this);

        fragment_change=(TextView)findViewById(R.id.textView2);
        fragmentClass = LoginFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();

        fragment_change.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(flag==1)
                {
                    fragmentClass = SignupFragment.class;
                    fragment_change.setText("Already have an account? Log In.");
                    flag=0;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                }
                else
                {
                    fragmentClass = LoginFragment.class;
                    fragment_change.setText("Don't have an account? Sign up.");
                    flag=1;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit();
                }
            }
        });
    }
}
