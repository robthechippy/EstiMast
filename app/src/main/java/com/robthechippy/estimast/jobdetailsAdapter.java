package com.robthechippy.estimast;

import android.app.FragmentManager;
import android.app.Fragment;
import android.support.v13.app.FragmentPagerAdapter;
import android.content.Context;

/**
 * Created by Jarvises on 7/02/2015.
 */
public class jobdetailsAdapter extends FragmentPagerAdapter {

    private static String jobId="";
    private static String clientId="";

    public jobdetailsAdapter(FragmentManager mgr, String jobID, String clientID){
        super(mgr);

        jobId=jobID;
        clientId=clientID;

    }

    @Override
    public int getCount() {
        return (3);
    }

    @Override
    public Fragment getItem(int postition) {

        if (postition == 0) {
            return (Fragment_Job_Details.newInstance(postition, jobId, clientId));
        }else if(postition == 1){
            return (SectionListFragment.newInstance(postition, jobId, clientId));
        } else {
            return (Fragment_jobs_Attachments.newInstance(postition));
        }
    }

    @Override
    public String getPageTitle(int position){

        if(position==0){
            return(Fragment_Job_Details.getTitle(position));

        }else if(position == 1) {
            return(SectionListFragment.getTitle(position));
        }else{
            return(Fragment_jobs_Attachments.getTitle(position));
        }
    }
}
