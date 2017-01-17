package com.example.ramesh.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    EditText email;
    EditText password;
    Button signup;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog progressDialog;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_signup, container, false);

        signup=(Button)view.findViewById(R.id.sign);
        email=(EditText)view.findViewById(R.id.editText3);
        password=(EditText)view.findViewById(R.id.editText4);
        progressDialog= new ProgressDialog(getContext());
        firebaseAuth= FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // User is signed in
                    progressDialog.dismiss();
                    Log.i("SIGN UP", "on Auth State Changed:signed_in");
                    Toast.makeText(getContext(), "Successfully Registered", Toast.LENGTH_LONG).show();
                    Intent in = new Intent(getContext(), MainActivity.class);
                    startActivity(in);
                }
                else
                {
                    // User is signed out
                    Log.i("SIGN UP", "on Auth State Changed:signed_out");
                }
            }
        };

        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email_string=email.getText().toString();
                String pass_string=password.getText().toString();

                if(email_string.isEmpty() || pass_string.isEmpty())
                    Toast.makeText(getContext(),"Please enter E-mail and Password",Toast.LENGTH_LONG).show();

                else if(pass_string.length()!=0&&pass_string.length()<6)
                    Toast.makeText(getContext(),"Password should be at least 6 characters long",Toast.LENGTH_LONG).show();

                else
                {
                    progressDialog.setMessage("Registering...");
                    progressDialog.show();

                    firebaseAuth.createUserWithEmailAndPassword(email_string, pass_string).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            Log.i("create user", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Log.i("sign up failed",task.getException().toString());
                                Toast.makeText(getContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }

        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
   /* @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    } */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
