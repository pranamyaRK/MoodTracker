package com.example.moodtracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ButtonStateViewModel : ViewModel() {
    private val _buttonStates = MutableLiveData<MutableMap<String, Boolean>>()
    val buttonStates: LiveData<MutableMap<String, Boolean>> = _buttonStates

    init {
        _buttonStates.value = mutableMapOf()
    }

    fun toggleButtonState(buttonId: String) {
        val currentState = _buttonStates.value ?: mutableMapOf()
        currentState[buttonId] = !(currentState[buttonId] ?: false)
        _buttonStates.value = currentState
    }

    fun getButtonState(buttonId: String): Boolean {
        return _buttonStates.value?.get(buttonId) ?: false
    }
}
