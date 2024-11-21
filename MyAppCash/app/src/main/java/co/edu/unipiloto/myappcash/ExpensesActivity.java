package co.edu.unipiloto.myappcash;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import co.edu.unipiloto.myappcash.models.ExpenseModel;

public class ExpensesActivity extends AppCompatActivity {

    private EditText etExpenseDescription, etExpenseAmount, etExpenseDate;
    private Spinner spExpenseCategory;
    private Button btnSaveExpense, btnAddCategory, btnEditExpense, btnDeleteExpense;

    private DatabaseReference expensesRef, categoriesRef;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private ArrayList<String> categoriesList;
    private ArrayAdapter<String> categoryAdapter;

    private String selectedExpenseId; // Para rastrear el gasto seleccionado para edición o eliminación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        // Inicializar Firebase
        expensesRef = FirebaseDatabase.getInstance().getReference("gastos");
        categoriesRef = FirebaseDatabase.getInstance().getReference("categorias");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Inicializar vistas
        etExpenseDescription = findViewById(R.id.etExpenseDescription);
        etExpenseAmount = findViewById(R.id.etExpenseAmount);
        etExpenseDate = findViewById(R.id.etExpenseDate);
        spExpenseCategory = findViewById(R.id.spExpenseCategory);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnEditExpense = findViewById(R.id.btnEditExpense);
        btnDeleteExpense = findViewById(R.id.btnDeleteExpense);

        // Configurar Spinner de categorías
        categoriesList = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spExpenseCategory.setAdapter(categoryAdapter);

        loadCategoriesFromFirebase();

        btnSaveExpense.setOnClickListener(v -> saveExpense());
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
        btnEditExpense.setOnClickListener(v -> showSelectExpenseDialog("edit"));
        btnDeleteExpense.setOnClickListener(v -> showSelectExpenseDialog("delete"));
    }

    private void loadCategoriesFromFirebase() {
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    categoriesList.add(categorySnapshot.getKey());
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesActivity.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveExpense() {
        String description = etExpenseDescription.getText().toString().trim();
        String amountStr = etExpenseAmount.getText().toString().trim();
        String date = etExpenseDate.getText().toString().trim();
        String category = spExpenseCategory.getSelectedItem().toString();

        if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String expenseId = expensesRef.push().getKey();
        if (expenseId != null) {
            ExpenseModel expense = new ExpenseModel(description, amount, date, category, userId);
            expensesRef.child(expenseId).setValue(expense)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Gasto guardado", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(this, "Error al guardar el gasto", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Categoría");

        final EditText input = new EditText(this);
        input.setHint("Nombre de la categoría");
        builder.setView(input);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String newCategory = input.getText().toString().trim();
            if (!newCategory.isEmpty() && !categoriesList.contains(newCategory)) {
                categoriesRef.child(newCategory).setValue(true);
                Toast.makeText(this, "Categoría agregada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Categoría inválida o ya existe", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void showSelectExpenseDialog(String action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Gasto");

        ArrayList<String> expenseList = new ArrayList<>();
        expensesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseList.clear();
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    String description = expenseSnapshot.child("description").getValue(String.class);
                    expenseList.add(description);
                }
                String[] expensesArray = expenseList.toArray(new String[0]);

                builder.setItems(expensesArray, (dialog, which) -> {
                    String selectedExpenseDescription = expensesArray[which];
                    for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                        if (selectedExpenseDescription.equals(expenseSnapshot.child("description").getValue(String.class))) {
                            selectedExpenseId = expenseSnapshot.getKey();
                            break;
                        }
                    }
                    if (action.equals("edit")) {
                        showEditExpenseDialog();
                    } else if (action.equals("delete")) {
                        deleteExpense();
                    }
                });

                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesActivity.this, "Error al cargar gastos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditExpenseDialog() {
        expensesRef.child(selectedExpenseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ExpenseModel expense = snapshot.getValue(ExpenseModel.class);
                if (expense != null) {
                    etExpenseDescription.setText(expense.getDescription());
                    etExpenseAmount.setText(String.valueOf(expense.getAmount()));
                    etExpenseDate.setText(expense.getDate());
                    int categoryPosition = categoriesList.indexOf(expense.getCategory());
                    if (categoryPosition >= 0) {
                        spExpenseCategory.setSelection(categoryPosition);
                    }
                    btnSaveExpense.setText("ACTUALIZAR GASTO");
                    btnSaveExpense.setOnClickListener(v -> updateExpense());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ExpensesActivity.this, "Error al cargar el gasto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateExpense() {
        String description = etExpenseDescription.getText().toString().trim();
        String amountStr = etExpenseAmount.getText().toString().trim();
        String date = etExpenseDate.getText().toString().trim();
        String category = spExpenseCategory.getSelectedItem().toString();

        if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedExpenseId == null) {
            Toast.makeText(this, "No se seleccionó ningún gasto para actualizar", Toast.LENGTH_SHORT).show();
            return;
        }

        ExpenseModel updatedExpense = new ExpenseModel(description, amount, date, category, currentUser.getUid());
        expensesRef.child(selectedExpenseId).setValue(updatedExpense)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Gasto actualizado correctamente", Toast.LENGTH_SHORT).show();
                        clearFields();
                        btnSaveExpense.setText("GUARDAR GASTO");
                        btnSaveExpense.setOnClickListener(v -> saveExpense());
                    } else {
                        Toast.makeText(this, "Error al actualizar el gasto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteExpense() {
        expensesRef.child(selectedExpenseId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Gasto eliminado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al eliminar el gasto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        etExpenseDescription.setText("");
        etExpenseAmount.setText("");
        etExpenseDate.setText("");
        spExpenseCategory.setSelection(0);
    }
}
