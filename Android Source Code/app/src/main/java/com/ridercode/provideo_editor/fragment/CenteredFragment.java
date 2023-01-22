package com.ridercode.provideo_editor.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.NativeAdLayout;
import com.ridercode.provideo_editor.CenterZoomLayoutManager;
import com.ridercode.provideo_editor.Helper;
import com.ridercode.provideo_editor.R;
import com.ridercode.provideo_editor.Saved;
import com.ridercode.provideo_editor.ads.AdsManager;
import com.ridercode.provideo_editor.billing.Config;
import com.ridercode.provideo_editor.billing.UnlockAllActivity;
import com.ridercode.provideo_editor.listmusicandmymusic.ListMusicAndMyMusicActivity;
import com.ridercode.provideo_editor.listvideoandmyvideo.ListVideoAndMyAlbumActivity;
import com.ridercode.provideo_editor.listvideowithmymusic.ListVideoAndMyMusicActivity;
import com.ridercode.provideo_editor.model.GridIcons;
import com.ridercode.provideo_editor.phototovideo.SelectImageAndMyVideoActivity;
import com.ridercode.provideo_editor.videocollage.ListCollageAndMyAlbumActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;




public class CenteredFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private ArrayList<GridIcons> gridList = new ArrayList<>();
    GrideAdapter grideAdapter;

    public static CenteredFragment createFor(String text) {
        CenteredFragment fragment = new CenteredFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.center_fragment, container, false);

        FrameLayout frameLayout =
                view.findViewById(R.id.fl_adplaceholder);

        NativeAdLayout nativeAdLayout = view.findViewById(R.id.native_ad_container);

        AdsManager.loadNativeAd(getActivity(), frameLayout, nativeAdLayout);

        addList();
        setUpRecyclerView(view);

        return view;
    }

    private void addList() {
        gridList.add(new GridIcons("Video To GIF", R.drawable.ic_animation, false));
        gridList.add(new GridIcons("Video Revserse", R.drawable.ic_slow_motion, false));
        gridList.add(new GridIcons("Fast Motion", R.drawable.ic_stopwatch, false));
        gridList.add(new GridIcons("Video Mute", R.drawable.ic_mute, false));
        gridList.add(new GridIcons("Video Compress", R.drawable.ic_multimedia, true));
        gridList.add(new GridIcons("Video To MP3", R.drawable.ic_mp3, false));
        gridList.add(new GridIcons("Video Mixer", R.drawable.ic_multimedia, false));
        gridList.add(new GridIcons("Video Joiner", R.drawable.ic_multimedia, true));
        gridList.add(new GridIcons("Video To Image", R.drawable.ic_picture, false));
        gridList.add(new GridIcons("Video Cutter", R.drawable.ic_wire_cutter, true));
        gridList.add(new GridIcons("Video Converter", R.drawable.ic_multimedia, false));
        gridList.add(new GridIcons("Slow Motion", R.drawable.ic_slow_motion, true));
        gridList.add(new GridIcons("Video Splitter", R.drawable.ic_audio_waves, true));
        gridList.add(new GridIcons("Video Cropper", R.drawable.ic_crop, false));
        gridList.add(new GridIcons("Video To GIF", R.drawable.ic_animation, false));
        gridList.add(new GridIcons("Video Rotate", R.drawable.ic_rotate, true));
        gridList.add(new GridIcons("Video Mirror", R.drawable.ic_reflection, false));
        gridList.add(new GridIcons("Photo To Video", R.drawable.ic_picture, false));
        gridList.add(new GridIcons("Audio Compress", R.drawable.ic_volume, false));
        gridList.add(new GridIcons("Audio Joiner", R.drawable.ic_audio_editing, false));
        gridList.add(new GridIcons("Audio Cutter", R.drawable.ic_music_player, false));
    }

    private void setUpRecyclerView(View view) {
        grideAdapter = new GrideAdapter(gridList);
        RecyclerView categoriesList = view.findViewById(R.id.categoriesList);
        categoriesList.setLayoutManager(new CenterZoomLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoriesList.setAdapter(grideAdapter);
        grideAdapter.notifyDataSetChanged();
        categoriesList.scheduleLayoutAnimation();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        final String text = args != null ? args.getString(EXTRA_TEXT) : "";


    }

    public class GrideAdapter extends RecyclerView.Adapter<GrideAdapter.MyViewHolder> {

        private List<GridIcons> gridIconsList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView iconLabel, pro;
            ImageView icon;
            CardView card;

            public MyViewHolder(View view) {
                super(view);
                iconLabel = (TextView) view.findViewById(R.id.iconLabel);
                icon = (ImageView) view.findViewById(R.id.icon);
                card = (CardView) view.findViewById(R.id.card);
                pro = (TextView) view.findViewById(R.id.pro);
            }
        }


        public GrideAdapter(List<GridIcons> gridIconsList) {
            this.gridIconsList = gridIconsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_list, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            GridIcons gridIcons = gridIconsList.get(position);
            if (gridIcons.isPro()) holder.pro.setVisibility(View.VISIBLE);
            else holder.pro.setVisibility(View.GONE);
            holder.iconLabel.setText(gridIcons.getLabel());
            holder.icon.setImageResource(gridIcons.getIcon());

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Helper.ModuleId = position + 1;
                    boolean isSubscribed = Saved.g_Boolean(getActivity(), Config.IsPurchased, false);

                    switch (position) {
                        case 0:
                        case 1:
                        case 3:
                        case 4:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 12:
                        case 13:
                        case 14:
                        case 15:
                        case 21:
                            if (gridIcons.isPro()) {
                                if (isSubscribed) {
                                    setIntent(ListVideoAndMyAlbumActivity.class);
                                } else {
                                    setIntent(UnlockAllActivity.class);
                                }
                            } else {
                                setIntent(ListVideoAndMyAlbumActivity.class);
                            }
                            break;
                        case 2:
                            setIntent(ListVideoAndMyMusicActivity.class);
                            break;
                        case 5:
                            if (gridIcons.isPro()) {
                                if (isSubscribed) {
                                    setIntent(com.ridercode.provideo_editor.videojoiner.ListVideoAndMyAlbumActivity.class);
                                } else {
                                    setIntent(UnlockAllActivity.class);
                                }
                            } else {
                                setIntent(com.ridercode.provideo_editor.videojoiner.ListVideoAndMyAlbumActivity.class);
                            }
                            break;
                        case 6:
                        case 11:
                            setIntent(com.ridercode.provideo_editor.videotogif.ListVideoAndMyAlbumActivity.class);
                            break;
                        case 16:
                            setIntent(ListCollageAndMyAlbumActivity.class);
                            break;
                        case 17:
                        case 18:
                        case 19:
                            setIntent(ListMusicAndMyMusicActivity.class);
                            break;
                        case 20:
                            setIntent(SelectImageAndMyVideoActivity.class);
                            break;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return gridIconsList.size();
        }

        private void setIntent(Class<?> cls) {
            Intent intent = new Intent(getActivity(), cls);
            intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

}
