package co.edu.unipiloto.myappcash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.myappcash.vista.MyCashActivity;
import co.edu.unipiloto.myappcash.gamificacion.gamificacion.RangosActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Botón para ingresos
        Button btnIngreso = findViewById(R.id.btnIngreso);
        btnIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, IncomeActivity.class);
                startActivity(intent);
            }
        });

        // Botón para crear presupuesto
        Button btnBudget = findViewById(R.id.btnBudget);
        btnBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        // Botón para registrar gastos
        Button btnExpenses = findViewById(R.id.btnExpenses);
        btnExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ExpensesActivity.class);
                startActivity(intent);
            }
        });

        // Botón para Recursos Educativos
        Button btnCommunity = findViewById(R.id.btnCommunity);
        btnCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RangosActivity.class);
                startActivity(intent);
            }
        });


        // Botón para la planificación de ahorros
        Button btnSavings = findViewById(R.id.btnSavings);
        btnSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SavingsActivity.class);
                startActivity(intent);
            }
        });

        // Botón para My Cash
        Button btnMyCash = findViewById(R.id.btnMyCash);
        btnMyCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MyCashActivity.class);
                startActivity(intent);
            }
        });
    }
}
