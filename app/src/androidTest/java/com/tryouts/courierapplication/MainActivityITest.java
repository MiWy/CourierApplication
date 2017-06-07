package com.tryouts.courierapplication;

import android.support.design.widget.NavigationView;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
public class MainActivityITest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void ensureDrawerIsPresent() {
        MainActivity activity = rule.getActivity();
        View view = activity.findViewById(R.id.nvView);
        assertThat(view,notNullValue());
        assertThat(view, instanceOf(NavigationView.class));
        NavigationView drawer = (NavigationView) view;
        assertThat(drawer.getMenu(), notNullValue());

    }

}
