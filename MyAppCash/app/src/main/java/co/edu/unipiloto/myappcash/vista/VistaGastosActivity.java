package co.edu.unipiloto.myappcash.vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import co.edu.unipiloto.myappcash.R;
import co.edu.unipiloto.myappcash.models.ExpenseModel;

public class VistaGastosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGastos;
    private ArrayList<ExpenseModel> listaGastos;
    private GastosAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference gastosRef;
    private TextView tvTotalGastos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_gastos);

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance();
        gastosRef = database.getReference("gastos");

        // Inicializar lista
        listaGastos = new ArrayList<>();

        // Inicializar vistas
        tvTotalGastos = findViewById(R.id.tvTotalGastos);
        recyclerViewGastos = findViewById(R.id.recyclerViewGastos);
        recyclerViewGastos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GastosAdapter();
        recyclerViewGastos.setAdapter(adapter);

        // Verificar usuario autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Debes iniciar sesión para ver tus gastos",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar los gastos del usuario
        cargarGastos(userId);
    }

    private void cargarGastos(String userId) {
        gastosRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaGastos.clear();
                        double totalGastos = 0;

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ExpenseModel gasto = ds.getValue(ExpenseModel.class);
                            if (gasto != null) {
                                listaGastos.add(gasto);
                                totalGastos += gasto.getAmount();
                            }
                        }

                        // Actualizar el total de gastos
                        tvTotalGastos.setText(String.format("Total: $%.2f", totalGastos));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(VistaGastosActivity.this,
                                "Error al cargar los gastos: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class GastosAdapter extends RecyclerView.Adapter<GastosAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_gasto, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ExpenseModel gasto = listaGastos.get(position);
            holder.tvDescripcion.setText(gasto.getDescription());
            holder.tvMonto.setText(String.format("$%.2f", gasto.getAmount()));
            holder.tvFecha.setText(gasto.getDate());
            holder.tvCategoria.setText(gasto.getCategory());
        }

        @Override
        public int getItemCount() {
            return listaGastos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDescripcion, tvMonto, tvFecha, tvCategoria;

            ViewHolder(View itemView) {
                super(itemView);
                tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
                tvMonto = itemView.findViewById(R.id.tvMonto);
                tvFecha = itemView.findViewById(R.id.tvFecha);
                tvCategoria = itemView.findViewById(R.id.tvCategoria);
            }
        }
    }
}