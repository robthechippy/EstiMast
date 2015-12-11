package com.robthechippy.estimast;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by Jarvises on 20/02/2015.
 */
public class sectionDetailsAdapter  extends FragmentPagerAdapter {

    private static String jobId="";
    private static String clientId="";
    private static String sectionId="";

    public sectionDetailsAdapter(FragmentManager mgr, String jobID, String clientID, String sectionID){
        super(mgr);

        jobId=jobID;
        clientId=clientID;
        sectionId = sectionID;

    }

    @Override
    public int getCount() {
        return (3);
    }

    @Override
    public Fragment getItem(int postition) {

        if (postition == 0) {
            return (Fragment_Section_Details.newInstance( postition, jobId, clientId, sectionId));
        }else if(postition == 1){
            //TODO change to components
            return (JobItemListFragment.newInstance(postition, jobId, clientId, sectionId));
        } else {
            //TODO add in items tab
            return (Fragment_jobs_Attachments.newInstance(postition));
        }
    }

    @Override
    public String getPageTitle(int position){

        if(position==0){
            return(Fragment_Job_Details.getTitle(position));

        }else if(position == 1) {
            return(JobItemListFragment.getTitle(position));
        }else{
            return(Fragment_jobs_Attachments.getTitle(position));
        }
    }
}
