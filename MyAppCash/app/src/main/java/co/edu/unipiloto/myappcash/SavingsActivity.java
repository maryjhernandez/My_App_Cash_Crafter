package co.edu.unipiloto.myappcash;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.edu.unipiloto.myappcash.models.Ahorro;

public class SavingsActivity extends AppCompatActivity {
    private Spinner spinnerGoals;
    private EditText etGoalName, etTargetAmount, etCurrentAmount, etMonthsToAchieve;
    private TextView tvMonthlySavings;
    private Button btnSaveSavings, btnEditSavings, btnDeleteSavings;

    private FirebaseDatabase database;
    private DatabaseReference savingsRef;
    private String currentSavingsId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savings);

        database = FirebaseDatabase.getInstance();
        savingsRef = database.getReference("savings");

        spinnerGoals = findViewById(R.id.spinnerGoals);
        etGoalName = findViewById(R.id.etGoalName);
        etTargetAmount = findViewById(R.id.etTargetAmount);
        etCurrentAmount = findViewById(R.id.etCurrentAmount);
        etMonthsToAchieve = findViewById(R.id.etMonthsToAchieve);
        tvMonthlySavings = findViewById(R.id.tvMonthlySavings);
        btnSaveSavings = findViewById(R.id.btnSaveSavings);
        btnEditSavings = findViewById(R.id.btnEditSavings);
        btnDeleteSavings = findViewById(R.id.btnDeleteSavings);

        setupGoalsSpinner();
        loadCurrentSavings();

        btnSaveSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSavingsLimit();
            }
        });

        btnEditSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSavingsGoal();
            }
        });

        btnDeleteSavings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSavingsGoal();
            }
        });
    }

    private void setupGoalsSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.goals_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoals.setAdapter(adapter);

        spinnerGoals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGoal = parent.getItemAtPosition(position).toString();
                if (!selectedGoal.equals("Otro")) {
                    etGoalName.setText(selectedGoal);
                } else {
                    etGoalName.setText("");
                    etGoalName.setHint("Ingrese su objetivo personalizado");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    private void loadCurrentSavings() {
        // Aquí puedes cargar la meta de ahorro actual si es necesario
        // Por ejemplo, si ya hay una meta de ahorro guardada para editar
    }

    private void checkSavingsLimit() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            savingsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int savingsCount = (int) dataSnapshot.getChildrenCount();
                    if (savingsCount >= 10) {
                        Toast.makeText(SavingsActivity.this, "Has alcanzado el límite de 10 metas de ahorro.", Toast.LENGTH_SHORT).show();
                    } else {
                        saveSavingsGoal();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SavingsActivity.this, "Error al verificar el número de metas de ahorro: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SavingsActivity.this, "Debes iniciar sesión para guardar una meta de ahorro", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSavingsGoal() {
        String goalName = etGoalName.getText().toString().trim();
        String targetAmountStr = etTargetAmount.getText().toString().trim();
        String currentAmountStr = etCurrentAmount.getText().toString().trim();
        String monthsToAchieveStr = etMonthsToAchieve.getText().toString().trim();

        // Validación de campos
        if (goalName.isEmpty() || targetAmountStr.isEmpty() || currentAmountStr.isEmpty() || monthsToAchieveStr.isEmpty()) {
            Toast.makeText(SavingsActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double targetAmount;
        double currentAmount;
        int monthsToAchieve;

        // Conversión de datos y manejo de excepciones
        try {
            targetAmount = Double.parseDouble(targetAmountStr);
            currentAmount = Double.parseDouble(currentAmountStr);
            monthsToAchieve = Integer.parseInt(monthsToAchieveStr);
        } catch (NumberFormatException e) {
            Toast.makeText(SavingsActivity.this, "Por favor, ingresa valores válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validaciones adicionales
        if (targetAmount < 10) {
            Toast.makeText(SavingsActivity.this, "La cantidad objetivo debe ser al menos 10", Toast.LENGTH_SHORT).show();
            return;
        }

        if (monthsToAchieve <= 0) {
            Toast.makeText(SavingsActivity.this, "Los meses para alcanzar la meta deben ser mayores que 0", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            Ahorro savingsGoal = new Ahorro(goalName, targetAmount, currentAmount, monthsToAchieve, userId);
            double monthlySavings = savingsGoal.calculateMonthlySavings();

            if (monthlySavings < 0) {
                tvMonthlySavings.setText("Ahorro mensual necesario: 0.00");
                Toast.makeText(SavingsActivity.this, "Ahorro mensual no puede ser negativo", Toast.LENGTH_SHORT).show();
            } else {
                tvMonthlySavings.setText(String.format("Ahorro mensual necesario: %.2f", monthlySavings));
            }

            saveSavingsGoalToFirebase(savingsGoal);
        } else {
            Toast.makeText(SavingsActivity.this, "Debes iniciar sesión para guardar una meta de ahorro", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSavingsGoalToFirebase(Ahorro savingsGoal) {
        String savingsId = savingsRef.push().getKey();

        if (savingsId != null) {
            savingsRef.child(savingsId).setValue(savingsGoal)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SavingsActivity.this, "Meta de ahorro guardada exitosamente", Toast.LENGTH_SHORT).show();
                                currentSavingsId = savingsId; // Guarda el ID de la meta de ahorro actual
                            } else {
                                Toast.makeText(SavingsActivity.this, "Error al guardar la meta de ahorro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(SavingsActivity.this, "Error al generar el ID de la meta de ahorro", Toast.LENGTH_SHORT).show();
        }
    }

    private void editSavingsGoal() {
        if (currentSavingsId != null) {
            String goalName = etGoalName.getText().toString().trim();
            String targetAmountStr = etTargetAmount.getText().toString().trim();
            String currentAmountStr = etCurrentAmount.getText().toString().trim();
            String monthsToAchieveStr = etMonthsToAchieve.getText().toString().trim();

            // Validación de campos
            if (goalName.isEmpty() || targetAmountStr.isEmpty() || currentAmountStr.isEmpty() || monthsToAchieveStr.isEmpty()) {
                Toast.makeText(SavingsActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double targetAmount;
            double currentAmount;
            int monthsToAchieve;

            // Conversión de datos y manejo de excepciones
            try {
                targetAmount = Double.parseDouble(targetAmountStr);
                currentAmount = Double.parseDouble(currentAmountStr);
                monthsToAchieve = Integer.parseInt(monthsToAchieveStr);
            } catch (NumberFormatException e) {
                Toast.makeText(SavingsActivity.this, "Por favor, ingresa valores válidos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validaciones adicionales
            if (targetAmount < 10) {
                Toast.makeText(SavingsActivity.this, "La cantidad objetivo debe ser al menos 10", Toast.LENGTH_SHORT).show();
                return;
            }

            if (monthsToAchieve <= 0) {
                Toast.makeText(SavingsActivity.this, "Los meses para alcanzar la meta deben ser mayores que 0", Toast.LENGTH_SHORT).show();
                return;
            }

            Ahorro savingsGoal = new Ahorro(goalName, targetAmount, currentAmount, monthsToAchieve, FirebaseAuth.getInstance().getCurrentUser().getUid());
            savingsRef.child(currentSavingsId).setValue(savingsGoal)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SavingsActivity.this, "Meta de ahorro editada exitosamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SavingsActivity.this, "Error al editar la meta de ahorro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(SavingsActivity.this, "No hay una meta de ahorro seleccionada para editar", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSavingsGoal() {
        if (currentSavingsId != null) {
            savingsRef.child(currentSavingsId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SavingsActivity.this, "Meta de ahorro eliminada exitosamente", Toast.LENGTH_SHORT).show();
                                clearFields();
                            } else {
                                Toast.makeText(SavingsActivity.this, "Error al eliminar la meta de ahorro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(SavingsActivity.this, "No hay una meta de ahorro seleccionada para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        etGoalName.setText("");
        etTargetAmount.setText("");
        etCurrentAmount.setText("");
        etMonthsToAchieve.setText("");
        tvMonthlySavings.setText("");
        currentSavingsId = null; // Limpiar el ID de la meta de ahorro actual
    }
}
