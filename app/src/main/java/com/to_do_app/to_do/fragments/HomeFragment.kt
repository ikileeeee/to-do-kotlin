package com.to_do_app.to_do.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import com.to_do_app.to_do.R
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.to_do_app.to_do.databinding.FragmentHomeBinding
import com.to_do_app.to_do.utils.adapter.TaskAdapter
import com.to_do_app.to_do.utils.model.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), ToDoDialogFragment.OnDialogNextBtnClickListener,
    TaskAdapter.TaskAdapterInterface {

    private val TAG = "HomeFragment"

    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private var frag: ToDoDialogFragment? = null //FragmentToDoDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private lateinit var taskAdapter: TaskAdapter //nas adapter koji smo kreirali
    private lateinit var toDoItemList: MutableList<ToDoData> //lista
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
        //lateinit var navController: NavController
        super.onViewCreated(view, savedInstanceState)
        val toolbarnas= binding?.toolBar
        fun action(){
            navController.navigate(R.id.action_homeFragment_to_signInFragment)
        }
        toolbarnas!!.inflateMenu(R.menu.main_menu)
        toolbarnas!!.setOnMenuItemClickListener {
            when(it.itemId) {
                //mAuth.signOut()

                R.id.logout ->action()
            }
            true
        }
        //get data from firebase, funkcija je dole definisana
        getTaskFromFirebase()

        binding.addTask.setOnClickListener {
            if (frag != null)//ako fragment odnosno ToDoDialog vec postoji, nije null
                childFragmentManager.beginTransaction().remove(frag!!).commit()//kreiramo nov
            frag = ToDoDialogFragment() //pravimo insancu fragmenta
            frag!!.setListener(this)//funcija setListener je definisana u dialog fragmentu

            frag!!.show( //njegov prikaz
                childFragmentManager,
                ToDoDialogFragment.TAG
            )

        }

    }
    private fun getTaskFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoItemList.clear()//prvo brisemo sve iz liste
                for (taskSnapshot in snapshot.children) {///kreiramo varijablu taskSnapshot
                    val todoTask = taskSnapshot.key?.let { // key?. proverava dal je null
                        ToDoData(it, taskSnapshot.value.toString()) //it je kljuc, dodajemo ga u ToDoData ako nije null
                    }
                    if (todoTask != null) {
                       toDoItemList.add(todoTask)//dodajemo not null vr listi
                    }
                }
                Log.d(TAG, "onDataChange: " + toDoItemList)
                taskAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun init(view: View) {

        auth = FirebaseAuth.getInstance() //uzimamo bazu
        authId = auth.currentUser!!.uid // uzimamo kor
        database = Firebase.database.reference.child("Tasks") //bazi dodajmeo "dete" tasks, a on ima jos jendo dete
            .child(authId) //preko id dolazimo do svih delova to do liste naseg korisnika


        binding.lista.setHasFixedSize(true)
        binding.lista.layoutManager = LinearLayoutManager(context)

        toDoItemList = mutableListOf()
        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.lista.adapter = taskAdapter
        navController = Navigation.findNavController(view)
    }

    override fun saveTask(todoTask: String, todoEdit: TextInputEditText) {
// funkciji smo poslali parametre iz toDoDialogFragmenta
        database
            .push().setValue(todoTask) //push krerira unique id, setujemo vrednost na polsatu iz ToDoDialoga
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Task was added!", Toast.LENGTH_SHORT).show()//poruka o uspesnom dodavanju
                    todoEdit.text = null // setujemo input polje da nema vrednost

                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()// ako dodje do greske prilikom dodavnja task, ali se to nmz desiti
                }
            }
        frag!!.dismiss()//zatvaranje prozora

    }

    override fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>() //firebase update radi samo sa mapama
        map[toDoData.taskId] = toDoData.task
        database.updateChildren(map).addOnCompleteListener {//updejtovanje, zelimo samo vr teksta da updatujemo, ne i id
            if (it.isSuccessful) {
                Toast.makeText(context, "You have successfully updated task", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()//zatvaramo dialog
        }
    }

    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {//vr su poslate iz adaptera
        database.child(toDoData.taskId).removeValue().addOnCompleteListener {// na osnovu id-ja brise task, pa salje propratnu poruku
            if (it.isSuccessful) {
                Toast.makeText(context, "You have successfully deleted task", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditItemClicked(toDoData: ToDoData, position: Int) {//vrednosti koje su poslate
        if (frag != null)//ako frag nije null
            childFragmentManager.beginTransaction().remove(frag!!).commit()//sklanjamo taj fragment
        frag = ToDoDialogFragment.newInstance(toDoData.taskId, toDoData.task)//pravimo novu instancu sa postojecim vr iz baze, one su i poslate
        frag!!.setListener(this)
        frag!!.show(//prikazujemo izmnjen fragment
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }

}