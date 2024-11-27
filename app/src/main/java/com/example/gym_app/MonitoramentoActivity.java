package com.example.gym_app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gym_app.database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonitoramentoActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView statusConsultaTextView;
    private DatabaseHelper databaseHelper;
    private LinearLayout consultaMarcadaLegend;
    private LinearLayout indisponivelLegend;
    private String selectedDate; // Data selecionada no calendário
    private Locale locale = new Locale("pt", "BR"); // Locale para português do Brasil

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoramento);

        // Configuração do Locale para português
        Locale.setDefault(locale);

        // Inicializar componentes do layout
        calendarView = findViewById(R.id.calendarView);
        statusConsultaTextView = findViewById(R.id.textViewTitle);
        consultaMarcadaLegend = findViewById(R.id.consultaMarcadaLegend);
        indisponivelLegend = findViewById(R.id.indisponivelLegend);

        // Inicializar o banco de dados
        databaseHelper = new DatabaseHelper(this);

        // Configurar o evento de clique no calendário
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Obter a data selecionada
                selectedDate = getFormattedDate(year, month, dayOfMonth);

                // Consultar o status da data no banco
                String status = databaseHelper.getConsultaStatus(selectedDate);

                // Atualizar o status na interface
                updateConsultaStatus(status);

                // Mostrar um Toast com o dia da semana e o status
                Toast.makeText(MonitoramentoActivity.this,
                        selectedDate + " - Status: " + status,
                        Toast.LENGTH_LONG).show();

                // Abrir um diálogo para editar o status
                showEditStatusDialog(selectedDate, status);
            }
        });
    }

    // Atualiza o status da consulta na interface
    private void updateConsultaStatus(String status) {
        switch (status) {
            case "marcada":
                statusConsultaTextView.setText("Consulta Marcada");
                consultaMarcadaLegend.setVisibility(LinearLayout.VISIBLE);
                indisponivelLegend.setVisibility(LinearLayout.GONE);
                break;
            case "indisponível":
                statusConsultaTextView.setText("Data Indisponível");
                consultaMarcadaLegend.setVisibility(LinearLayout.GONE);
                indisponivelLegend.setVisibility(LinearLayout.VISIBLE);
                // Personalizar cores da data
                break;
            default:
                statusConsultaTextView.setText("Data Livre");
                consultaMarcadaLegend.setVisibility(LinearLayout.GONE);
                indisponivelLegend.setVisibility(LinearLayout.GONE);
                break;
        }
    }

    // Exibe um diálogo para editar o status da data
    private void showEditStatusDialog(String date, String currentStatus) {
        // Opções de status
        String[] options = {"Disponível", "Indisponível", "Consulta Marcada"};

        // Determinar a seleção inicial com base no status atual
        int checkedItem = -1;
        switch (currentStatus) {
            case "livre":
                checkedItem = 0;
                break;
            case "indisponível":
                checkedItem = 1;
                break;
            case "marcada":
                checkedItem = 2;
                break;
        }

        // Criar o diálogo
        new AlertDialog.Builder(this)
                .setTitle("Editar Status da Data: " + date)
                .setSingleChoiceItems(options, checkedItem, null)
                .setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedOption = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        String newStatus;

                        switch (selectedOption) {
                            case 0:
                                newStatus = "livre";
                                break;
                            case 1:
                                newStatus = "indisponível";
                                break;
                            case 2:
                                newStatus = "marcada";
                                break;
                            default:
                                newStatus = "livre";
                                break;
                        }

                        // Atualizar o status no banco de dados
                        databaseHelper.updateConsulta(date, newStatus);

                        // Atualizar a interface com o novo status
                        updateConsultaStatus(newStatus);

                        // Mostrar uma mensagem ao usuário
                        Toast.makeText(MonitoramentoActivity.this,
                                "Status atualizado para: " + newStatus,
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Retorna a data formatada com o dia da semana e o mês em português
    private String getFormattedDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance(locale);
        calendar.set(year, month, dayOfMonth);

        // Formatar a data
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", locale);
        return dateFormat.format(calendar.getTime());
    }
}
