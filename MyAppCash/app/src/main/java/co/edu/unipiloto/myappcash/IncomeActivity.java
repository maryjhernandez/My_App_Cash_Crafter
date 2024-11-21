package co.edu.unipiloto.myappcash;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import co.edu.unipiloto.myappcash.models.IncomeModel;

public class IncomeActivity extends AppCompatActivity {

    private EditText etIncomeDescription, etIncomeAmount, etIncomeDate;
    private Spinner spIncomeSource, spIncomeType;
    private Button btnSaveIncome, btnEditIncome, btnDeleteIncome;

    private DatabaseReference incomesRef;
    private ArrayList<String> incomeIds;
    private String selectedIncomeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        // Inicializar Firebase
        incomesRef = FirebaseDatabase.getInstance().getReference("ingresos");

        // Referencias a los views
        etIncomeDescription = findViewById(R.id.etIncomeDescription);
        etIncomeAmount = findViewById(R.id.etIncomeAmount);
        etIncomeDate = findViewById(R.id.etIncomeDate);
        spIncomeSource = findViewById(R.id.spIncomeSource);
        spIncomeType = findViewById(R.id.spIncomeType);
        btnSaveIncome = findViewById(R.id.btnSaveIncome);
        btnEditIncome = findViewById(R.id.btnEditIncome);
        btnDeleteIncome = findViewById(R.id.btnDeleteIncome);

        // Configurar Spinner para fuente de ingresos
        ArrayAdapter<CharSequence> adapterSource = ArrayAdapter.createFromResource(this,
                R.array.income_sources_array, android.R.layout.simple_spinner_item);
        adapterSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIncomeSource.setAdapter(adapterSource);

        // Configurar Spinner para tipo de ingreso
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this,
                R.array.income_type_array, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIncomeType.setAdapter(adapterType);

        // Configurar acciones de los botones
        btnSaveIncome.setOnClickListener(v -> saveIncome());
        btnEditIncome.setOnClickListener(v -> showSelectIncomeDialog("edit"));
        btnDeleteIncome.setOnClickListener(v -> showSelectIncomeDialog("delete"));

        incomeIds = new ArrayList<>();
    }

    private void saveIncome() {
        String description = etIncomeDescription.getText().toString().trim();
        String amountStr = etIncomeAmount.getText().toString().trim();
        String date = etIncomeDate.getText().toString().trim();
        String incomeSource = spIncomeSource.getSelectedItem().toString();
        String incomeType = spIncomeType.getSelectedItem().toString();

        if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "El monto debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Por favor, ingresa un monto válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Debes iniciar sesión para registrar un ingreso",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String incomeId = selectedIncomeId != null ? selectedIncomeId : incomesRef.push().getKey();
        if (incomeId != null) {
            IncomeModel income = new IncomeModel(description, amount, date, userId, incomeSource, incomeType);

            incomesRef.child(incomeId).setValue(income)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String message = selectedIncomeId == null ?
                                    "Ingreso guardado exitosamente" : "Ingreso actualizado exitosamente";
                            Toast.makeText(IncomeActivity.this, message, Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(IncomeActivity.this,
                                    "Error al guardar el ingreso", Toast.LENGTH_SHORT).show();
                        }
                        selectedIncomeId = null; // Limpiar ID seleccionado tras guardar o actualizar
                    });
        }
    }

    private void showSelectIncomeDialog(String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Ingreso");

        incomesRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> incomeDescriptions = new ArrayList<>();
                incomeIds.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    IncomeModel income = ds.getValue(IncomeModel.class);
                    if (income != null) {
                        incomeDescriptions.add(income.getDescription());
                        incomeIds.add(ds.getKey());
                    }
                }

                builder.setItems(incomeDescriptions.toArray(new String[0]), (dialog, which) -> {
                    selectedIncomeId = incomeIds.get(which);

                    if (action.equals("edit")) {
                        loadIncomeForEditing(selectedIncomeId);
                    } else if (action.equals("delete")) {
                        deleteIncome(selectedIncomeId);
                    }
                });

                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IncomeActivity.this,
                        "Error al cargar ingresos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadIncomeForEditing(String incomeId) {
        incomesRef.child(incomeId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                IncomeModel income = snapshot.getValue(IncomeModel.class);
                if (income != null) {
                    etIncomeDescription.setText(income.getDescription());
                    etIncomeAmount.setText(String.valueOf(income.getAmount()));
                    etIncomeDate.setText(income.getDate());

                    int sourcePosition = ((ArrayAdapter<String>) spIncomeSource.getAdapter())
                            .getPosition(income.getIncomeSource());
                    spIncomeSource.setSelection(sourcePosition);

                    int typePosition = ((ArrayAdapter<String>) spIncomeType.getAdapter())
                            .getPosition(income.getIncomeType());
                    spIncomeType.setSelection(typePosition);

                    btnSaveIncome.setText("Actualizar Ingreso");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IncomeActivity.this,
                        "Error al cargar el ingreso para editar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteIncome(String incomeId) {
        incomesRef.child(incomeId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(IncomeActivity.this,
                                "Ingreso eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(IncomeActivity.this,
                                "Error al eliminar el ingreso", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        etIncomeDescription.setText("");
        etIncomeAmount.setText("");
        etIncomeDate.setText("");
        spIncomeSource.setSelection(0);
        spIncomeType.setSelection(0);
        btnSaveIncome.setText("Guardar Ingreso");
        selectedIncomeId = null;
    }
}
