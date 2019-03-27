package tk.uditsharma.clientapp.view.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tk.uditsharma.clientapp.model.User;
import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.model.UserParcel;
import tk.uditsharma.clientapp.view.UserProfileActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<User> modelList;
    ProgressDialog prgDial;

    public RecyclerViewAdapter(Context context, List<User> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public RecyclerViewAdapter(Context context, ProgressDialog prgD) {
        this.mContext = context;
        this.modelList = new ArrayList<>();
        this.prgDial = prgD;

    }

    public void updateList(List<User> mList) {
        //this.modelList.addAll(mList);
        this.modelList = mList;
        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycler_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final User model = getItem(position);
            ViewHolder genericViewHolder = (ViewHolder) holder;

            genericViewHolder.userName.setText(model.getName());
            genericViewHolder.userId.setText(model.getUserName());
            if (model.getPassword().equals("Access Denied")) {
                genericViewHolder.user_pass.setText("---");
            } else {
                genericViewHolder.user_pass.setText(model.getPassword());
            }
            genericViewHolder.reg_date.setText(model.getRegDateString());


        }
    }


    @Override
    public int getItemCount() {
        if (modelList != null)
            return modelList.size();
        else return 0;

    }

    private User getItem(int position) {
        return modelList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        private TextView userName;
        private TextView userId;
        private TextView user_pass;
        private TextView reg_date;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.cv = (CardView)itemView.findViewById(R.id.cv);
            this.userName = (TextView) itemView.findViewById(R.id.user_name);
            this.userId = (TextView) itemView.findViewById(R.id.user_id);
            this.user_pass = (TextView) itemView.findViewById(R.id.pass_text);
            this.reg_date = (TextView) itemView.findViewById(R.id.reg_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(mContext, "Clicked: " + ((User) modelList.get(getAdapterPosition())).getName(), Toast.LENGTH_SHORT).show();
                    prgDial.show();
                    Intent profileIntent = new Intent(mContext, UserProfileActivity.class);
                    User vUser = (User) modelList.get(getAdapterPosition());
                    profileIntent.putExtra("selected_user", UserParcel.create(vUser.getName(),vUser.getUserName(),vUser.getPassword(),vUser.getRegDateString()));
                    mContext.startActivity(profileIntent);
                }
            });
        }
    }

}

