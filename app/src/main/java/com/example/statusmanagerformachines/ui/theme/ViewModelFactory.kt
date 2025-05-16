/*
 * Фабрика для создания экземпляров ViewModel, используемая в приложении для управления состоянием главного экрана.
 *
 * Основное назначение этого файла:
 *  - Обеспечить механизм инстанцирования ViewModel, который требует передачи зависимостей через конструктор.
 *  - Интегрироваться с Android Architecture Components, используя интерфейс ViewModelProvider.Factory,
 *    что позволяет корректно работать с жизненным циклом Activity или Fragment.
 *
 *  - В Activity или Fragment передается созданный экземпляр этой фабрики в ViewModelProvider,
 *    что обеспечивает получение корректно сконфигурированного экземпляра MainViewModel с необходимыми зависимостями.
 */

package com.example.statusmanagerformachines.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import data.repository.MachineRepository
import data.repository.MachineRepositoryComposite
import ui.main.MainViewModel

class ViewModelFactory(
    private val repository: MachineRepositoryComposite,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}