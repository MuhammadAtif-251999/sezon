/*
package io.sezon.sezon.mLaundry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import io.sezon.sezon.R;

public class RecyclerViewAdaptor<ClubViewHolder> extends RecyclerView.Adapter<ClubViewHolder>
{
    List<Club>  clubs;
    RecyclerViewAdaptor(List<Club> clubs){
        this.clubs=clubs;
    }

    public static class clubViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView clubName;
        ImageView clubLogo;

        ClubViewHolder(View itemView){
            super(itemView);
            cardView =  itemView.findViewById(R.id.cardView);
            clubName =  itemView.findViewById(R.id.club_name);
            clubLogo = itemView.findViewById(R.id.logo);
        }
    }
    public ClubViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout,parent, false);
    ClubViewHolder  cvh =new clubViewHolder(view);
    return cvh;
    }

}
*/
