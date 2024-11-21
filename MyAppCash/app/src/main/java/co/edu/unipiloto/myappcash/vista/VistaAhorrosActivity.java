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
import co.edu.unipiloto.myappcash.models.Ahorro;

public class VistaAhorrosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAhorros;
    private ArrayList<Ahorro> listaAhorros;
    private AhorrosAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference ahorrosRef;
    private TextView tvTotalAhorros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_ahorros);

        // Inicializar Firebase
        database = FirebaseDatabase.getInstance();
        ahorrosRef = database.getReference("savings");

        // Inicializar lista
        listaAhorros = new ArrayList<>();

        // Inicializar vistas
        tvTotalAhorros = findViewById(R.id.tvTotalAhorros);
        recyclerViewAhorros = findViewById(R.id.recyclerViewAhorros);
        recyclerViewAhorros.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AhorrosAdapter();
        recyclerViewAhorros.setAdapter(adapter);

        // Verificar usuario autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Debes iniciar sesión para ver tus ahorros",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar los ahorros del usuario
        cargarAhorros(userId);
    }

    private void cargarAhorros(String userId) {
        ahorrosRef.orderByChild("userId").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listaAhorros.clear();
                        double totalAhorros = 0;
                        double totalObjetivo = 0;

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Ahorro ahorro = ds.getValue(Ahorro.class);
                            if (ahorro != null) {
                                listaAhorros.add(ahorro);
                                totalAhorros += ahorro.getCurrentAmount();
                                totalObjetivo += ahorro.getTargetAmount();
                            }
                        }

                        // Actualizar el total de ahorros y progreso
                        String resumen = String.format("Total ahorrado: $%.2f / $%.2f",
                                totalAhorros, totalObjetivo);
                        tvTotalAhorros.setText(resumen);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(VistaAhorrosActivity.this,
                                "Error al cargar los ahorros: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class AhorrosAdapter extends RecyclerView.Adapter<AhorrosAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ahorro, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Ahorro ahorro = listaAhorros.get(position);
            holder.tvNombreMeta.setText(ahorro.getGoalName());
            holder.tvMontoActual.setText(String.format("Actual: $%.2f",
                    ahorro.getCurrentAmount()));
            holder.tvMontoObjetivo.setText(String.format("Objetivo: $%.2f",
                    ahorro.getTargetAmount()));
            holder.tvMesesRestantes.setText(String.format("Meses: %d",
                    ahorro.getMonthsToAchieve()));

            // Calcular y mostrar el ahorro mensual necesario
            double ahorroMensual = ahorro.calculateMonthlySavings();
            holder.tvAhorroMensual.setText(String.format("Mensual: $%.2f",
                    ahorroMensual));

            // Calcular y mostrar el progreso
            double progreso = (ahorro.getCurrentAmount() / ahorro.getTargetAmount()) * 100;
            holder.tvProgreso.setText(String.format("Progreso: %.1f%%", progreso));
        }

        @Override
        public int getItemCount() {
            return listaAhorros.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombreMeta, tvMontoActual, tvMontoObjetivo, tvMesesRestantes,
                    tvAhorroMensual, tvProgreso;

            ViewHolder(View itemView) {
                super(itemView);
                tvNombreMeta = itemView.findViewById(R.id.tvNombreMeta);
                tvMontoActual = itemView.findViewById(R.id.tvMontoActual);
                tvMontoObjetivo = itemView.findViewById(R.id.tvMontoObjetivo);
                tvMesesRestantes = itemView.findViewById(R.id.tvMesesRestantes);
                tvAhorroMensual = itemView.findViewById(R.id.tvAhorroMensual);
                tvProgreso = itemView.findViewById(R.id.tvProgreso);
            }
        }
    }
}