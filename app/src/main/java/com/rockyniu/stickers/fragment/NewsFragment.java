package com.rockyniu.stickers.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.rockyniu.stickers.R;
import com.rockyniu.stickers.activity.EditLinkActivity;
import com.rockyniu.stickers.adapter.LinkListAdapter;
import com.rockyniu.stickers.database.LinkDataSource;
import com.rockyniu.stickers.listener.OnFragmentInteractionListener;
import com.rockyniu.stickers.listener.SwipeDismissListViewTouchListener;
import com.rockyniu.stickers.model.Link;
import com.rockyniu.stickers.util.ToastHelper;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class NewsFragment extends BaseFragment implements AbsListView.OnItemClickListener {

    private static final int REQUEST_EDIT_LINK = 1001;
    // TODO: Rename parameter arguments, choose names that match
    private static final String USER_ID = "userId";
    private static final int LINK_TYPE = 0;

    // TODO: Rename and change types of parameters
    private static String userId;

    private LinkDataSource linkDataSource;
    private List<Link> links;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private LinkListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static NewsFragment newInstance(String userId) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
        }

        linkDataSource = new LinkDataSource(getActivity());
        links = linkDataSource.getList(LINK_TYPE);

        if (links.isEmpty()) {
            Link link = new Link();
            link.setId(UUID.randomUUID().toString());
            link.setTitle(getResources().getString(R.string.cctvNews));
            link.setAddress(getResources().getString(R.string.cctvNews_address));
            link.setLinkType(LINK_TYPE);
            link.setModifiedTime(Calendar.getInstance()
                    .getTimeInMillis());
            linkDataSource.insertItemWithId(link);
            links.add(link);
        }
        // Adapter to display content
        mAdapter = new LinkListAdapter(getActivity(), links);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_record, container, false);

        setHasOptionsMenu(true); // must for additional menu of fragment

        // Set the adapter
        mListView = (AbsListView) rootView.findViewById(R.id.list_records);
        mListView.setAdapter(mAdapter);

//        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        // long cilck action
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Link currentLink = mAdapter
                        .getItem(position);
                String itemId = currentLink.getId();
                editItem(itemId);
                return true;
            }
        });

        // swipe to delete tasks
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
                (ListView) mListView,
                new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListView listView,
                                          int[] reverseSortedPositions) {

                        // Delete all dismissed tasks
                        for (int position : reverseSortedPositions) {
                            LinkListAdapter tasksAdapter = (LinkListAdapter) mListView
                                    .getAdapter();
                            final Link currentLink = tasksAdapter
                                    .getItem(position);

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Delete Link")
                                    .setMessage("Are you sure you want to delete?")
                                    .setNegativeButton(android.R.string.no, null)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface arg0, int arg1) {
                                            // label delete
                                            currentLink.setModifiedTime(Calendar.getInstance()
                                                    .getTimeInMillis());
                                            linkDataSource
                                                    .labelItemDeletedWithModifiedTime(currentLink);
                                            ToastHelper.showToastInternal(
                                                    getActivity(),
                                                    "Link deleted.");
                                            refreshView();
                                        }
                                    }).create().show();
                        }
                    }
                });
        mListView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during
        // ListView scrolling,
        // we don't look for swipes.
        mListView.setOnScrollListener(touchListener.makeScrollListener());

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_link:
                editItem("");
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NewsFragment.REQUEST_EDIT_LINK
                && resultCode == Activity.RESULT_OK) {
            refreshView();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
            mListener.onFragmentInteraction(links.get(position).getId());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    // add or edit Item
    private void editItem(String linkId) {
        Intent myIntent = new Intent(this.getActivity(), EditLinkActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("com.rockyniu.stickers.userId", userId);
        bundle.putString("com.rockyniu.stickers.linkId", linkId);
        bundle.putInt("com.rockyniu.stickers.linkType", LINK_TYPE);
        myIntent.putExtras(bundle);
        startActivityForResult(myIntent, REQUEST_EDIT_LINK);
    }

    private void refreshView() {
        links = linkDataSource.getList(LINK_TYPE);
        mAdapter.updateList(links);
        mAdapter.notifyDataSetChanged();
    }

}
