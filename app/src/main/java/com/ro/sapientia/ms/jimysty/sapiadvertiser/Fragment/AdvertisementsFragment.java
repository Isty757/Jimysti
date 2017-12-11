package com.ro.sapientia.ms.jimysty.sapiadvertiser.Fragment;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

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
import com.ro.sapientia.ms.jimysty.sapiadvertiser.activity.ListScreen;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by Drako on 20-Nov-17.
 */

public class AdvertisementsFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener, MyRecyclerViewAdapter.LongItemClickListener{

    private static final String TAG = "AdvertisementsFragment";

    private boolean blackListWasDownloaded = false;

    private ArrayList<String> blackList = new ArrayList<>();

    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private FloatingActionButton add;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    protected RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;

    private FirebaseUser currentFirebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public View rootView;
    public AdvertisementsFragment() {
        // Required empty public constructor
    }

    private String searchTitle = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_advertisements, container, false);

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
                loadAdvertisementsFromDatabase(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TAG , error.getMessage());
            }
        });

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        add = rootView.findViewById(R.id.fbt_add_event);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFirebaseUser != null){
                    //StaticMethods.goToCreateNewAdvertisementActivity(getActivity());
                    StaticMethods.goToProfile(getActivity());
                } else{
                    dialogIfUserWantToLogin();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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


        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if (currentFirebaseUser != null) {
            myRef = database.getReference(currentFirebaseUser.getUid()).child("BlackList");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> downloadedBlackList = (ArrayList<String>) dataSnapshot.getValue();
                    if (downloadedBlackList != null) {
                        blackList = downloadedBlackList;
                        if (!blackListWasDownloaded) {
                            blackListWasDownloaded = true;
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(AdvertisementsFragment.this).attach(AdvertisementsFragment.this).commit();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }

        return rootView;
    }
    public void setSearchText(Bundle msg){
        if (msg == null){
            searchTitle = null;
        }
        else {
            searchTitle = msg.getString("Search");
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
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
        ArrayList<String> id = new ArrayList<>();

        try {
            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                if ( searchTitle == null) {
                    Log.d(TAG , "Rektifikal");
                    String profilPicture = (String) messageSnapshot.child("googleUser").child("image").getValue();
                    String title = (String) messageSnapshot.child("title").getValue();
                    String description = (String) messageSnapshot.child("description").getValue();
                    String sId = (String) messageSnapshot.child("id").getValue();

                    ArrayList<String> images = (ArrayList<String>) messageSnapshot.child("images").getValue();


                    Log.d("ADVFRAGMENT", blackList.size() + "");
                    if (blackList.size() != 0) {
                        Log.d("ADVFRAGMENT", blackList.size() + "");
                        if (!blackList.contains(sId)) {
                            Log.d("ADVFRAGMENT", sId);
                            profilPictureList.add(profilPicture);
                            titleList.add(title);
                            descriptionList.add(description);
                            imagesList.add(images.get(0));
                            id.add(sId);
                        } else {
                            Log.d("ADVFRAGMENT", "Ide is jott" + sId);
                        }
                    }
                    else{
                        Log.d("ADVFRAGMENT", "Itt van");
                        profilPictureList.add(profilPicture);
                        titleList.add(title);
                        descriptionList.add(description);
                        imagesList.add(images.get(0));
                        id.add(sId);
                    }
                    /*
                    profilPictureList.add(profilPicture);
                    titleList.add(title);
                    descriptionList.add(description);
                    imagesList.add(images.get(0));
                    id.add(sId);
                    */
                }
                else{
                    Log.d(TAG , searchTitle.toLowerCase());
                    String profilPicture = (String) messageSnapshot.child("googleUser").child("image").getValue();
                    String title = (String) messageSnapshot.child("title").getValue();

                    String description = (String) messageSnapshot.child("description").getValue();
                    String sId = (String) messageSnapshot.child("id").getValue();

                    ArrayList<String> images = (ArrayList<String>) messageSnapshot.child("images").getValue();
                    Log.d(TAG , title.toLowerCase());

                    if (title.toLowerCase().contains(searchTitle.toLowerCase())){
                        profilPictureList.add(profilPicture);
                        titleList.add(title);
                        descriptionList.add(description);
                        imagesList.add(images.get(0));
                        id.add(sId);
                    }
                }
            }
            adapter = new MyRecyclerViewAdapter(rootView.getContext(), profilPictureList, titleList, descriptionList, imagesList, id);
            adapter.setClickListener(AdvertisementsFragment.this);
            adapter.setLongClickListener(AdvertisementsFragment.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.d(TAG , e.getMessage());
        }
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
        if (adapter.isClickable) {
            Log.d("AboutAdvertisement2", adapter.getItem(position));
            adapter.isClickable = false;
            view.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.button_click_animation));
            getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            StaticMethods.goToTellAboutAdvertisementActivity(rootView.getContext(), adapter.getItem(position));
        }
    }
    @Override
    public boolean onLongItemClick(View view, final int position) {
        Log.d("AboutAdvertisement2222", adapter.getItem(position));

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if (currentFirebaseUser != null) {
            database = FirebaseDatabase.getInstance();
            database.getReference(currentFirebaseUser.getUid()).child("BlackList").setValue(blackList);

            myRef = database.getReference(currentFirebaseUser.getUid()).child("BlackList");
            blackList.add(adapter.getItem(position));
            myRef.setValue(blackList);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(AdvertisementsFragment.this).attach(AdvertisementsFragment.this).commit();
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.isClickable = true;
        }
    }
}
