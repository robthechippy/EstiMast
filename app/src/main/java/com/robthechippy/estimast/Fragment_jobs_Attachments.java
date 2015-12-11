package com.robthechippy.estimast;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jarvises on 7/02/2015.
 */
public class Fragment_jobs_Attachments extends Fragment {

    private static final String KEY_POSITION="position";
    //TODO set up to make reuseable by calling back to parent intent

    static Fragment_jobs_Attachments newInstance(int position) {
        Fragment_jobs_Attachments frag=new Fragment_jobs_Attachments();
        Bundle args=new Bundle();

        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.fragment_jobs_attachments, container, false);
        //EditText editor=(EditText)result.findViewById(R.id.editor);
        //int position=getArguments().getInt(KEY_POSITION, -1);

        //editor.setHint(String.format(getString(R.string.hint), position + 1));

        return(result);
    }

    static  String getTitle(int position){
        return("Attachments");
    }
}
