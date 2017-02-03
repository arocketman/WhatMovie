package com.github.arocketman.whatmovie;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.arocketman.whatmovie.constants.Constants;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.uwetrottmann.tmdb2.entities.Movie;


@Layout(R.layout.movie_card_view)
public class MovieCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView mTitleVote;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    @View(R.id.descriptionLayout)
    private LinearLayout descriptionView;

    @View(R.id.movieCardLayout)
    private LinearLayout movieFrameLayout;

    private Movie mMovie;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    private int descriptionOldHeight;
    private int imageViewOldHeight;

    public MovieCard(Context context, Movie profile, SwipePlaceHolderView swipeView) {
        mContext = context;
        mMovie = profile;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(Constants.IMAGES_BASEURL+mMovie.poster_path).into(profileImageView);
        mTitleVote.setText(mMovie.title + ", " + mMovie.vote_average);
        locationNameTxt.setText(mMovie.overview);
    }

    @Click(R.id.descriptionLayout)
    private void toggleDescription() {
        if(profileImageView.getLayoutParams().height == 0) {
            getToggleAnimation(profileImageView,profileImageView.getHeight(),imageViewOldHeight).start();
            getToggleAnimation(descriptionView,descriptionView.getHeight(),descriptionOldHeight).start();
            ((FrameLayout.LayoutParams)descriptionView.getLayoutParams()).gravity = Gravity.BOTTOM;
        }
        else{
            imageViewOldHeight = profileImageView.getHeight();
            descriptionOldHeight = descriptionView.getHeight();
            getToggleAnimation(profileImageView,imageViewOldHeight,0).start();
            getToggleAnimation(descriptionView,descriptionOldHeight,((CardView)descriptionView.getParent()).getHeight()).start();
        }
    }

    /**
     * Creates an animator that smoothly animates the passed view height from startHeight to
     * endHeight.
     * @param view The view that needs to be animated.
     * @param startHeight Starting height of the view.
     * @param endHeight Final height of the view.
     * @return
     */
    private ValueAnimator getToggleAnimation(final android.view.View view , int startHeight , int endHeight) {
        //We create the animator and setup the starting height and the final height. The animator
        //Will create smooth itnermediate values (based on duration) to go across these two values.
        ValueAnimator animator = ValueAnimator.ofInt(startHeight,endHeight);
        //Overriding updateListener so that we can tell the animator what to do at each update.
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //We get the value of the animatedValue, this will be between [startHeight,endHeight]
                int val = (Integer)animation.getAnimatedValue();
                //We retrieve the layout parameters and pick up the height of the View.
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.height = val;
                //Once we have updated the height all we need to do is to call the set method.
                view.setLayoutParams(params);
            }
        });
        //A duration for the whole animation, this can easily become a function parameter if needed.
        animator.setDuration(Constants.TOGGLE_ANIMATION_DURATION);
        return animator;
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }
}

