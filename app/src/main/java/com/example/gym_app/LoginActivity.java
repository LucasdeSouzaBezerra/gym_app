package com.example.gym_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gym_app.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);  // Certifique-se de ter um layout correspondente

        // Inicializando os campos e o DatabaseHelper
        editEmail = findViewById(R.id.editTextEmail);  // Verifique o ID no layout
        editPassword = findViewById(R.id.editTextPassword);  // Verifique o ID no layout
        dbHelper = new DatabaseHelper(this);
    }

    // Método de login
    public void onLogin(View view) {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
        } else {
            // Verificando o login (com base no tipo de usuário, seja profissional ou cliente)
            boolean loginValidoProfissional = dbHelper.checkProfissional(email, password);
            boolean loginValidoCliente = dbHelper.checkCliente(email, password);

            if (loginValidoProfissional || loginValidoCliente) {
                // Se o login for válido, redireciona para a tela de monitoramento
                navigateToMonitoramento();
            } else {
                // Caso o login falhe
                Toast.makeText(this, "Login ou senha inválidos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Função para navegar para a tela de monitoramento
    private void navigateToMonitoramento() {
        // Criação da Intent para a tela de Monitoramento
        Intent intent = new Intent(LoginActivity.this, MonitoramentoActivity.class);
        startActivity(intent);  // Inicia a nova Activity
        finish();  // Finaliza a tela de login para não voltar a ela ao pressionar "voltar"
    }
}
