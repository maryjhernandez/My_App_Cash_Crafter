package co.edu.unipiloto.myappcash.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.edu.unipiloto.myappcash.models.BudgetModel;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private List<BudgetModel> budgetList;
    private OnBudgetListener onBudgetListener;

    public BudgetAdapter(List<BudgetModel> budgetList, OnBudgetListener onBudgetListener) {
        this.budgetList = budgetList;
        this.onBudgetListener = onBudgetListener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new BudgetViewHolder(view, onBudgetListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        BudgetModel budget = budgetList.get(position);
        holder.textView.setText(budget.getBudgetName());
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        OnBudgetListener onBudgetListener;

        public BudgetViewHolder(View itemView, OnBudgetListener onBudgetListener) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
            this.onBudgetListener = onBudgetListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBudgetListener.onBudgetClick(getAdapterPosition());
        }
    }

    public interface OnBudgetListener {
        void onBudgetClick(int position);
    }
}