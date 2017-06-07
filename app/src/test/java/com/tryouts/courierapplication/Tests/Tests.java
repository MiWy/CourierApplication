package com.tryouts.courierapplication.Tests;

import com.google.android.gms.maps.model.LatLng;
import com.tryouts.courierapplication.fragments.NewOrderFragment;
import com.tryouts.courierapplication.interactors.NewOrderInteractor;
import com.tryouts.courierapplication.presenters.NewOrderPresenter;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class Tests {

    @Mock
    NewOrderInteractor newOrderInteractor;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void validateIfNewOrderIsReady() {
        Assert.assertEquals(false, newOrderInteractor.isOrderReadyToSubmit());
    }
}
