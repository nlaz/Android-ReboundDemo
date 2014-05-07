package com.mad.rebounddemo.app;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringConfigRegistry;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.facebook.rebound.ui.SpringConfiguratorView;
import com.facebook.rebound.ui.Util;


/**
 * This Activity demonstrates a simple example of creating a spring toggle animation
 *  based on the Origami examples http://facebook.github.io/origami/examples and
 *  brought to you with MAD <3
 */
public class MainActivity extends Activity {

    // Create a spring configuration with starting Tension and Friction values.
    private static final SpringConfig SPRING_CONFIG = SpringConfig.fromOrigamiTensionAndFriction(40, 3);
    private static final long DELAY_TIME = 100;

    private Spring mSpring1;
    private Spring mSpring2;
    private Spring mSpring3;
    private SpringConfiguratorView mSpringConfiguratorView;
    private TextView mObject1;
    private TextView mObject2;
    private TextView mObject3;
    private SpringRunnable runnable1;
    private SpringRunnable runnable2;
    private SpringRunnable runnable3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpringConfiguratorView = (SpringConfiguratorView) findViewById(R.id.spring_configurator);
        Typeface mFace = Typeface.createFromAsset(getAssets(), "LOT.otf");
        mObject1 = (TextView) findViewById(R.id.object1);
        mObject2 = (TextView) findViewById(R.id.object2);
        mObject3 = (TextView) findViewById(R.id.object3);
        mObject1.setTypeface(mFace);
        mObject2.setTypeface(mFace);
        mObject3.setTypeface(mFace);


        // Setup the Spring by creating a SpringSystem adding a SimpleListener that renders the
        // animation whenever the spring is updated.
        SpringSystem mSpringSystem = SpringSystem.create();
        mSpring1 = mSpringSystem.createSpring();
        mSpring2 = mSpringSystem.createSpring();
        mSpring3 = mSpringSystem.createSpring();

        mSpring1.setSpringConfig(SPRING_CONFIG);
        mSpring2.setSpringConfig(SPRING_CONFIG);
        mSpring3.setSpringConfig(SPRING_CONFIG);

        mSpring1.addListener(new ViewSpringListener(mObject1));
        mSpring2.addListener(new ViewSpringListener(mObject2));
        mSpring3.addListener(new ViewSpringListener(mObject3));

        runnable1 = new SpringRunnable(mSpring1);
        runnable2 = new SpringRunnable(mSpring2);
        runnable3 = new SpringRunnable(mSpring3);


        /** Optional - Live Spring Tuning **/

        // Put our config into a registry. This is optional, but it gives you the ability to live tune
        // the spring using the SpringConfiguratorView which will show up at the bottom of the screen.
        SpringConfigRegistry.getInstance().addSpringConfig(SPRING_CONFIG, "Splash animation spring");
        // Tell the SpringConfiguratorView that we've updated the registry to allow you to live tune the animation spring.
        mSpringConfiguratorView.refreshSpringConfigurations();

        // Uncomment this line to actually show the SpringConfiguratorView allowing you to live tune
        // the Spring constants as you manipulate the UI.
        mSpringConfiguratorView.setVisibility(View.VISIBLE);
    }

    /**
     * On click we just move the springs end state from 0 to 1. This allows the Spring to act much
     * like an Origami switch.
     */
    public void handleClick(View view) {

        Handler handler=new Handler();
        handler.post(runnable1);
        handler.postDelayed(runnable2,DELAY_TIME);
        handler.postDelayed(runnable3,DELAY_TIME * 2);
    }

    /**
     * This method takes the current state of the spring and maps it to all the values for each UI
     * element that is animated on this spring. This allows the Spring to act as a common timing
     * function for the animation ensuring that all element transitions are synchronized.
     *
     * You can think of these mappings as similiar to Origami transitions.
     * SpringUtil#mapValueFromRangeToRange converts the spring's 0 to 1 transition and maps it to the
     * range of animation for a property on a view such as translation, scale, rotation, and alpha.
     */
    private void render(View v, Spring spring) {
        Resources resources = getResources();
        // Get the current spring value.
        double value = spring.getCurrentValue();

        // Map the spring to the selected photo scale as it moves into and out of the grid.
        float selectedYScale = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, 1, 0);
        selectedYScale = Math.max(selectedYScale, 0); // Clamp the value so we don't go below 0.
        v.setScaleY(selectedYScale);
        // Animation text to drop down instead of spring up
//        v.setScaleY(1-selectedYScale);

        float selectedTranslateY = (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, Util.dpToPx(80f, resources), 0);
        v.setTranslationY(-selectedTranslateY);
    }

    public class ViewSpringListener implements SpringListener {

        View _view;

        ViewSpringListener(View view){
            this._view = view;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            render(_view,spring);
        }

        @Override
        public void onSpringAtRest(Spring spring) {
        }

        @Override
        public void onSpringActivate(Spring spring) {
        }

        @Override
        public void onSpringEndStateChange(Spring spring) {
        }
    }

    public class SpringRunnable implements Runnable{
        Spring _spring;

        SpringRunnable(Spring spring){
            this._spring = spring;
        }
        @Override
        public void run() {
            if (_spring.getEndValue() == 0) {

                _spring.setEndValue(1);
            } else {
                _spring.setEndValue(0);
            }
        }
    }

}