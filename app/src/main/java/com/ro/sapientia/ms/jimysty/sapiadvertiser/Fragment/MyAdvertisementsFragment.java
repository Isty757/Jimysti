package com.ro.sapientia.ms.jimysty.sapiadvertiser.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Adapter.MyRecyclerViewAdapter;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;

import java.util.ArrayList;
import java.util.SimpleTimeZone;

/**
 * Created by Drako on 20-Nov-17.
 */

public class MyAdvertisementsFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener{

    private static final int RESET_STATE_DELAY_MILLIS = 300;

    private static final String TAG = "AdvertisementsFragment";

    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private FloatingActionButton add;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    protected RecyclerView.LayoutManager mLayoutManager;
    protected MyAdvertisementsFragment.LayoutManagerType mCurrentLayoutManagerType;

    public View rootView;

    private FirebaseUser currentFirebaseUser;


    public MyAdvertisementsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_my_advertisements, container, false);

        // set up the RecyclerView
        recyclerView = rootView.findViewById(R.id.rv_list_my_advertisements);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = MyAdvertisementsFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (MyAdvertisementsFragment.LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        // database references

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if (currentFirebaseUser != null) {
            DatabaseReference myRef = database.getReference(currentFirebaseUser.getUid());

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadAdvertisementsFromDatabase(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.d(TAG, error.getMessage());
                }
            });
        }
        else{
            ArrayList<String> profilPictureList = new ArrayList<>();

            ArrayList<String> titleList = new ArrayList<>();
            ArrayList<String> descriptionList = new ArrayList<>();
            ArrayList<String> imagesList = new ArrayList<>();

            profilPictureList.add("");
            titleList.add("This will be you advertisement's title but before you have to...");
            descriptionList.add("And this will be you advertisement description but before you have to...");
            imagesList.add("https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/SignIn.jpg?alt=media&token=e4008fe5-aaa8-4a8f-8582-3cf8a71c303e");
            adapter = new MyRecyclerViewAdapter(rootView.getContext(), profilPictureList, titleList, descriptionList, imagesList);
            adapter.setClickListener(MyAdvertisementsFragment.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        add = rootView.findViewById(R.id.fbt_add_event);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFirebaseUser != null){
                    StaticMethods.goToCreateNewAdvertisementActivity(getActivity());
                } else{
                    dialogIfUserWantToLogin();
                }
            }
        });

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

        return rootView;
    }
    private void dialogIfUserWantToLogin(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Click Yes to Log In");
        // set dialog message
        alertDialogBuilder
                .setMessage("To add a new advertisement you have to be logged in!")
                .setCancelable(false)
                .setPositiveButton("Go to login",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        StaticMethods.goToLoginScreenActivity(getActivity());
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Stay here",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void loadAdvertisementsFromDatabase(DataSnapshot dataSnapshot) {
        ArrayList<String> profilPictureList = new ArrayList<>();

        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> descriptionList = new ArrayList<>();
        ArrayList<String> imagesList = new ArrayList<>();

        try {
            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                String profilPicture = (String) messageSnapshot.child("googleUser").child("image").getValue();

                String title = (String) messageSnapshot.child("title").getValue();
                String description = (String) messageSnapshot.child("description").getValue();

                ArrayList<String> images = (ArrayList<String>) messageSnapshot.child("images").getValue();

                titleList.add(title);
                descriptionList.add(description);
                imagesList.add(images.get(0));
                profilPictureList.add(profilPicture);

            }
            adapter = new MyRecyclerViewAdapter(rootView.getContext(), profilPictureList, titleList, descriptionList, imagesList);
            adapter.setClickListener(MyAdvertisementsFragment.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setRecyclerViewLayoutManager(MyAdvertisementsFragment.LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = MyAdvertisementsFragment.LayoutManagerType.LINEAR_LAYOUT_MANAGER;

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
        if (currentFirebaseUser != null) {
            if (adapter.isClickable) {
                adapter.isClickable = false;
                view.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.button_click_animation));
                StaticMethods.goToTellAboutAdvertisementActivity(rootView.getContext(), adapter.getItem(position));
            }
        }
        else{
            view.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.button_click_animation));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialogIfUserWantToLogin();
                }
            }, RESET_STATE_DELAY_MILLIS);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.isClickable = true;
        }
    }
}
