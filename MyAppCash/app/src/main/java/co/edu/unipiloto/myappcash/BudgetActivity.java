package co.edu.unipiloto.myappcash;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.edu.unipiloto.myappcash.models.BudgetModel;

public class BudgetActivity extends AppCompatActivity {

    private EditText etBudgetName, etTotalAmount, etNewCategory;
    private Spinner spCategory;
    private Button btnSaveBudget, btnEditBudget, btnDeleteBudget, btnAddCategory;

    // Referencia a la base de datos
    private FirebaseDatabase database;
    private DatabaseReference budgetsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Inicializar la base de datos
        database = FirebaseDatabase.getInstance();
        budgetsRef = database.getReference("presupuestos");

        // Referencias a los views
        etBudgetName = findViewById(R.id.etBudgetName);
        etTotalAmount = findViewById(R.id.etTotalAmount);
        spCategory = findViewById(R.id.spCategory);
        etNewCategory = findViewById(R.id.etNewCategory);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);
        btnEditBudget = findViewById(R.id.btnEditBudget);
        btnDeleteBudget = findViewById(R.id.btnDeleteBudget);
        btnAddCategory = findViewById(R.id.btnAddCategory);

        // Configurar el Spinner con categorías
        // Lista dinámica de categorías (agregando categorías de categories_array)
        List<String> categoriesList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.categories_array)));

// Crear el adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);


        // Mostrar campos para nueva categoría si se selecciona "Otro"
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spCategory.getSelectedItem().toString().equals("Otro")) {
                    etNewCategory.setVisibility(View.VISIBLE);
                    btnAddCategory.setVisibility(View.VISIBLE);
                } else {
                    etNewCategory.setVisibility(View.GONE);
                    btnAddCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Configurar botones
        btnSaveBudget.setOnClickListener(v -> checkBudgetLimit());
        btnEditBudget.setOnClickListener(v -> editBudget());
        btnDeleteBudget.setOnClickListener(v -> deleteBudget());
        btnAddCategory.setOnClickListener(v -> addCategory());
    }

    private void checkBudgetLimit() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            budgetsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int budgetCount = (int) dataSnapshot.getChildrenCount();
                    if (budgetCount >= 10) {
                        Toast.makeText(BudgetActivity.this, "Has alcanzado el límite de 10 presupuestos.", Toast.LENGTH_SHORT).show();
                    } else {
                        saveBudget();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("BudgetActivity", "Error al verificar el número de presupuestos: " + databaseError.getMessage());
                }
            });
        } else {
            Toast.makeText(BudgetActivity.this, "Debes iniciar sesión para guardar un presupuesto", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBudget() {
        String budgetName = etBudgetName.getText().toString().trim();
        String totalAmountStr = etTotalAmount.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        if (budgetName.isEmpty() || totalAmountStr.isEmpty()) {
            Toast.makeText(BudgetActivity.this, "Por favor, ingresa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount;
        try {
            totalAmount = Double.parseDouble(totalAmountStr);
            if (totalAmount < 10) {
                Toast.makeText(BudgetActivity.this, "El monto total debe ser al menos 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(BudgetActivity.this, "Por favor, ingresa un monto válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el userId del usuario autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            // Guardar el presupuesto bajo el nodo del usuario
            String budgetId = budgetsRef.child(userId).push().getKey();
            if (budgetId != null) {
                BudgetModel budget = new BudgetModel(budgetId, budgetName, totalAmount, category, userId);
                budgetsRef.child(userId).child(budgetId).setValue(budget).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(BudgetActivity.this, "Presupuesto guardado exitosamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BudgetActivity.this, "Error al guardar el presupuesto", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(BudgetActivity.this, "Debes iniciar sesión para guardar un presupuesto", Toast.LENGTH_SHORT).show();
        }
    }

    private void editBudget() {
        String budgetName = etBudgetName.getText().toString().trim();
        String totalAmountStr = etTotalAmount.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        if (budgetName.isEmpty() || totalAmountStr.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount;
        try {
            totalAmount = Double.parseDouble(totalAmountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresa un monto válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            budgetsRef.child(userId).orderByChild("budgetName").equalTo(budgetName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            String budgetId = child.getKey();
                            BudgetModel updatedBudget = new BudgetModel(budgetId, budgetName, totalAmount, category, userId);
                            budgetsRef.child(userId).child(budgetId).setValue(updatedBudget);
                            Toast.makeText(BudgetActivity.this, "Presupuesto actualizado exitosamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(BudgetActivity.this, "Presupuesto no encontrado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BudgetActivity.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Debes iniciar sesión para editar un presupuesto", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteBudget() {
        String budgetName = etBudgetName.getText().toString().trim();

        if (budgetName.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa el nombre del presupuesto a eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            budgetsRef.child(userId).orderByChild("budgetName").equalTo(budgetName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().removeValue();
                            Toast.makeText(BudgetActivity.this, "Presupuesto eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(BudgetActivity.this, "Presupuesto no encontrado", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BudgetActivity.this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Debes iniciar sesión para eliminar un presupuesto", Toast.LENGTH_SHORT).show();
        }
    }

    private void addCategory() {
        String newCategory = etNewCategory.getText().toString().trim();

        if (newCategory.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el adaptador actual del Spinner
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spCategory.getAdapter();

        if (adapter != null) {
            // Agregar la nueva categoría al adaptador
            adapter.add(newCategory);
            adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado

            // Seleccionar automáticamente la nueva categoría
            spCategory.setSelection(adapter.getPosition(newCategory));

            // Limpiar el campo de texto de la nueva categoría
            etNewCategory.setText("");

            // Mostrar mensaje de éxito
            Toast.makeText(this, "Nueva categoría agregada", Toast.LENGTH_SHORT).show();

            // Agregar el log para verificar que la categoría se haya agregado correctamente
            Log.d("Category", "Categoría agregada: " + newCategory);
        } else {
            Toast.makeText(this, "Error al actualizar las categorías", Toast.LENGTH_SHORT).show();
        }
    }


}
