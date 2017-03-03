package cn.ittiger.video.player;

import cn.ittiger.video.R;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ylhu on 17-3-3.
 */
public class TigerVideoPlayer extends JCVideoPlayerStandard {
    private TextView mDurationTextView;
    private int mPosition = -1;
    private VideoClickPlayListener mVideoClickPlayListener;

    public TigerVideoPlayer(Context context) {

        super(context);
    }

    public TigerVideoPlayer(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    public int getLayoutId() {

        return R.layout.tiger_video_player_layout;
    }

    @Override
    public void init(Context context) {

        super.init(context);
        mDurationTextView = (TextView) findViewById(R.id.videoDuration);
    }

    @Override
    public void setUp(String url, int screen, Object... objects) {

        super.setUp(url, screen, objects);
        mDurationTextView.setText(objects[1].toString());
        mPosition = Integer.parseInt(objects[2].toString());
        if(mDurationTextView.getVisibility() == GONE) {
            mDurationTextView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setUiWitStateAndScreen(int state) {

        super.setUiWitStateAndScreen(state);
        switch (currentState) {
            case CURRENT_STATE_PREPARING:
                mDurationTextView.setVisibility(GONE);
                if(mVideoClickPlayListener != null) {
                    mVideoClickPlayListener.onVideoClick(this, mPosition);
                }
                break;
            case CURRENT_STATE_ERROR:
                mDurationTextView.setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    public void addTextureView() {

        removeTextureView();
        super.addTextureView();
    }

    public interface VideoClickPlayListener {

        void onVideoClick(TigerVideoPlayer videoPlayer, int position);
    }

    public void setVideoClickPlayListener(VideoClickPlayListener videoClickPlayListener) {

        mVideoClickPlayListener = videoClickPlayListener;
    }
}
