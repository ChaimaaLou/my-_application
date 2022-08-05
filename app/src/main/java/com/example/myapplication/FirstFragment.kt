package com.example.myapplication


import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.database.AppDatabase
import com.example.myapplication.databinding.FragmentFirstBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.geojson.*
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), OnMapReadyCallback {

    private var mapboxMap: MapboxMap? = null

    private var _binding: FragmentFirstBinding? = null
    private lateinit var dialog: BottomSheetDialog

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var appDb: AppDatabase
    private val FILL_SOURCE_ID = "fill-source-id"
    private val FILL_LAYER_ID = "fill-layer-polygon-id"
    private val CIRCLE_SOURCE_ID = "circle-source-id"
    private val LINE_SOURCE_ID = "line-source-id"
    private val CIRCLE_LAYER_ID = "circle-layer-id"
    private val LINE_LAYER_ID = "line-layer-id"

    private var lineSource: GeoJsonSource? = null
    private var fillSource: GeoJsonSource? = null
    private var circleSource: GeoJsonSource? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        appDb = AppDatabase.getDatabase(requireContext())
        loadSpinnerData()

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        dialog = BottomSheetDialog(requireContext())
        binding.second.setOnClickListener {
            showBottomSheetDialog()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Map

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
                ), 2000
            )
            circleSource = initCircleSource(style)
            fillSource = initFillSource(style)
            lineSource = initLineSource(style)

            initCircleLayer(style)
            initLineLayer(style)
            initFillLayer(style)

            initSpinnerClickListeners(style)

        }

    }


///////////////////////////////////////////////////////////Spinner
lateinit var spin : Spinner

    private fun loadSpinnerData() {

        lateinit var parcelle: List<String>

        GlobalScope.launch(Dispatchers.Unconfined) {
            ///////////////////////////////////////////////////////////////////////
            parcelle = appDb.AppDao().mesParcelles()

             spin = binding.parcelle

             val dataAdapterj =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, parcelle)

            dataAdapterj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spin?.setAdapter(dataAdapterj)

            dataAdapterj.notifyDataSetChanged()


        }
    }


    //////////////////////////////////////Style
    private fun initFillSource(loadedMapStyle: Style): GeoJsonSource {
        val fillFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val fillGeoJsonSource = GeoJsonSource(FILL_SOURCE_ID, fillFeatureCollection)
        loadedMapStyle.addSource(fillGeoJsonSource)
        return fillGeoJsonSource
    }

    private fun initCircleSource(loadedMapStyle: Style): GeoJsonSource {
        val circleFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val circleGeoJsonSource = GeoJsonSource(CIRCLE_SOURCE_ID, circleFeatureCollection)
        loadedMapStyle.addSource(circleGeoJsonSource)
        return circleGeoJsonSource
    }

    private fun initLineSource(loadedMapStyle: Style): GeoJsonSource {
        val lineFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val lineGeoJsonSource = GeoJsonSource(LINE_SOURCE_ID, lineFeatureCollection)
        loadedMapStyle.addSource(lineGeoJsonSource)
        return lineGeoJsonSource
    }

    ////////////////////////////////////////////////
    private fun initFillLayer(loadedMapStyle: Style) {
        val fillLayer = FillLayer(
            FILL_LAYER_ID,
            FILL_SOURCE_ID
        )
        fillLayer.setProperties(
            PropertyFactory.fillOpacity(.6f),
            PropertyFactory.fillColor(Color.parseColor("#00e9ff"))
        )
        loadedMapStyle.addLayerBelow(fillLayer, LINE_LAYER_ID)
    }


    private fun initLineLayer(loadedMapStyle: Style) {
        val lineLayer = LineLayer(
            LINE_LAYER_ID,
            LINE_SOURCE_ID
        )
        lineLayer.setProperties(
            PropertyFactory.lineColor(Color.WHITE),
            PropertyFactory.lineWidth(5f)
        )
        loadedMapStyle.addLayerBelow(lineLayer, CIRCLE_LAYER_ID)
    }

    private fun initCircleLayer(loadedMapStyle: Style) {
        val circleLayer = CircleLayer(
            CIRCLE_LAYER_ID,
            CIRCLE_SOURCE_ID
        )
        circleLayer.setProperties(
            PropertyFactory.circleRadius(7f),
            PropertyFactory.circleColor(Color.parseColor("#d004d3"))
        )
        loadedMapStyle.addLayer(circleLayer)
    }

    /////////////////////////////////////////////////////Visualiser
    private fun initSpinnerClickListeners(loadedMapStyle: Style) {
        val spinner = binding.parcelle
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                val selectedItem = spinner.getSelectedItem().toString()

                //Log.d("testingData", "onItemSelected: ${appDb.AppDao().getGeo()}")
                //val polygon: Polygon? = appDb.AppDao().getGeo()?.let { Polygon.fromJson(it) }
                val polygon: Polygon? =
                    appDb.AppDao().getplot(selectedItem)?.let { Polygon.fromJson(it) }
                //Log.d("testingData", "yarbi i koun polygon: ${Polygon.fromJson(polygon.toString()).coordinates()[0]}")

                if (lineSource != null) {
                    lineSource!!.setGeoJson(
                        FeatureCollection.fromFeatures(
                            arrayOf(
                                Feature.fromGeometry(
                                    polygon?.coordinates()?.get(0)
                                        ?.let { LineString.fromLngLats(it) })
                            )
                        )
                    )
                }


                val finalFeatureList: MutableList<Feature> = ArrayList()
                finalFeatureList.add(
                    Feature.fromGeometry(
                        polygon?.coordinates()?.get(0)?.let { LineString.fromLngLats(it) })
                )
                val newFeatureCollection = FeatureCollection.fromFeatures(finalFeatureList)
                if (fillSource != null) {
                    fillSource!!.setGeoJson(newFeatureCollection)

                }

                zoomOnPlot(polygon!!)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

    }

    /////////////////////////////////////////////////////
    fun toCenterPlot(polygon: Polygon): LatLng {

        val pointsList: MutableList<Point?> = ArrayList()

        if (polygon != null) {
            polygon.coordinates()[0].forEach {

                pointsList.add(it)
            }
        }

        return getCenterByPlot(pointsList)
    }

    private fun getCenterByPlot(plotLocations: MutableList<Point?>): LatLng {

        val listLatLngCenters = ArrayList<LatLng>()
        plotLocations.forEach {
            if (it != null) {
                listLatLngCenters.add(LatLng(it.latitude(), it.longitude()))
            }
        }

        return getPolygonCenterPoint(listLatLngCenters)
    }

    private fun getPolygonCenterPoint(polygonPointsList: ArrayList<LatLng>): LatLng {
        val centerLatLng: LatLng?
        val builder: LatLngBounds.Builder = LatLngBounds.Builder()
        for (i in 0 until polygonPointsList.size) {
            builder.include(polygonPointsList[i])
        }
        val bounds: LatLngBounds = builder.build()
        centerLatLng = bounds.center
        return centerLatLng
    }

    /////////////////////////////////////////////////////////////////////////////
    private fun convertPolygonToList(polygon: Polygon?): MutableList<Point?> {

        val pointsList: MutableList<Point?> = ArrayList()

        if (polygon != null) {
            polygon.coordinates()[0].forEach {

                pointsList.add(it)

            }
        }

        return pointsList
    }

    private fun zoomOnPlot(polygon: Polygon) {

        val listLatLngMarkers = ArrayList<LatLng>()
        convertPolygonToList(polygon).forEach {
            if (it != null) {
                listLatLngMarkers.add(LatLng(it.latitude(), it.longitude()))
            }
        }
        mapboxMap!!.easeCamera(
            CameraUpdateFactory.newLatLngBounds(
                LatLngBounds.Builder().includes(listLatLngMarkers).build(),
                180
            ), 3000
        )
    }

    ///////////////////////////////////////////////////////////
    private fun showBottomSheetDialog() {
        dialog.setContentView(R.layout.bottom_sheet)

        appDb = AppDatabase.getDatabase(requireContext())
        lateinit var parcelle: List<String>

        GlobalScope.launch(Dispatchers.IO) {
            ///////////////////////////////////////////////////////////////////////
            val parcelle2 = appDb.AppDao().mesParcelles()

            var spin2 = dialog.findViewById<Spinner>(R.id.parcelle)

            val dataAdapter2 =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, parcelle2)

            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spin2?.setAdapter(dataAdapter2)

            ///////////////////////////////////////////////////////////////////////
            dialog.findViewById<Button>(R.id.btn_sub)?.setOnClickListener {
                val spinner = dialog.findViewById<Spinner>(R.id.parcelle)?.getSelectedItem().toString()
                val exp = dialog.findViewById<EditText>(R.id.exploitation)?.text.toString()

                val spinnerId = appDb.AppDao().parId(spinner)
                Log.d("testingData", "onItemSelected: ${spinner}")

                appDb.AppDao().changename(spinnerId, exp)
                Toast.makeText(requireContext(), "Successfully written",Toast.LENGTH_SHORT).show()

                dialog.hide()

                refreshSpinner()
            }



        }

        dialog.show()

    }

    private fun refreshSpinner(){
        lateinit var parcelle: List<String>

        binding.parcelle.visibility = View.GONE

        GlobalScope.launch(Dispatchers.Unconfined) {
            ///////////////////////////////////////////////////////////////////////
            parcelle = appDb.AppDao().mesParcelles()

            spin = binding.parcelle2

            val dataAdapterj =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, parcelle)

            dataAdapterj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spin?.setAdapter(dataAdapterj)

            dataAdapterj.notifyDataSetChanged()


        }
    }
}