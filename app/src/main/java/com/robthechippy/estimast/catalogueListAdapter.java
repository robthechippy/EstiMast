package com.robthechippy.estimast;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by Jarvises on 22/02/2015.
 */
public class catalogueListAdapter extends FragmentPagerAdapter {


    public catalogueListAdapter(FragmentManager mgr){
        super(mgr);

   }

    @Override
    public int getCount() {
        return (1);
    }

    @Override
    public Fragment getItem(int postition) {

        if (postition == 0) {
            return (fragmentcatalogueitems.newInstance());
        }//else if(postition == 1){
            //return (SectionListFragment.newInstance(postition));
        //} else {
            //return (Fragment_jobs_Attachments.newInstance(postition));
        //}
        //TODO delete when other pages added
        return (fragmentcatalogueitems.newInstance());
    }

    @Override
    public String getPageTitle(int position){

        if(position==0){
            return(fragmentcatalogueitems.getTitle());

        }else if(position == 1) {
            return(SectionListFragment.getTitle(position));
        }else{
            return(Fragment_jobs_Attachments.getTitle(position));
        }
    }
}
