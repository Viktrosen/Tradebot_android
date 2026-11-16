package ru.bolotov.tradebot.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import ru.bolotov.tradebot.di.components.AppNavigationDependencies
import ru.bolotov.tradebot.di.components.DaggerAppNavigationComponent

/** Supplies the Activity-owned restored handle to the existing Dagger graph. */
internal class AppViewModelFactory(private val dependencies: AppNavigationDependencies) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        require(modelClass == AppViewModel::class.java)
        val model =
            DaggerAppNavigationComponent.builder()
                .dependencies(dependencies)
                .savedStateHandle(extras.createSavedStateHandle())
                .build()
                .viewModel
        return modelClass.cast(model)!!
    }
}
