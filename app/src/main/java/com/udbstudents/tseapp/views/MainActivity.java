package com.udbstudents.tseapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.udbstudents.tseapp.R;
import com.udbstudents.tseapp.RetrofitService;
import com.udbstudents.tseapp.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final String baseurl = "https://172.09.09.9::8080/";
    private Retrofit retrofit = null;
    List<User>  userList = new ArrayList<>();

    private FloatingActionButton fav;
    private EditText mEditUser;
    private EditText mEditPassword;
    private Button btnLogin;
    private String userName = "";
    private String passwordText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditUser = findViewById(R.id.edit_usuario);
        mEditPassword = findViewById(R.id.edit_pass);
        btnLogin = findViewById(R.id.btn_login);


        retrofit();
        retrofit = new Retrofit.Builder().baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = mEditUser.getText().toString().trim();
                passwordText = mEditPassword.getText().toString().trim();

                if (!userName.isEmpty() && !passwordText.isEmpty()){
                    signIn(userName, passwordText);
                }
                else{
                    Toast.makeText(MainActivity.this, "Los campos son necesarios", Toast.LENGTH_LONG).show();
                }
            }
        });

        fav = findViewById(R.id.fab);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SingUpActivity.class);
                startActivity(myIntent);
            }
        });


    }

    private void signIn(String userName, String passwordText) {
    }

    private void retrofit() {

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        final Call<List<User>> listaUser = retrofitService.getUsers();

        listaUser.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                if (response.isSuccessful()){
                    userList = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }
}