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
import co.edu.unipiloto.myappcash.models.IncomeModel;

public class VistaIngresosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewIngresos;
    private ArrayList<IncomeModel> listaIngresos;
    private IngresosAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference ingresosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_ingresos);

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance();
        ingresosRef = database.getReference("ingresos");

        // Inicializar lista
        listaIngresos = new ArrayList<>();

        // Inicializar RecyclerView
        recyclerViewIngresos = findViewById(R.id.recyclerViewIngresos);
        recyclerViewIngresos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngresosAdapter();
        recyclerViewIngresos.setAdapter(adapter);

        // Verificar usuario autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Debes iniciar sesión para ver tus ingresos",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar los ingresos del usuario
        cargarIngresos(userId);
    }

    private void cargarIngresos(String userId) {
        ingresosRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaIngresos.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            IncomeModel ingreso = ds.getValue(IncomeModel.class);
                            if (ingreso != null) {
                                listaIngresos.add(ingreso);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(VistaIngresosActivity.this,
                                "Error al cargar los ingresos: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class IngresosAdapter extends RecyclerView.Adapter<IngresosAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ingreso, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            IncomeModel ingreso = listaIngresos.get(position);
            holder.tvDescripcion.setText(ingreso.getDescription());
            holder.tvMonto.setText(String.format("$%.2f", ingreso.getAmount()));
            holder.tvFecha.setText(ingreso.getDate());
        }

        @Override
        public int getItemCount() {
            return listaIngresos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDescripcion, tvMonto, tvFecha;

            ViewHolder(View itemView) {
                super(itemView);
                tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
                tvMonto = itemView.findViewById(R.id.tvMonto);
                tvFecha = itemView.findViewById(R.id.tvFecha);
            }
        }
    }
}