package com.c242ps518.tanami.ui.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.c242ps518.tanami.R
import com.c242ps518.tanami.databinding.FragmentHomeBinding
import com.c242ps518.tanami.ui.adapters.ArticlesAdapter
import com.c242ps518.tanami.ui.adapters.LastGenerateAdapter
import com.c242ps518.tanami.ui.factory.HistoryViewModelFactory
import com.c242ps518.tanami.ui.factory.HomeViewModelFactory
import com.c242ps518.tanami.ui.main.history.HistoryViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var locationRequest: LocationRequest
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.green)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }

        homeViewModel = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory.getInstance()
        )[HomeViewModel::class.java]

        historyViewModel = ViewModelProvider(
            requireActivity(),
            HistoryViewModelFactory.getInstance(requireContext())
        )[HistoryViewModel::class.java]
        historyViewModel.getArticles()
        historyViewModel.get5History()

        getLocationAccess()

        homeViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            if (location.latitude != null && location.longitude != null) {
                homeViewModel.getWeather(
                    location.latitude,
                    location.longitude,
                    "dd5ffe5135d0ad030b6ac240733df87d"
                )
            }
        }

        homeViewModel.error.observe(viewLifecycleOwner) {
            showToast(it)
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        historyViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        val weatherCard = binding.cardWeather
        homeViewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            Log.d("HomeFragment", "Weather data: $weatherData")
            if (weatherData != null) {
                weatherCard.tvCityName.text =
                    String.format("%s, %s", weatherData.name, weatherData.sys?.country)
                weatherCard.tvTemperature.text = weatherData.main?.temp?.let { kelvinToCelsius(it) }
                weatherCard.tvWeatherDescription.text =
                    weatherData.weather?.get(0)?.description ?: "No description available"
                weatherCard.tvFeelsLike.text = getString(
                    R.string.feels_like,
                    weatherData.main?.feelsLike?.let { kelvinToCelsius(it) })
                Glide.with(requireContext())
                    .load(
                        "https://rodrigokamada.github.io/openweathermap/images/${
                            weatherData.weather?.get(
                                0
                            )?.icon
                        }_t@4x.png"
                    )
                    .into(weatherCard.ivWeatherIcon)

                Log.d("HomeFragment", "rain: ${weatherData.rain?.jsonMember1h}")
            }
        }

        historyViewModel.articles.observe(viewLifecycleOwner){
            val adapter = ArticlesAdapter()
            binding.recyclerViewArticles.adapter = adapter
            binding.recyclerViewArticles.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            if (!it.isNullOrEmpty()) {
                binding.textArticles.visibility = View.VISIBLE
            } else{
                binding.recyclerViewArticles.visibility = View.GONE
            }
            adapter.submitList(it)
            binding.recyclerViewArticles.setHasFixedSize(true)
        }

        historyViewModel.history5.observe(viewLifecycleOwner){
            val adapter = LastGenerateAdapter()
            binding.recyclerViewHistory.adapter = adapter
            binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

           if (!it.isNullOrEmpty()) {
               binding.textHistory.visibility = View.VISIBLE
            } else{
                binding.recyclerViewHistory.visibility = View.GONE
            }
            adapter.submitList(it)
            binding.recyclerViewHistory.setHasFixedSize(true)
        }

        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            getLocationAccess()
            swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun kelvinToCelsius(kelvin: Double): String {
        val celsius = kelvin - 273.15
        return "%.0f".format(celsius) + "°"
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showToast(getString(R.string.permission_granted))
            getMyLastLocation()
        } else {
            showToast(getString(R.string.permission_denied))
        }
    }

    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationSettings()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getMyLastLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    homeViewModel.addLocation(location.latitude, location.longitude)
                } else {
                    getCurrentAccurateLocation(fusedLocationClient)
                }
            }.addOnFailureListener {
                showToast(getString(R.string.failed_get_location))
                getCurrentAccurateLocation(fusedLocationClient)
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentAccurateLocation(fusedLocationClient: FusedLocationProviderClient) {
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        fusedLocationClient.getCurrentLocation(priority, null).addOnSuccessListener { location ->
            if (location != null) {
                homeViewModel.addLocation(location.latitude, location.longitude)
            } else {
                showToast(getString(R.string.failed_location))
            }
        }
    }

    private fun checkLocationSettings() {
        val priority = Priority.PRIORITY_HIGH_ACCURACY
        val interval = TimeUnit.MINUTES.toMillis(1)
        val maxWaitTime = TimeUnit.MINUTES.toMillis(1)

        locationRequest = LocationRequest.Builder(
            priority,
            interval
        ).apply {
            setMaxUpdateDelayMillis(maxWaitTime)
        }.build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        showToast("Error: ${sendEx.message}")
                    }
                }
            }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    getMyLastLocation()
                }

                RESULT_CANCELED -> {
                    showToast(getString(R.string.gps_enable))
                }
            }
        }


    private fun showToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()

        val window = requireActivity().window
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }

    override fun onResume() {
        super.onResume()
        historyViewModel.get5History()
        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.green)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }
    }
}