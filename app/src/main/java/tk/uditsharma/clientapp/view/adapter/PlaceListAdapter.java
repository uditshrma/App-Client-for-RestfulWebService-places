package tk.uditsharma.clientapp.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.model.PlaceEntry;
import tk.uditsharma.clientapp.view.WishListActivity;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private Context mContext;
    private final LayoutInflater mInflater;
    private List<PlaceEntry> mPlaces;

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final TextView placeItemView;

        private PlaceViewHolder(View itemView) {
            super(itemView);
            placeItemView = itemView.findViewById(R.id.wishlist_textView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PlaceEntry cPlace = mPlaces.get(getAdapterPosition());
                    ((WishListActivity)mContext).goToActionMode(v, cPlace);
                    v.setSelected(true);
                    return true;
                }
            });
        }

    }

    public PlaceListAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.place_recyclerview_item, parent, false);
        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        if (mPlaces != null) {
            PlaceEntry current = mPlaces.get(position);
            holder.placeItemView.setText(current.getPlaceName());
        } else {
            // Covers the case of data not being ready yet.
            holder.placeItemView.setText("No Place");
        }
    }

    public void setPlaces(List<PlaceEntry> places){
        mPlaces = places;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mPlaces has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPlaces != null)
            return mPlaces.size();
        else return 0;
    }
}
