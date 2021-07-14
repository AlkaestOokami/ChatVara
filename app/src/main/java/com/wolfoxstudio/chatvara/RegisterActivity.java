package com.wolfoxstudio.chatvara;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnEnter;
    private Button btnSelectPhoto;
    private Uri selectedUri;
    private ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);

        editName = findViewById(R.id.edit_username);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        btnEnter = findViewById(R.id.button_singin);
        btnSelectPhoto = findViewById(R.id.btn_select_photo);
        imgPhoto = findViewById(R.id.img_photo);

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectPhoto();
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0){
            selectedUri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedUri);
                imgPhoto.setImageDrawable(new BitmapDrawable(bitmap));
                btnSelectPhoto.setAlpha(0);
            } catch (IOException e) {

            }
        }
    }

    private void SelectPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    private void CreateUser(){
        String nome = editName.getText().toString();
        String email = editEmail.getText().toString();
        String senha = editPassword.getText().toString();

        if (nome == null || nome.isEmpty() || email == null || email.isEmpty() || senha == null || senha.isEmpty()){
            Toast.makeText(this, "Nome, email e senha devem ser preenchidos", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.i("Test", task.getResult().getUser().getUid());
                            SaveUserInFirebase();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Test", e.getMessage());
                    }
                });
    }

    private void SaveUserInFirebase(){
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(selectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("Test", uri.toString());

                                String uid = FirebaseAuth.getInstance().getUid();
                                String username = editName.getText().toString();
                                String profileUrl = uri.toString();
                                User user = new User(uid, username, profileUrl);

                                FirebaseFirestore.getInstance().collection("users")
                                        .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.i("Test", documentReference.getId());
                                                editName.setText("");
                                                editEmail.setText("");
                                                editPassword.setText("");
                                                btnSelectPhoto.setAlpha(1);
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Test", e.getMessage(), e);
                    }
                });
    }
}