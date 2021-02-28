package dam95.android.uk.firstbyte.model.builds

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dam95.android.uk.firstbyte.datasource.FirstByteRepository

/**
 *
 */
class PCBuildListViewModel(application: Application): AndroidViewModel(application) {
    private val repository: FirstByteRepository = FirstByteRepository(application)
   // var personalPCLiveList: LiveData<List<PCbuild>> = repository.retrieveAllPCs()

}