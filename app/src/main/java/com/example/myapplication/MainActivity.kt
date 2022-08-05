package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.entities.TypeSol
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.database.AppDao
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.entities.Culture
import com.example.myapplication.entities.Variete
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    //private lateinit var appDb: AppDatabase
    var dao:AppDao? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBarWithNavController(findNavController(R.id.nav_host_fragment_content_main))

        dao = AppDatabase.getDatabase(this).AppDao()

        val cultures = listOf(
            Culture(0,"Colza"),
            Culture(1,"Blé tendre"),
            Culture(2,"Blé dur"),
            Culture(3,"Luzerne"),
            Culture(4,"Amande"),
            Culture(5,"Pomme"),
            Culture(6,"Figue"),
            Culture(7,"Lin"),
            Culture(8,"Abricot"),
            Culture(9,"Plantes aromatiques"),
            Culture(10,"Artichaut"),
            Culture(11,"Asperge"),
            Culture(12,"Avocat")
        )

        val typeSols = listOf(
            TypeSol(0,"Argile"),
            TypeSol(1,"Argile limoneuse"),
            TypeSol(2,"Argile sableuse"),
            TypeSol(3,"Equilibré"),
            TypeSol(4,"Inconnu"),
            TypeSol(5,"Limon argileux"),
            TypeSol(6,"Limon argileux limoneux"),
            TypeSol(7,"Limon argileux sableux"),
            TypeSol(8,"Limon fin"),
            TypeSol(9,"Limon sableux"),
            TypeSol(10,"Limon très fin"),
            TypeSol(11,"sable"),
            TypeSol(12,"sable limoneux")
        )

        val Varietes = listOf(
            Variete(0,"A"),
            Variete(1,"B"),
            Variete(2,"C"),
        )


        lifecycleScope.launch(Dispatchers.IO) {
            cultures.forEach {  dao!!.insertCulture(it)}
            typeSols.forEach{ dao!!.insertSol(it)}
            Varietes.forEach{ dao!!.insertVariete(it)}
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}