package com.valleskeyp.tubehub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class DeleteDialogFragment extends DialogFragment {

	static DeleteDialogFragment newInstance() {
		  
		  String title = "Confirm Deletion";
		  
		        DeleteDialogFragment f = new DeleteDialogFragment();
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
		     .setPositiveButton("Delete", 
		       new DialogInterface.OnClickListener() {
		        
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		         ((EditActivity)getActivity()).okClicked();
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