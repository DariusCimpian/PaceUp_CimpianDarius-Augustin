package com.example.paceup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paceup.data.repository.RunRepository
import com.example.paceup.model.Run
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RunViewModel : ViewModel() {
    private val repository = RunRepository()

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds

    private val _runHistory = MutableStateFlow<List<Run>>(emptyList())
    val runHistory: StateFlow<List<Run>> = _runHistory

    private val _isLoadingHistory = MutableStateFlow(false)
    val isLoadingHistory: StateFlow<Boolean> = _isLoadingHistory

    fun updateDistance(km: Double) { _distanceKm.value = km }
    fun updateElapsedSeconds(seconds: Int) { _elapsedSeconds.value = seconds }
    fun setRunning(running: Boolean) { _isRunning.value = running }
    fun calculateXp(km: Double): Int = (km * 100).toInt()

    fun getPaceString(distanceKm: Double, seconds: Int): String {
        if (distanceKm < 0.01) return "--:--"
        val paceSeconds = (seconds / distanceKm).toInt()
        val paceMin = paceSeconds / 60
        val paceSec = paceSeconds % 60
        return "%d:%02d min/km".format(paceMin, paceSec)
    }

    fun saveRun() {
        viewModelScope.launch {
            val xp = calculateXp(_distanceKm.value)
            repository.saveRun(_distanceKm.value, xp, _elapsedSeconds.value)
            _isSaved.value = true
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _isLoadingHistory.value = true
            val result = repository.getUserRuns()
            if (result.isSuccess) _runHistory.value = result.getOrNull() ?: emptyList()
            _isLoadingHistory.value = false
        }
    }

    fun reset() {
        _distanceKm.value = 0.0
        _elapsedSeconds.value = 0
        _isRunning.value = false
        _isSaved.value = false
    }
}