package com.example.ramesh.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText email,password;
    Button login,google;
    TextView resetPassword;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient mGoogleApiClient;

    private OnFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        final View view=inflater.inflate(R.layout.fragment_login, container, false);
        email=(EditText)view.findViewById(R.id.email);
        password=(EditText)view.findViewById(R.id.password);
        login=(Button)view.findViewById(R.id.login);
        resetPassword=(TextView)view.findViewById(R.id.password_reset);
        google=(Button)view.findViewById(R.id.google);
        progressDialog=new ProgressDialog(getContext());
        firebaseAuth=firebaseAuth.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("897218998757-35797ga2a9ecl7to24irpe5vi06ugnq9.apps.googleusercontent.com").requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).enableAutoManage(getActivity()/* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(getContext(), connectionResult.toString(), Toast.LENGTH_SHORT).show();
            }
        } /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        google.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,5760);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                if (firebaseAuth.getCurrentUser() != null)
                {
                    progressDialog.dismiss();
                    Log.i("SIGN UP", "on Auth State Changed:signed_in");
                    Snackbar.make(view,"Logged in as:"+firebaseAuth.getCurrentUser().getEmail().toString(), Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "Logged in", Toast.LENGTH_LONG).show();
                    Intent in = new Intent(getContext(), MainActivity.class);
                    startActivity(in);
                }
                else
                {
                    Log.i("LOGIN", "on Auth State Changed:signed_out");
                }
            }

        };

        resetPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final EditText recovery_email = new EditText(getContext());
                recovery_email.setInputType(InputType.TYPE_CLASS_TEXT);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Reset Password").setView(recovery_email).setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Log.i("reset","inside on click OK");
                        if(recovery_email.getText().toString().isEmpty())
                            Snackbar.make(getView(), "Enter Recovery Email", Snackbar.LENGTH_SHORT).show();
                        else {
                            firebaseAuth.sendPasswordResetEmail(recovery_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    Snackbar.make(getView(), "Password Reset Email sent", Snackbar.LENGTH_SHORT).show();
                                }
                            }); }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email_string=email.getText().toString();
                String pass_string=password.getText().toString();

                if(email_string.isEmpty()||pass_string.isEmpty())
                    Toast.makeText(getContext(),"Please Enter both Email and Password",Toast.LENGTH_LONG).show();

                else
                {
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email_string,pass_string).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            Log.i("sign in comp", "signInWithEmail:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful())
                            {
                                progressDialog.dismiss();
                                Log.i("sign in failed", task.getException().toString(), task.getException());
                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5760)
        {
            //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(Auth.GoogleSignInApi.getSignInResultFromIntent(data));
        }
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        Log.i("handleSignInResult:", "" + result.isSuccess());
        if (result.isSuccess())
        {
            // Signed in successfully, show authenticated UI.
            //GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(result.getSignInAccount());

        }
        else
        {
            // Signed out, show unauthenticated UI.
            // updateUI(false);
            Snackbar.make(getView(),"Failed. Try Again",Snackbar.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.i("firebaseAuthWithGoogle:", "" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                Log.i("signInWithCredential:","onComplete:" + task.isSuccessful());
                if (!task.isSuccessful())
                {
                    Log.i("signInWithCredential", "", task.getException());
                    Toast.makeText(getActivity(),"failed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.i("LOGIN FRAGMENT","STARTED");
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("LOGIN FRAGMENT","STOPPED");
        if (mAuthListener != null)
        {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("LOGIN FRAGMENT","DESTROYED");
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("LOGIN FRAGMENT","PAUSED");
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i("LOGIN FRAGMENT","RESUMED");
        mGoogleApiClient.reconnect();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

  /*  @Override
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
