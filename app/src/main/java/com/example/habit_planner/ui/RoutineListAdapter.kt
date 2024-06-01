//package com.example.mcp_db.ui
//
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.mcp_db.data.Routine
//import com.example.mcp_db.databinding.ItemRoutineBinding
//
//class RoutineListAdapter(
//    private val routines: List<Routine>,
//    private val onEdit: (Routine) -> Unit,
//    private val onPlay: (Routine) -> Unit
//) : RecyclerView.Adapter<RoutineListAdapter.RoutineViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
//        val binding = ItemRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return RoutineViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
//        val routine = routines[position]
//        holder.bind(routine, onEdit, onPlay)
//    }
//
//    override fun getItemCount() = routines.size
//
//    class RoutineViewHolder(private val binding: ItemRoutineBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(routine: Routine, onEdit: (Routine) -> Unit, onPlay: (Routine) -> Unit) {
//            binding.routineName.text = routine.name
//            binding.editRoutineButton.setOnClickListener {
//                onEdit(routine)
//            }
//            binding.playRoutineButton.setOnClickListener {
//                onPlay(routine)
//            }
//        }
//    }
//}
package com.example.habit_planner.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habit_planner.data.Routine
import com.example.habit_planner.databinding.ItemRoutineBinding

class RoutineListAdapter(
    private val routines: List<Routine>,
    private val onEdit: (Routine) -> Unit,
    private val onPlay: (Routine) -> Unit,
    private val onDelete: (Routine) -> Unit
) : RecyclerView.Adapter<RoutineListAdapter.RoutineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val binding = ItemRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoutineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.bind(routine, onEdit, onPlay, onDelete)
    }

    override fun getItemCount() = routines.size

    class RoutineViewHolder(private val binding: ItemRoutineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routine: Routine, onEdit: (Routine) -> Unit, onPlay: (Routine) -> Unit, onDelete: (Routine) -> Unit) {
            binding.routineName.text = routine.name
            binding.editRoutineButton.setOnClickListener {
                onEdit(routine)
            }
            binding.playRoutineButton.setOnClickListener {
                onPlay(routine)
            }
            binding.root.setOnLongClickListener {
                onDelete(routine)
                true
            }
        }
    }
}
