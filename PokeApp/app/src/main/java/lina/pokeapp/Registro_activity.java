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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registro_activity extends AppCompatActivity {
    private EditText Mnamefield;
    private  EditText Memailfield;
    private  EditText Mpassword;
    private Button Msignup;

    private FirebaseAuth mAuth;
    private DatabaseReference database;
    private ProgressDialog mprgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_activity);

        mAuth =FirebaseAuth.getInstance();
        Mnamefield = (EditText)findViewById(R.id.nombre);

        Memailfield = (EditText)findViewById(R.id.email);
        Mpassword = (EditText)findViewById(R.id.password);
        Msignup = (Button)findViewById(R.id.registrarbt);

        mprgress = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance().getReference().child("Users");

        Msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startregister();
            }
        });


    }

    private void startregister() {

        final String name = Mnamefield.getText().toString().trim();
        String email = Memailfield.getText().toString().trim();
        String password = Mpassword.getText().toString().trim();

        if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
            mprgress.setMessage("Registrando...");
            mprgress.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentuser_db = database.child(user_id);
                        currentuser_db.child("nombre").setValue(name);
                        currentuser_db.child("imagen_perfil").setValue("default");

                        mprgress.dismiss();

                        Intent mainIntent = new Intent(Registro_activity.this,MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);




                    }

                }
            });

        }
    }
}
