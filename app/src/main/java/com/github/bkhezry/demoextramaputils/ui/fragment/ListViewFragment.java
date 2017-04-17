package com.github.bkhezry.demoextramaputils.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.bkhezry.demoextramaputils.R;
import com.github.bkhezry.demoextramaputils.ui.MapsActivity;
import com.github.bkhezry.demoextramaputils.utils.AppUtils;
import com.github.bkhezry.extramaputils.builder.OptionViewBuilder;
import com.github.bkhezry.extramaputils.model.OptionView;
import com.github.bkhezry.extramaputils.utils.MapUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;

public class ListViewFragment extends Fragment {
    private ListFragment mList;
    private MapAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view,
                container, false);
        mAdapter = new MapAdapter(getActivity(), LIST_OPTION_VIEW);
        mList = (ListFragment) getChildFragmentManager().findFragmentById(R.id.list);
        mList.setListAdapter(mAdapter);
        AbsListView lv = mList.getListView();
        lv.setRecyclerListener(mRecycleListener);
        return view;
    }

    public Fragment newInstance() {
        return new ListViewFragment();
    }

    private class MapAdapter extends ArrayAdapter<OptionView> {

        private final HashSet<MapView> mMaps = new HashSet<>();
        private OptionView[] optionViews;

        public MapAdapter(Context context, OptionView[] optionViews) {
            super(context, R.layout.list_item, R.id.titleTextView, optionViews);
            this.optionViews = optionViews;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;


            if (row == null) {
                row = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);
                holder = new ViewHolder(row);
                row.setTag(holder);
                holder.initializeMapView();
                mMaps.add(holder.mapView);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            OptionView optionView = optionViews[position];
            holder.mapView.setTag(optionView);

            if (holder.map != null) {
                setMapLocation(optionView, holder.map);
            }

            holder.title.setText(optionView.getTitle());

            return row;
        }

        public HashSet<MapView> getMaps() {
            return mMaps;
        }
    }

    private static void setMapLocation(OptionView optionView, GoogleMap googleMap) {
        MapUtils.showElements(optionView, googleMap);
    }

    private class ViewHolder implements OnMapReadyCallback {
        MapView mapView;
        TextView title;
        GoogleMap map;

        private ViewHolder(View view) {
            mapView = (MapView) view.findViewById(R.id.mapLite);
            title = (TextView) view.findViewById(R.id.titleTextView);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getActivity());
            map = googleMap;
            final OptionView optionView = (OptionView) mapView.getTag();
            if (optionView != null) {
                setMapLocation(optionView, map);
            }
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Bundle args = new Bundle();
                    args.putParcelable("optionView", optionView);
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    intent.putExtra("args", args);
                    startActivity(intent);
                }
            });
        }

        private void initializeMapView() {
            if (mapView != null) {
                mapView.onCreate(null);
                mapView.getMapAsync(this);
            }
        }

    }

    private AbsListView.RecyclerListener mRecycleListener = new AbsListView.RecyclerListener() {

        @Override
        public void onMovedToScrapHeap(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder != null && holder.map != null) {
                holder.map.clear();
            }

        }
    };

    private static OptionView[] LIST_OPTION_VIEW = {
            new OptionViewBuilder()
                    .withTitle("1")
                    .withCenterCoordinates(new LatLng(35.6892, 51.3890))
                    .withMarkers(AppUtils.getListExtraMarker())
                    .withForceCenterMap(false)
                    .withIsListView(true)
                    .build(),
            new OptionViewBuilder()
                    .withTitle("2")
                    .withCenterCoordinates(new LatLng(35.6892, 51.3890))
                    .withPolygons(
                            AppUtils.getPolygon_1(),
                            AppUtils.getPolygon_2()
                    )
                    .withForceCenterMap(false)
                    .withIsListView(true)
                    .build(),
            new OptionViewBuilder()
                    .withTitle("3")
                    .withCenterCoordinates(new LatLng(35.6892, 51.3890))
                    .withPolylines(
                            AppUtils.getPolyline_1(),
                            AppUtils.getPolyline_2()
                    )
                    .withForceCenterMap(false)
                    .withIsListView(true)
                    .build(),
            new OptionViewBuilder()
                    .withTitle("4")
                    .withCenterCoordinates(new LatLng(35.6892, 51.3890))
                    .withMarkers(AppUtils.getListMarker())
                    .withPolylines(AppUtils.getPolyline_3())
                    .withForceCenterMap(false)
                    .withIsListView(true)
                    .build()
    };
}
