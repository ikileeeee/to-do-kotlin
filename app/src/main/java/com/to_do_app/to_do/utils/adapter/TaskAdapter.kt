package com.to_do_app.to_do.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.to_do_app.to_do.databinding.EachTodoItemBinding
import com.to_do_app.to_do.utils.model.ToDoData
//definisemo listu kao ulazni parametar
class TaskAdapter(private val list: MutableList<ToDoData>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private  val TAG = "TaskAdapter"
    private var listener:TaskAdapterInterface? = null //listener, a interfejs je definisan dole
    fun setListener(listener:TaskAdapterInterface){
        this.listener = listener //setovanje listenera
    }
    class TaskViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder { //izgled
        val binding =
            EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {//funkcionalnost kotlina
                binding.todoTask.text = this.task//setujemo tekst kartice, this je iz liste jedan element

                Log.d(TAG, "onBindViewHolder: "+this)
                binding.editTask.setOnClickListener { //ako u kartici klikne edit poziva se ta funkcija, u interfejsu definisana
                    listener?.onEditItemClicked(this , position) //saljemo potrerbne vr
                }

                binding.deleteTask.setOnClickListener {//ako u kartici klikne delete poziva se ta funkcija, u interfejsu definisana
                    listener?.onDeleteItemClicked(this , position) //saljemo potrebne parametre
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size //ulazni parametar je list
    }

    interface TaskAdapterInterface{ //interfejs
        fun onDeleteItemClicked(toDoData: ToDoData , position : Int)
        fun onEditItemClicked(toDoData: ToDoData , position: Int)
    }

}