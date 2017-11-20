package com.ro.sapientia.ms.jimysty.sapiadvertiser.Fragment;



import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Adapter.MyRecyclerViewAdapter;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by Drako on 20-Nov-17.
 */

public class AdvertisementsFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener{

    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FloatingActionButton add;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
    private static final int SPAN_COUNT = 2;

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    protected RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;

    public View rootView;
    public AdvertisementsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_advertisements, container, false);
        //rootView.setTag(TAG);

        // set up the RecyclerView
        recyclerView = rootView.findViewById(R.id.rv_list_advertisements);
        recyclerView.setItemAnimator(new SlideInUpAnimator());

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);


        // database references
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Advertisements");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> titleList = new ArrayList<>();
                ArrayList<String> descriptionList = new ArrayList<>();
                ArrayList<String> imagesList = new ArrayList<>();

                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    String title = (String) messageSnapshot.child("title").getValue();
                    String description = (String) messageSnapshot.child("description").getValue();

                    ArrayList<String> images = (ArrayList<String>) messageSnapshot.child("images").getValue();
                    //Advertisement myAdvertisement = new Advertisement(title , description , images);
                    titleList.add(title);
                    descriptionList.add(description);
                    imagesList.add(images.get(0));
                }
                adapter = new MyRecyclerViewAdapter(rootView.getContext(), titleList , descriptionList , imagesList);
                adapter.setClickListener(AdvertisementsFragment.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        add = rootView.findViewById(R.id.fbt_add_event);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 ||dy<0 && add.isShown())
                    add.hide();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    add.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticMethods.goToCreateNewAdvertisementActivity(getActivity());
            }
        });

        // Inflate the layout for this fragment
        return rootView;


    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onItemClick(View view, int position) {
        StaticMethods.goToTellAboutAdvertisementActivity(rootView.getContext() , adapter.getItem(position));
    }
}
