package lina.pokeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText email,password;
    private Button loginbt,registerbt;
    private FirebaseAuth mAuthlogin;
    private DatabaseReference mDatabseUsers;
    private ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.login_email);
        password = (EditText)findViewById(R.id.login_password);
        loginbt = (Button)findViewById(R.id.loginbt);
        registerbt = (Button)findViewById(R.id.nuevousuariobt);

        mAuthlogin = FirebaseAuth.getInstance();
        mDatabseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabseUsers.keepSynced(true);
        mprogress = new ProgressDialog(this);
        loginbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checklogin();
            }
        });

        registerbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postintent = new Intent(LoginActivity.this,Registro_activity.class);
                postintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(postintent);
            }
        });


    }

    private void checklogin() {
        String emailtxt = email.getText().toString().trim();
        String passwordtxt = password.getText().toString().trim();

        if (!TextUtils.isEmpty(emailtxt)&&!TextUtils.isEmpty(passwordtxt)){
            mprogress.setMessage("checking login...");
            mprogress.show();
            mAuthlogin.signInWithEmailAndPassword(emailtxt,passwordtxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        mprogress.dismiss();
                        checkexist();


                    }
                    else {
                        mprogress.dismiss();
                        Toast.makeText(LoginActivity.this,"Error Login",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    private void checkexist() {
        final String user_id= mAuthlogin.getCurrentUser().getUid();
        mDatabseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    Intent  mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                }else{
                    Intent  setupintent = new Intent(LoginActivity.this,SetupPerfil.class);
                    setupintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupintent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
