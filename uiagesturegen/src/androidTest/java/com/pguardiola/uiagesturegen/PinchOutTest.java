/*
 * Copyright (C) 2017 Pablo Guardiola Sánchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pguardiola.uiagesturegen;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class PinchOutTest {
  private static final String BASIC_SAMPLE_PACKAGE = "com.pguardiola.uiagesturegen";
  private static final int LAUNCH_TIMEOUT = 5000;
  private UiDevice mDevice;

  @Before
  public void startMainActivityFromHomeScreen() {
    // Initialize UiDevice instance
    mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    // Start from the home screen
    mDevice.pressHome();

    // Wait for launcher
    final String launcherPackage = getLauncherPackageName();
    assertThat(launcherPackage, notNullValue());
    mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

    // Launch the blueprint app
    Context context = InstrumentationRegistry.getContext();
    final Intent intent = context.getPackageManager()
      .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
    context.startActivity(intent);

    // Wait for the app to appear
    mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
  }

  @Test
  public void checksPreconditions() {
    assertThat(mDevice, notNullValue());
    try {
      UiSelector mapViewSelector = new UiSelector().description("map view container");
      UiObject mapView = mDevice.findObject(mapViewSelector);
      assertTrue(mapView.pinchOut(100, 50));
    } catch (UiObjectNotFoundException e) {
      e.printStackTrace();
    }
  }

  private String getLauncherPackageName() {
    // Create launcher Intent
    final Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_HOME);

    // Use PackageManager to get the launcher package name
    PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
    ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
    return resolveInfo.activityInfo.packageName;
  }
}
