package com.valleskeyp.tubehub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class MyDialogFragment extends DialogFragment {
 
 static MyDialogFragment newInstance() {
  
  String title = "Confirm Logout";
  
        MyDialogFragment f = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return f;
    }

 @Override
 public Dialog onCreateDialog(Bundle savedInstanceState) {
  String title = getArguments().getString("title");
  Dialog myDialog = new AlertDialog.Builder(getActivity())
     .setIcon(R.drawable.ic_launcher)
     .setTitle(title)
     .setPositiveButton("Logout", 
       new DialogInterface.OnClickListener() {
        
        @Override
        public void onClick(DialogInterface dialog, int which) {
         ((MainActivity)getActivity()).okClicked();
        }
       })
     .setNegativeButton("Cancel", 
       new DialogInterface.OnClickListener() {
        
        @Override
        public void onClick(DialogInterface dialog, int which) {
         
        }
       })
     .create();

  return myDialog;
 }
 
}