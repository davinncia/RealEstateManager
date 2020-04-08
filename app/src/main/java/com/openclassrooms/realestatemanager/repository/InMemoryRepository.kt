package com.openclassrooms.realestatemanager.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.view.model_ui.EmptyProperty
import com.openclassrooms.realestatemanager.view.model_ui.PropertyWrapper


class InMemoryRepository {

    private val propertySelectionMutable = MutableLiveData<PropertyWrapper>()

    init {
        //Empty property by default
        propertySelectionMutable.value = EmptyProperty
    }

    // TODO LUCAS Attention le repo manipule un modèle de donnée qui va être utilisé par la vue
    //  Il faudrait changer le type de cette LiveData en `PropertyUi?`(en renommant le nom)
    //  ( PS : pas de soucis qu'un ImMemoryRepo n'ait pas de donnée de base, embrasse le null)
    //  Et changer le type de donnée du viewmodel pour qu'il expose un model comportant uniquement
    //  des Booleans / String / Int (l'id de la propriété, pas les nombres).
    //  ca peut sembler redondant mais ça permet une plus grande flexibilité : tu peux faire évoluer
    //  ta couche repo ou ta couche UI indépendamment de l'autre !
    fun getPropertySelection(): LiveData<PropertyWrapper> = propertySelectionMutable

    fun setPropertySelection(propertyWrapper: PropertyWrapper) {
        propertySelectionMutable.value = propertyWrapper
    }

    //Singleton
    companion object {
        private var INSTANCE: InMemoryRepository? = null

        fun getInstance(): InMemoryRepository {
            if (INSTANCE == null){
                synchronized(InMemoryRepository){
                    if (INSTANCE == null){
                        INSTANCE = InMemoryRepository()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}