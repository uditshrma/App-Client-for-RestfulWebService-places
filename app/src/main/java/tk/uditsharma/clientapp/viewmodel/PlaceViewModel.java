package tk.uditsharma.clientapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import tk.uditsharma.clientapp.model.PlaceEntry;
import tk.uditsharma.clientapp.repository.PlaceRepository;

public class PlaceViewModel extends ViewModel {

    private PlaceRepository mRepository;

    private LiveData<List<PlaceEntry>> mAllPlaces;

    @Inject
    public PlaceViewModel (PlaceRepository mRepo) {
        this.mRepository = mRepo;
        mAllPlaces = mRepository.getAllPlaces();
    }

    public LiveData<List<PlaceEntry>> getAllPlaces() { return mAllPlaces; }

    public void insert(PlaceEntry place) { mRepository.insert(place); }

    public void delete(PlaceEntry place) { mRepository.delete(place); }
}
