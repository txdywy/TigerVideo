package cn.ittiger.video.adapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.video.R;
import cn.ittiger.video.bean.VideoData;
import cn.ittiger.video.player.TigerVideoPlayer;
import cn.ittiger.video.ui.recycler.HeaderAndFooterAdapter;
import cn.ittiger.video.ui.recycler.ViewHolder;
import cn.ittiger.video.util.DisplayManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import com.bumptech.glide.Glide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author: laohu on 2016/8/24
 * @site: http://ittiger.cn
 */
public class VideoAdapter extends HeaderAndFooterAdapter<VideoData> implements TigerVideoPlayer.VideoClickPlayListener {

    private Context mContext;
    private int mCurPosition = -1;
    private int mLastPosition = -1;
    private TigerVideoPlayer mCurVideoPlayer;

    public VideoAdapter(Context context, List<VideoData> list) {

        super(list);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position, VideoData item) {

        VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
        videoViewHolder.mVideoPlayer.setVideoClickPlayListener(this);
        videoViewHolder.mVideoPlayer.setUp(
                "http://video.jiecao.fm/11/23/xin/%E5%81%87%E4%BA%BA.mp4", JCVideoPlayer.SCREEN_LAYOUT_LIST,
                item.getTitle(), item.getDuration(), position);

        Glide.with(mContext).load(item.getImageUrl())
                .placeholder(R.drawable.video_image_place_holder)
                .error(R.drawable.video_image_place_holder)
                .into(videoViewHolder.mVideoPlayer.thumbImageView);
    }

    @Override
    public void onVideoClick(TigerVideoPlayer videoPlayer, int position) {

        mLastPosition = mCurPosition;
        mCurPosition = position;
        mCurVideoPlayer = videoPlayer;
    }

    public int getCurPosition() {

        return mCurPosition;
    }

    public int getLastPosition() {

        return mLastPosition;
    }

    public TigerVideoPlayer getCurVideoPlayer() {

        return mCurVideoPlayer;
    }

    class VideoViewHolder extends ViewHolder {
        @BindView(R.id.videoPlayer)
        TigerVideoPlayer mVideoPlayer;

        public VideoViewHolder(View itemView) {

            super(itemView);
            //以宽高比16:9的比例设置播放器的尺寸
            int width = DisplayManager.screenWidthPixel(mContext);
            int height = (int) (width * 1.0f / 16 * 9 + 0.5f);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            params.height = height;
            params.width = width;
            itemView.setLayoutParams(params);
            ButterKnife.bind(this, itemView);
        }
    }

    public void onDestroy() {

        mCurVideoPlayer = null;
    }
}
