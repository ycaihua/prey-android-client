package com.prey.activities;

import com.prey.R;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class TourPageFragment extends Fragment {

	 public static final String ARG_PAGE = "page";

	    /**
	     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
	     */
	    private int mPageNumber;

	    /**
	     * Factory method for this fragment class. Constructs a new fragment for the given page number.
	     */
	    public static TourPageFragment create(int pageNumber) {
	    	TourPageFragment fragment = new TourPageFragment();
	        Bundle args = new Bundle();
	        args.putInt(ARG_PAGE, pageNumber);
	        fragment.setArguments(args);
	        return fragment;
	    }

	    public TourPageFragment() {
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        mPageNumber = getArguments().getInt(ARG_PAGE);
	    }

	    @Override
	    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
	            Bundle savedInstanceState) {
	        // Inflate the layout containing a title and body text.
	    	ViewGroup rootView = null;
	    	switch (mPageNumber) {
			case 0:
				rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_page0, container, false);
				break;

			case 1:
				rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_page1, container, false);
				break;
			case 2:
				rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_page2, container, false);
				break;
			case 3:
				rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_page3, container, false);
				break;
			case 4:
				rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_page4, container, false);
				break;
			case 5:
				rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_page5, container, false);
				break;
			case 6:
				rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_swipe, container, false);
				 
				final ViewGroup rootView2=rootView;
				
				   
				TextView text=(TextView)rootView.findViewById(R.id.tour_already_have_an_account);
				text.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
					 
						
						LinearLayout linearLayout=(LinearLayout) rootView2.findViewById(R.id.content2);
					       
					        linearLayout.removeAllViews();
					        
					        
						FragmentTransaction trans = getFragmentManager().beginTransaction();
                        trans.replace(R.id.content2, ExistingUserFragment.newInstance());
                        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        trans.addToBackStack(null);
                        trans.commit();
								
						
						 
					}
				});
				 
				break;
			default:
				rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_page, container, false);
				break;
 
			}
	    	
	    	
	    	 
	        // Set the title view to show the page number.
	        //((TextView) rootView.findViewById(android.R.id.text1)).setText(
	       //         getString(R.string.title_template_step, mPageNumber + 1));

	        return rootView;
	    }

	    /**
	     * Returns the page number represented by this fragment object.
	     */
	    public int getPageNumber() {
	        return mPageNumber;
	    }
	    
	    public static class ExistingUserFragment extends Fragment {

	        public static ExistingUserFragment newInstance() {
	        	ExistingUserFragment f = new ExistingUserFragment();
	            return f;
	        }

	        @Override
	        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	            //Log.d("DEBUG", "onCreateView");
	        	
	        	ViewGroup rootView = null;
	        	rootView = (ViewGroup)  inflater.inflate(R.layout.fragment_tour_existing_user, container, false);
	        	final ViewGroup rootView2=rootView;
	        	
	        	
	        	TextView text=(TextView)rootView.findViewById(R.id.tour_dont_have_an_account);
				text.setOnClickListener(new View.OnClickListener() {
					
				
					
					@Override
					public void onClick(View v) {
					 
						LinearLayout linearLayout=(LinearLayout) rootView2.findViewById(R.id.content2);
					       
				        linearLayout.removeAllViews();
				        
				        
						FragmentTransaction trans = getFragmentManager().beginTransaction();
                        trans.replace(R.id.content2, NewAccountFragment.newInstance());
                        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        trans.addToBackStack(null);
                        trans.commit();
								
						
						 
					}
				});
				
				
	        	return rootView;
	        }
	    }
	    
	    public static class NewAccountFragment extends Fragment {

	        public static NewAccountFragment newInstance() {
	        	NewAccountFragment f = new NewAccountFragment();
	            return f;
	        }

	        @Override
	        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	            //Log.d("DEBUG", "onCreateView");
	        	
	        	ViewGroup rootView = null;
	        	rootView = (ViewGroup)  inflater.inflate(R.layout.fragment_tour_new_account, container, false);
	        	final ViewGroup rootView2=rootView;
	        	
	        	TextView text=(TextView)rootView.findViewById(R.id.tour_already_have_an_account);
				text.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
					 
						LinearLayout linearLayout=(LinearLayout) rootView2.findViewById(R.id.content2);
					       
				        linearLayout.removeAllViews();
				        
				        
						FragmentTransaction trans = getFragmentManager().beginTransaction();
                        trans.replace(R.id.content2, ExistingUserFragment.newInstance());
                        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        trans.addToBackStack(null);
                        trans.commit();
								
						
						 
					}
				});
				
				
	        	return rootView;
	        }
	    }
	    
}
