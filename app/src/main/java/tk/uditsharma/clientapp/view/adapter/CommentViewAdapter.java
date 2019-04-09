package tk.uditsharma.clientapp.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tk.uditsharma.clientapp.R;
import tk.uditsharma.clientapp.model.UserDao;
import tk.uditsharma.clientapp.model.CommentResponse;
import tk.uditsharma.clientapp.view.MapsActivity;


public class CommentViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CommentResponse> commentList;

    public CommentViewAdapter(Context context) {
        this.mContext = context;
        this.commentList = new ArrayList<>();

    }

    public void addComment(CommentResponse comment) {
        this.commentList.add(comment);
        notifyDataSetChanged();
    }

    public void setCommentList(List<CommentResponse> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }

    public void removeComment(int commentPos) {
        this.commentList.remove(commentPos);
        notifyDataSetChanged();
    }

    public void updateComment(int index, CommentResponse comm) {
        this.commentList.set(index, comm);
        notifyDataSetChanged();
    }

    public void updateAllComment(List<CommentResponse> cmtList) {
        this.commentList.addAll(cmtList);
        notifyDataSetChanged();
    }


    @Override
    public CommentViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_row, viewGroup, false);

        return new CommentViewAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView commentView;


        public ViewHolder(final View itemView) {
            super(itemView);

            this.commentView = (TextView) itemView.findViewById(R.id.left_cmnt);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    CommentResponse cComment = commentList.get(getAdapterPosition());
                    if(cComment.getUsermail().equals(UserDao.getCurrentUser())){
                        ((MapsActivity)mContext).goToActionMode(v, cComment);
                        v.setSelected(true);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (commentList != null)
            return commentList.size();
        else return 0;
    }

    private CommentResponse getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final CommentResponse cmtObj = getItem(position);
            final SpannableStringBuilder sb = new SpannableStringBuilder(cmtObj.getUsername() + ": " + cmtObj.getCommentText());
            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(43, 84, 109));
            sb.setSpan(bss, 0,cmtObj.getUsername().length()+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            if(cmtObj.getUsermail().equals(UserDao.getCurrentUser())){
                final ForegroundColorSpan fcsUser = new ForegroundColorSpan(Color.rgb(108, 200, 135));
                sb.setSpan(fcsUser, 0,cmtObj.getUsername().length()+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            } else{
                sb.setSpan(fcs, 0,cmtObj.getUsername().length()+1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            ViewHolder genericViewHolder = (ViewHolder) holder;
            genericViewHolder.commentView.setText(sb);
        }
    }

}
