package com.example.schoolapp;

import android.widget.Filter;

import com.example.schoolapp.model.ModelKid;
import com.example.schoolapp.adapters.AdapterKidShow;

import java.util.ArrayList;

public class FilterKidClass extends Filter {

    private AdapterKidShow adapter;
    private ArrayList<ModelKid> filterList;

    public FilterKidClass(AdapterKidShow adapter, ArrayList<ModelKid> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        // Validate data for search query
        if(constraint != null && constraint.length() > 0){
            // Search field not empty, searching something

            // Change upper case to make case insensitive
            constraint = constraint.toString().toUpperCase();

            // Store our filtered list
            ArrayList<ModelKid> filteredModels = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                // Check search by title and category
                if(filterList.get(i).getKidName().toUpperCase().contains(constraint) ||
                        filterList.get(i).getKidClass().toUpperCase().contains(constraint) ){
                    //Add filtered to list
                    filteredModels.add(filterList.get(i));

                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;

        }
        else {
            // Search field empty, not searching, return complete and original list
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.kidList = (ArrayList<ModelKid>) results.values;
        //Refresh adapter
        adapter.notifyDataSetChanged();


    }
}
