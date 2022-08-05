package com.example.myapplication.database

import com.mapbox.geojson.Point
import androidx.room.*
import com.example.myapplication.entities.*
import com.example.myapplication.relations.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.flow.Flow


@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertParcelle(parcelle: Parcelle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertSol(typeSol: TypeSol)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertSaison(saison: Saison)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertCulture(culture: Culture)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertExploitation(exploitation: Exploitation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertVariete(variete: Variete)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCross(cultureVarieteCrossRef: CultureVarieteCrossRef)


    @Transaction
    @Query("SELECT * FROM Parcelle WHERE id_p= :arg0 ")
     fun getParcelleWithSaisons(arg0: Int): List<ParcelleWithSaisons>

    @Transaction
    @Query("SELECT * FROM Exploitation WHERE id_exp =:id_exp")
     fun getExploitationWithParcelles(id_exp: Int): List<ExploitationWithParcelles>

    @Transaction
    @Query("SELECT * FROM `Type de sol` WHERE id_sol =:id_sol")
     fun getTypeSolWithParcelles(id_sol: Int): List<TypeSolWithParcelles>


    @Transaction
    @Query("SELECT * FROM CultureVarieteCrossRef WHERE id_var= :id_var ")
    fun getCulturesOfVariete(id_var: Int): List<CultureWithVarieties>

    @Transaction
    @Query("SELECT * FROM CultureVarieteCrossRef WHERE id_c= :id_c ")
    fun getVarietesOfCulture(id_c: Int): List<CultureWithVarieties>

    @Transaction
    @Query("SELECT * FROM Culture WHERE id_c= :id_c ")
    fun getSaisonsOfCulture(id_c: Int): List<CultureWithSaisons>

    @Query("SELECT * FROM Parcelle")
    fun readParcelle(): Flow<List<Parcelle>>

    ////////////////////////////////////////////////////////////////////
    @Query("SELECT name FROM Culture ")
    fun mesCultures(): List<String>

    @Query("SELECT name FROM Parcelle ")
    fun mesParcelles(): List<String>

    @Query("SELECT name FROM `Type de sol` ")
    fun mesSol(): List<String>

    @Query("SELECT name FROM Variété ")
    fun mesVar(): List<String>

    @Query("SELECT id_exp from Exploitation where name=:name ")
    fun expId(name : String): Int

    @Query("SELECT id_c from Culture where name=:name ")
    fun cultId(name : String): Int

    @Query("SELECT id_sol from `Type de sol` where name=:name ")
    fun solId(name : String): Int

    @Query("SELECT id_p from Parcelle where name=:name ")
    fun parId(name : String): Int

    @Query("SELECT id_var from Variété where name=:name ")
    fun variId(name : String): Int

    @Query("SELECT Plot FROM Parcelle where id_p=1")
    fun getGeo(): String?

    @Query("SELECT Plot FROM Parcelle where name=:name")
    fun getplot(name: String): String?

    @Query("Update Parcelle set name=:name2 where id_p=:id")
    fun changename(id: Int,name2: String)

}