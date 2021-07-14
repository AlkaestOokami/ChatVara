package com.wolfoxstudio.chatvara;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private Button btnEnter;
    private TextView txtNewAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        editEmail = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_email);
        btnEnter = findViewById(R.id.button_singin);
        txtNewAccount = findViewById(R.id.txt_new_account02);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                Log.i("Test", email);
                Log.i("Test", password);

                if (email == null || email.isEmpty() || password == null || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Email e senha devem ser preenchidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.i("Test", task.getResult().getUser().getUid());
                                Intent intent = new Intent(LoginActivity.this, MessageActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Test", e.getMessage());
                            }
                        });
            }
        });

        txtNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editEmail.setText("");
                editPassword.setText("");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}