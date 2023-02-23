package pl.mateusz.csgoteamgenerator.ListFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.mateusz.csgoteamgenerator.Role;

public class RiflerFragment extends AbstractRoleFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.initialSetup(Role.Rifler, inflater, container);
    }
}