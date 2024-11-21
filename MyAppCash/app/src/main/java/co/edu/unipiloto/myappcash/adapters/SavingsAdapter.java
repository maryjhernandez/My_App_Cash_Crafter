package co.edu.unipiloto.myappcash.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.edu.unipiloto.myappcash.models.Ahorro;

public class SavingsAdapter extends ArrayAdapter<Ahorro> {
    private List<Ahorro> ahorros;
    private Context context;

    public SavingsAdapter(Context context, List<Ahorro> ahorros) {
        super(context, 0, ahorros);
        this.context = context;
        this.ahorros = ahorros;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener el objeto Ahorro en la posición actual
        Ahorro ahorro = ahorros.get(position);

        // Infle la vista si no se ha reutilizado
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        // Referencias a los TextViews en la vista
        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        // Configurar el texto de los TextViews
        text1.setText(ahorro.getGoalName());
        text2.setText("Ahorro mensual: " + ahorro.calculateMonthlySavings());

        return convertView;
    }
}
