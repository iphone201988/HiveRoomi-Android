package com.tech.hive.base.location



import android.content.Context
import android.util.Log
import android.widget.AutoCompleteTextView
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.geo.AmazonLocationClient
import com.amazonaws.services.geo.model.SearchPlaceIndexForTextRequest
import kotlinx.coroutines.*

class LocationSearchManager(
    private val context: Context,
    identityPoolId: String,
    region: Regions,
    private val placeIndexName: String
) {
    private val client: AmazonLocationClient
    private val searchScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    init {
        val credentialsProvider = CognitoCachingCredentialsProvider(context, identityPoolId, region)
        client = AmazonLocationClient(credentialsProvider)
        client.setRegion(com.amazonaws.regions.Region.getRegion(region))
    }

    fun setupSearch(
        autoCompleteTextView: AutoCompleteTextView,
        onResultSelected: (String, Coordinate) -> Unit
    ) {
        autoCompleteTextView.threshold = 2

        autoCompleteTextView.addTextChangedListener(object : android.text.TextWatcher {
            private var searchJob: Job? = null

            override fun afterTextChanged(s: android.text.Editable?) {
                val query = s.toString()
                if (query.length < 3) return

                searchJob?.cancel()
                searchJob = searchScope.launch {
                    try {
                        val request = SearchPlaceIndexForTextRequest()
                            .withIndexName(placeIndexName)
                            .withText(query)
                            .withMaxResults(5)

                        val result = client.searchPlaceIndexForText(request)
                        val addresses = result.results.mapNotNull {
                            val label = it.place.label
                            val point = it.place.geometry.point
                            if (label != null && point != null) {
                                label to Coordinate(point[1], point[0]) // lat, lon
                            } else null
                        }

                        withContext(Dispatchers.Main) {
                            val adapter = LocationSuggestionAdapter(context, addresses)
                            autoCompleteTextView.setAdapter(adapter)
                            adapter.notifyDataSetChanged()

                            autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                                val (label, coordinate) = addresses[position]
                                autoCompleteTextView.setText(label, false)
                                onResultSelected(label, coordinate)
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("LocationSearchManager", "Error: ${e.localizedMessage}", e)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }



}




data class Coordinate(val latitude: Double, val longitude: Double)





