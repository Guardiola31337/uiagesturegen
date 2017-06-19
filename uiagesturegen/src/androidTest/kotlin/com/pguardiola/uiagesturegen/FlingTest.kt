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

package com.pguardiola.uiagesturegen

import android.content.Intent
import android.content.pm.PackageManager
import android.support.test.InstrumentationRegistry
import android.support.test.uiautomator.*
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FlingTest {

    companion object {
        private val BASIC_SAMPLE_PACKAGE = "com.pguardiola.uiagesturegen"
        private val LAUNCH_TIMEOUT = 5000
    }

    lateinit private var device: UiDevice

    @Before fun startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Start from the home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage = obtainLauncherPackageName()
        assertThat(launcherPackage, CoreMatchers.notNullValue())
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT.toLong())

        // Launch the blueprint app
        val context = InstrumentationRegistry.getContext()
        val intent = context.packageManager.getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)    // Clear out any previous instances
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT.toLong())
    }

    @Test fun checksPreconditions() {
        assertThat(device, notNullValue())
        try {
            val mapViewSelector = UiSelector().description("map view container")
            val mapView = device.findObject(mapViewSelector)
            assertTrue(mapView.swipeLeft(50))
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun obtainLauncherPackageName(): String {
        // Create launcher Intent
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        // Use PackageManager to get the launcher package name
        val pm = InstrumentationRegistry.getContext().packageManager
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo.activityInfo.packageName
    }
}


