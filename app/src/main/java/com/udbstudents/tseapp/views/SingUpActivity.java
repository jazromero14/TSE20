package com.udbstudents.tseapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.udbstudents.tseapp.R;

public class SingUpActivity extends AppCompatActivity {


    private EditText mEditextName;
    private EditText mEditextEmail;
    private EditText mEditextPassword;
    private EditText mEditextApellido;
    private EditText mEditextConfirmP;
    private Button mButton;
    private String name = "";
    private String email = "";
    private String password = "";
    private String apellido = "";
    private String confirmPass = "";
    private Boolean isValid = false;
    private Boolean isValidPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        //Inflate elements
        mEditextName = findViewById(R.id.edit_name);
        mEditextPassword = findViewById(R.id.edit_password);
        mEditextApellido = findViewById(R.id.edit_user);
        mEditextConfirmP = findViewById(R.id.edit_confirm_password);
        mButton = findViewById(R.id.btn_sign_up);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = mEditextName.getText().toString();
                apellido = mEditextApellido.getText().toString();
                email = mEditextEmail.getText().toString();
                password = mEditextPassword.getText().toString();
                confirmPass = mEditextConfirmP.getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    if (password.length() >= 8) {
                        registerUser(name, apellido,email);
                    } else {
                        Toast.makeText(SingUpActivity.this, "Debe Tener 8 Caracteres", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SingUpActivity.this, "Debe completar los campos, todos son obligatorios", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(String name, String apellido, String email) {
    }
}