package com.example.habit_planner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habit_planner.databinding.ItemRoutineBinding

class RoutineAdapter(
    private val routines: MutableList<String>,
    private val onEditRoutine: (String, Int) -> Unit,
    private val onPlayRoutine: (String) -> Unit
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    class RoutineViewHolder(val binding: ItemRoutineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = ItemRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.binding.routineName.text = routine
        holder.binding.editRoutineButton.setOnClickListener {
            onEditRoutine(routine, position)
        }
        holder.binding.playRoutineButton.setOnClickListener{
            onPlayRoutine(routine)
        }
    }

    override fun getItemCount() = routines.size

    fun addRoutine(routine: String) {
        routines.add(routine)
        notifyItemInserted(routines.size - 1)
    }

    fun updateRoutine(position: Int, routine: String) {
        routines[position] = routine
        notifyItemChanged(position)
    }
}