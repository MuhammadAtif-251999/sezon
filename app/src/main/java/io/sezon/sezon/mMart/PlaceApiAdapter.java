package io.sezon.sezon.mMart;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PlaceApiAdapter extends ArrayAdapter<String>implements Filterable {

    private ArrayList<PlaceModel>place_list;
    PlaceAPI placeAPI;
    public PlaceApiAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        place_list=new ArrayList<>();
        placeAPI=new PlaceAPI();
    }

    @Override
    public int getCount() {
        return place_list.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return place_list.get(position).place_name;
    }

    public PlaceModel getPlaceObject(int position)
    {
        return place_list.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    // A class that queries a web API, parses the data and returns an ArrayList<Style>
                    place_list=placeAPI.searchLocation(constraint.toString());
                    // Now assign the values and count to the FilterResults object
                    filterResults.values = place_list;
                    filterResults.count = place_list.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }
}
