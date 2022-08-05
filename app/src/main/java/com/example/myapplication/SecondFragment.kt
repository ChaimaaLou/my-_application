package com.example.myapplication

//////////////////////////////////////////////////////////

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.databinding.FragmentSecondBinding
import com.example.myapplication.entities.CultureVarieteCrossRef
import com.example.myapplication.entities.Exploitation
import com.example.myapplication.entities.Parcelle
import com.example.myapplication.entities.Saison
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.mapbox.geojson.*
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), OnMapReadyCallback  {


    private val CIRCLE_SOURCE_ID = "circle-source-id"
    private val FILL_SOURCE_ID = "fill-source-id"
    private val LINE_SOURCE_ID = "line-source-id"
    private val CIRCLE_LAYER_ID = "circle-layer-id"
    private val FILL_LAYER_ID = "fill-layer-polygon-id"
    private val LINE_LAYER_ID = "line-layer-id"
    private var fillLayerPointList: MutableList<Point?> = ArrayList()
    private var lineLayerPointList: MutableList<Point?> = ArrayList()
    private var circleLayerFeatureList: MutableList<Feature> = ArrayList()
    private var listOfList: MutableList<List<Point?>>? = null
    private var mapboxMap: MapboxMap? = null
    private var circleSource: GeoJsonSource? = null
    private var fillSource: GeoJsonSource? = null
    private var lineSource: GeoJsonSource? = null
    private var firstPointOfPolygon: Point? = null

    private var _binding: FragmentSecondBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    //////////////////////////////////////////////

    private lateinit var dialog:BottomSheetDialog
    private lateinit var appDb: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(requireContext(),getString(R.string.mapbox_access_token))

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        return binding.root

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("testingData", "yarbi : ${FILL_SOURCE_ID}")

        dialog = BottomSheetDialog(requireContext())

        binding.buttonSecond.setOnClickListener {
            binding.startLayout.visibility = View.GONE
            binding.secLayout.visibility = View.VISIBLE
        }

        binding.button2.setOnClickListener {
            showBottomSheetDialog()
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    //changes

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(
            Style.SATELLITE
        ) { style ->
            mapboxMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .zoom(0.0)
                        .build()
                ), 1
            )



            // Add sources to the map
            circleSource = initCircleSource(style)
            fillSource = initFillSource(style)
            lineSource = initLineSource(style)

            // Add layers to the map
            initCircleLayer(style)
            initLineLayer(style)
            initFillLayer(style)
            initFloatingActionButtonClickListeners()

        }
    }

    /**
     * Set the button click listeners
     */
    private fun initFloatingActionButtonClickListeners() {
        binding.button4.setOnClickListener(View.OnClickListener { clearEntireMap() })
        binding.button3.setOnClickListener { // Use the map click location to create a Point object
            val mapTargetPoint = Point.fromLngLat(
                mapboxMap!!.cameraPosition.target.longitude,
                mapboxMap!!.cameraPosition.target.latitude
            )

// Make note of the first map click location so that it can be used to create a closed polygon later on
            if (circleLayerFeatureList.size == 0) {
                firstPointOfPolygon = mapTargetPoint
            }

// Add the click point to the circle layer and update the display of the circle layer data
            circleLayerFeatureList.add(Feature.fromGeometry(mapTargetPoint))
            if (circleSource != null) {
                circleSource!!.setGeoJson(FeatureCollection.fromFeatures(circleLayerFeatureList))
            }

// Add the click point to the line layer and update the display of the line layer data
            if (circleLayerFeatureList.size < 3) {
                lineLayerPointList.add(mapTargetPoint)
            } else if (circleLayerFeatureList.size == 3) {
                lineLayerPointList.add(mapTargetPoint)
                lineLayerPointList.add(firstPointOfPolygon)
            } else {
                lineLayerPointList.removeAt(circleLayerFeatureList.size - 1)
                lineLayerPointList.add(mapTargetPoint)
                lineLayerPointList.add(firstPointOfPolygon)
            }
            if (lineSource != null) {
                lineSource!!.setGeoJson(
                    FeatureCollection.fromFeatures(
                        arrayOf(
                            Feature.fromGeometry(
                                LineString.fromLngLats(lineLayerPointList)
                            )
                        )
                    )
                )
            }

// Add the click point to the fill layer and update the display of the fill layer data
            if (circleLayerFeatureList.size < 3) {
                fillLayerPointList.add(mapTargetPoint)
            } else if (circleLayerFeatureList.size == 3) {
                fillLayerPointList.add(mapTargetPoint)
                fillLayerPointList.add(firstPointOfPolygon)
            } else {
                fillLayerPointList.removeAt(fillLayerPointList.size - 1)
                fillLayerPointList.add(mapTargetPoint)
                fillLayerPointList.add(firstPointOfPolygon)
            }
            listOfList = ArrayList()
            (listOfList as ArrayList<List<Point?>>).add(fillLayerPointList)
            val finalFeatureList: MutableList<Feature> = ArrayList()
            finalFeatureList.add(Feature.fromGeometry(Polygon.fromLngLats(listOfList as ArrayList<List<Point?>>)))
            val newFeatureCollection = FeatureCollection.fromFeatures(finalFeatureList)
            if (fillSource != null) {
                fillSource!!.setGeoJson(newFeatureCollection)
            }
        }
    }

    /**
     * Remove the drawn area from the map by resetting the FeatureCollections used by the layers' sources
     */
    private fun clearEntireMap() {
        fillLayerPointList = ArrayList()
        circleLayerFeatureList = ArrayList()
        lineLayerPointList = ArrayList()
        if (circleSource != null) {
            circleSource!!.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
        }
        if (lineSource != null) {
            lineSource!!.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
        }
        if (fillSource != null) {
            fillSource!!.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
        }
    }

    /**
     * Set up the CircleLayer source for showing map click points
     */
    private fun initCircleSource(loadedMapStyle: Style): GeoJsonSource {
        val circleFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val circleGeoJsonSource = GeoJsonSource(CIRCLE_SOURCE_ID, circleFeatureCollection)
        loadedMapStyle.addSource(circleGeoJsonSource)
        return circleGeoJsonSource
    }

    /**
     * Set up the CircleLayer for showing polygon click points
     */
    private fun initCircleLayer(loadedMapStyle: Style) {
        val circleLayer = CircleLayer(
            CIRCLE_LAYER_ID,
            CIRCLE_SOURCE_ID
        )
        circleLayer.setProperties(
            circleRadius(7f),
            circleColor(Color.parseColor("#d004d3"))
        )
        loadedMapStyle.addLayer(circleLayer)
    }

    /**
     * Set up the FillLayer source for showing map click points
     */
    private fun initFillSource(loadedMapStyle: Style): GeoJsonSource {
        val fillFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val fillGeoJsonSource = GeoJsonSource(FILL_SOURCE_ID, fillFeatureCollection)
        loadedMapStyle.addSource(fillGeoJsonSource)
        return fillGeoJsonSource
    }

    /**
     * Set up the FillLayer for showing the set boundaries' polygons
     */
    private fun initFillLayer(loadedMapStyle: Style) {
        val fillLayer = FillLayer(
            FILL_LAYER_ID,
            FILL_SOURCE_ID
        )
        fillLayer.setProperties(
            fillOpacity(.6f),
            fillColor(Color.parseColor("#00e9ff"))
        )
        loadedMapStyle.addLayerBelow(fillLayer, LINE_LAYER_ID)
    }

    /**
     * Set up the LineLayer source for showing map click points
     */
    private fun initLineSource(loadedMapStyle: Style): GeoJsonSource {
        val lineFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val lineGeoJsonSource = GeoJsonSource(LINE_SOURCE_ID, lineFeatureCollection)
        loadedMapStyle.addSource(lineGeoJsonSource)
        return lineGeoJsonSource
    }

    /**
     * Set up the LineLayer for showing the set boundaries' polygons
     */
    private fun initLineLayer(loadedMapStyle: Style) {
        val lineLayer = LineLayer(
            LINE_LAYER_ID,
            LINE_SOURCE_ID
        )
        lineLayer.setProperties(
            lineColor(Color.WHITE),
            lineWidth(5f)
        )
        loadedMapStyle.addLayerBelow(lineLayer, CIRCLE_LAYER_ID)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState!!)
    }

    //bottom sheet
    private fun showBottomSheetDialog() {
        dialog.setContentView(R.layout.bottom_sheet_dialog)

        val first = dialog.findViewById<ConstraintLayout>(R.id.firstBottomSheet)
        val second = dialog.findViewById<ConstraintLayout>(R.id.secondBottomSheet)
        val next = dialog.findViewById<Button>(R.id.btn_next)

        appDb = AppDatabase.getDatabase(requireContext())
        loadSpinnerData()

        dialog.findViewById<Button>(R.id.btn_submit)?.setOnClickListener{
            saveParcelle()
        }

        next?.setOnClickListener {  //handle click event
            View.GONE.also { first?.visibility = it }
            View.VISIBLE.also { second?.visibility = it }
        }

        dialog.show()


    }
////////////////////////////////////
    private fun saveParcelle() {

        val nomPar = dialog.findViewById<EditText>(R.id.nom_par)?.text.toString()
        val duree = dialog.findViewById<EditText>(R.id.id_duree)?.text.toString()
        val Exp = dialog.findViewById<EditText>(R.id.exploitation)?.text.toString()
        val Cult = dialog.findViewById<Spinner>(R.id.culture)?.getSelectedItem().toString()
        val CulturePrec = dialog.findViewById<Spinner>(R.id.culture_prec)?.getSelectedItem().toString()
        val Vari = dialog.findViewById<Spinner>(R.id.variete)?.getSelectedItem().toString()
        val TypeDeSol = dialog.findViewById<Spinner>(R.id.type_sol)?.getSelectedItem().toString()
        val saison = dialog.findViewById<DatePicker>(R.id.saison)
        val semi = dialog.findViewById<DatePicker>(R.id.date_semi)
        val DtSai = saison?.getFullDate('/')
        val DtSe = semi?.getFullDate('/')

        val solId = appDb.AppDao().solId(TypeDeSol)
        val cultId = appDb.AppDao().cultId(Cult)
        val variId = appDb.AppDao().variId(Vari)


        if(Exp.isNotEmpty() && saison!!.isNotEmpty() && duree.isNotEmpty() && nomPar.isNotEmpty() && fillLayerPointList.isNotEmpty() && Cult.isNotEmpty() && CulturePrec.isNotEmpty() && Vari.isNotEmpty() && TypeDeSol.isNotEmpty()){
            val exploitation = Exploitation(
                null,Exp
            )
            GlobalScope.launch ( Dispatchers.IO ){
                appDb.AppDao().insertExploitation(exploitation)
            }

            val expId = appDb.AppDao().expId(Exp)
            /////////////////////////////////////////////////////////////

            Log.d("testingData", "saveParcelle: ${fillLayerPointList}")
            val parcelle = Parcelle(
                null,nomPar,DtSe,Polygon.fromLngLats(listOfList as ArrayList<List<Point?>>).toJson().toString(),expId,solId
            )
            GlobalScope.launch ( Dispatchers.IO ){
                appDb.AppDao().insertParcelle(parcelle)
            }

            val parId =  appDb.AppDao().parId(nomPar)
        ////////////////////////////////////////////////////////////////
            val saison = Saison(
                null,DtSai,duree.toInt(),parId,cultId
            )
            GlobalScope.launch ( Dispatchers.IO ){
                appDb.AppDao().insertSaison(saison)
            }

        /////////////////////////////////
            val cross = CultureVarieteCrossRef(
                cultId,variId
            )
            GlobalScope.launch ( Dispatchers.IO ){
                appDb.AppDao().insertCross(cross)
            }
            Toast.makeText(requireContext(), "Successfully written",Toast.LENGTH_SHORT).show()

        }else{
            Toast.makeText(requireContext(),"Please enter Data",Toast.LENGTH_SHORT).show()
        }

    }



/////////////////////////////////////////////////////////////////

    private fun loadSpinnerData() {
        lateinit var culture: List<String>
        lateinit var sol: List<String>
        lateinit var vari: List<String>
        GlobalScope.launch {
            ///////////////////////////////////////////////////////////////////////
            culture= appDb.AppDao().mesCultures()

            var spin1=dialog.findViewById<Spinner>(R.id.culture)
            var spin2=dialog.findViewById<Spinner>(R.id.culture_prec)

            val dataAdapter= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,culture)

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spin1?.setAdapter(dataAdapter)
            spin2?.setAdapter(dataAdapter)
            ///////////////////////////////////////////////////////////////////////
            sol= appDb.AppDao().mesSol()

            var spin3=dialog.findViewById<Spinner>(R.id.type_sol)

            val dataA= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,sol)

            dataA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spin3?.setAdapter(dataA)
            //////////////////////////////////////////////////////////////////
            vari= appDb.AppDao().mesVar()

            var spin4=dialog.findViewById<Spinner>(R.id.variete)

            val dataAd= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,vari)

            dataAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spin4?.setAdapter(dataAd)

        }
    }

///////////////////////////////////////////////////
    fun DatePicker.getFullDate(seperator : Char = '-') =
        "$year$seperator${
            month.plus(1).let<Int, Any> { if (it < 10) "0$it" else it }
        }$seperator${dayOfMonth.let<Int, Any> { if (it < 10) "0$it" else it }}"

////////////////////////////////////////////////////


}




