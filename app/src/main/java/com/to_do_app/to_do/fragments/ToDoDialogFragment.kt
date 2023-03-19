package com.to_do_app.to_do.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.to_do_app.to_do.databinding.FragmentToDoDialogBinding
import com.to_do_app.to_do.utils.model.ToDoData
import com.google.android.material.textfield.TextInputEditText


class ToDoDialogFragment : DialogFragment() {

    private lateinit var binding:FragmentToDoDialogBinding
    private var listener : OnDialogNextBtnClickListener? = null //OnDialogNextBtnClickListener napravljen dole kao interfejs!!
    private var toDoData: ToDoData? = null//pocetna vr


    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener// gore listener setujemo, pozivamo ovu funkciju u HomeFragmentu i njag stavljamo kao listener
    }

    companion object {// prikazujemo vrednost izabranog taska u tododialu, a ne da input bude prazan
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) =//znaci potrebne vr su id i string iz baze
            ToDoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)//setovanje vr
                    putString("task", task)//setovanje vr
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToDoDialogBinding.inflate(inflater , container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){//arguments nije null kada se new instance pozove pri kliku edit dugmeta
            toDoData = ToDoData(arguments?.getString("taskId").toString() ,arguments?.getString("task").toString())//parametri
            binding.task.setText(toDoData?.task)//setujemo tekst taska kao vr toDOData task, definisano u liniji iznad
        }


        binding.close.setOnClickListener {
            dismiss()//klik X zatvaramo toDoDialog
        }

        binding.addBtn.setOnClickListener {

            val todoTask = binding.task.text.toString() //uzimamo tekst iz input polja
            if (todoTask.isNotEmpty()){
                if (toDoData == null){//ako je data prazna znaci da pravimo nov task u bazi, u else grani modifikujemo postojeci
                    listener?.saveTask(todoTask , binding.task) //ovde pravimo nov task, definisana funkcija u interfejsu dole
                }else{// kada editujemo
                    toDoData!!.task = todoTask
                    listener?.updateTask(toDoData!!, binding.task) //update starog, definisana funkcija u interfejsu dole
                }

            } else{
                Toast.makeText(context, "You need to name the task!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface OnDialogNextBtnClickListener{
        fun saveTask(todoTask:String , todoEdit:TextInputEditText)
        fun updateTask(toDoData: ToDoData , todoEdit:TextInputEditText)
    }

}