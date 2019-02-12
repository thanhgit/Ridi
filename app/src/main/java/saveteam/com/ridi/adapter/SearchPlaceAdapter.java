package saveteam.com.ridi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridi.R;
import saveteam.com.ridi.server.model.searchplacewithtext.Result;

public class SearchPlaceAdapter extends RecyclerView.Adapter<SearchPlaceAdapter.SearchPlaceHolder> {
    Context context;
    List<Result> results;
    OnClickItemSearchPlaceListener listener;

    public interface OnClickItemSearchPlaceListener {
        void selected(Result result);
    }

    public SearchPlaceAdapter(Context context, List<Result> results) {
        this.context = context;
        this.results = results;
    }

    public void setOnClickItemSearchPlaceListener(OnClickItemSearchPlaceListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchPlaceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place_where_search_place, viewGroup, false);
        return new SearchPlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPlaceHolder searchPlaceHolder, int i) {
        final Result result = results.get(i);
        final int position = i;
        searchPlaceHolder.tv_place_name.setText(result.getName());
        searchPlaceHolder.tv_place_name_detail.setText(result.getFormattedAddress());
        searchPlaceHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.selected(result);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class SearchPlaceHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_place_name_where_item_search_place)
        TextView tv_place_name;
        @BindView(R.id.tv_place_name_detail_item_search_place)
        TextView tv_place_name_detail;
        @BindView(R.id.iv_bg_where_item_search_place)
        ImageView iv_bg;
        @BindView(R.id.layout_where_item_search_place)
        LinearLayout layout;

        public SearchPlaceHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
