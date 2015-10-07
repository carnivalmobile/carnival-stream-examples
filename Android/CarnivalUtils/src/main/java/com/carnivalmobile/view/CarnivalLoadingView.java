package com.carnivalmobile.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.carnivalmobile.R;

public class CarnivalLoadingView extends RelativeLayout {

    private ProgressBar mLoadingProgress;
    private TextSwitcher mFeedbackTextView;
    private ImageView mCarnivalDots;

    private int mShortAnimationDuration;

    public CarnivalLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mShortAnimationDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        ImageView carnivalBubble = buildCarnivalBubble(context);
        addView(carnivalBubble);

        mCarnivalDots = buildCarnivalDots(context);
        addView(mCarnivalDots);

        mLoadingProgress = buildProgressBar(context);
        addView(mLoadingProgress);

        mFeedbackTextView = buildFeedbackText(context);
        addView(mFeedbackTextView);

        if (isInEditMode()) {
            mLoadingProgress.setVisibility(View.GONE);
            carnivalBubble.setImageResource(R.drawable.bg_carnival_bubble);
            mFeedbackTextView.setCurrentText("Nothing to see here\nMove along please");
        }

    }

    private ProgressBar buildProgressBar(Context context) {
        Resources r = context.getResources();
        int loadingProgressSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());

        LayoutParams progressLayout = new LayoutParams(loadingProgressSize, loadingProgressSize);
        progressLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        ProgressBar loadingProgress = new ProgressBar(context);
        loadingProgress.setLayoutParams(progressLayout);
        loadingProgress.setIndeterminate(true);

        return loadingProgress;
    }

    private ImageView buildCarnivalBubble(Context context) {
        Resources r = context.getResources();
        LayoutParams bubbleLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        bubbleLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        int bubblePaddingSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, r.getDisplayMetrics());

        ImageView carnivalBubble = new ImageView(context);
        carnivalBubble.setId(R.id.carnival_bubble);
        carnivalBubble.setLayoutParams(bubbleLayout);
        carnivalBubble.setPadding(0, bubblePaddingSize, 0, 0);
        carnivalBubble.setImageResource(R.drawable.bg_carnival_bubble_blank);

        return carnivalBubble;
    }

    private TextSwitcher buildFeedbackText(final Context context) {
        final Resources r = context.getResources();

        int topMarginSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, r.getDisplayMetrics());

        LayoutParams feedbackLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        feedbackLayout.addRule(RelativeLayout.BELOW, R.id.carnival_bubble);
        feedbackLayout.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        feedbackLayout.setMargins(0, topMarginSize, 0, 0);

        TextSwitcher switcher = new TextSwitcher(context);
        switcher.setLayoutParams(feedbackLayout);
        switcher.setFactory(new ViewSwitcher.ViewFactory() {

            @Override
            public View makeView() {
                TextView t = new TextView(context);
                t.setTextColor(Color.parseColor("#9B9BA2"));
                t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                t.setGravity(Gravity.CENTER);
                return t;
            }
        });

        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        switcher.setInAnimation(in);
        switcher.setOutAnimation(out);

        return switcher;
    }

    private ImageView buildCarnivalDots(Context context) {
        LayoutParams dotsLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        dotsLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        ImageView dotsImageView = new ImageView(context);
        dotsImageView.setLayoutParams(dotsLayout);
        dotsImageView.setImageResource(R.drawable.carnival_dots);
        dotsImageView.setVisibility(View.GONE);

        return dotsImageView;
    }

    public void setFeedbackText(final String text) {
        mFeedbackTextView.setText(text);
    }

    public void stop() {
        crossfadeViews(mLoadingProgress, mCarnivalDots, mShortAnimationDuration);
    }

    public void start() {
        crossfadeViews(mCarnivalDots, mLoadingProgress, mShortAnimationDuration);
    }

    private void crossfadeViews(final View disappearing, final View appearing, final long duration) {

        if (appearing.getVisibility() != View.VISIBLE) {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            appearing.setAlpha(0f);
            appearing.setVisibility(View.VISIBLE);

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            appearing.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setListener(null);
        }

        if (disappearing.getVisibility() != View.GONE) {
            // Animate the loading view to 0% opacity. After the animation ends,
            // set its visibility to GONE as an optimization step (it won't
            // participate in layout passes, etc.)
            disappearing.animate()
                    .alpha(0f)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            disappearing.setVisibility(View.GONE);
                        }
                    });
        }
    }

}
