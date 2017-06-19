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
import android.graphics.Point
import android.support.test.InstrumentationRegistry
import android.support.test.uiautomator.*
import android.view.MotionEvent
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RotateTest {

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
            val firstPointer = obtainFromZeroToOneHundredAndEightyCircleCoordinates(200,
                    mapView.bounds.centerX(),
                    mapView.bounds.centerY())
            val secondPointer = obtainFromOneHundredAndEightyToZeroCircleCoordinates(200,
                    mapView.bounds.centerX(),
                    mapView.bounds.centerY())
            // TODO Not working properly
            assertTrue(mapView.performMultiPointerGesture(firstPointer, secondPointer))
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

    private fun obtainFromZeroToOneHundredAndEightyCircleCoordinates(radius: Int, xc: Int, yc: Int): Array<MotionEvent.PointerCoords?> {
        // Variation angle
        var theta = Math.toRadians(0.0)
        // Initial point
        var x = radius
        var y = 0
        val pointer = arrayOfNulls<MotionEvent.PointerCoords>(73)
        var i = 0
        // While angle less than 360 degrees
        while (theta <= Math.PI) {
            addPoint(x + xc, y + yc, i, pointer)
            // Increase angle by 5 degrees
            theta = theta + Math.toRadians(2.5)
            // Obtain x and y parametric values
            val xd = radius * Math.cos(theta)
            x = Math.round(xd).toInt()
            val yd = radius * Math.sin(theta)
            y = yd.toInt()
            i++
        }
        return pointer
    }

    private fun obtainFromOneHundredAndEightyToZeroCircleCoordinates(radius: Int, xc: Int, yc: Int): Array<MotionEvent.PointerCoords?> {
        // Variation angle
        var theta = Math.toRadians(180.0)
        // Initial point
        var x = -radius
        var y = 0
        val pointer = arrayOfNulls<MotionEvent.PointerCoords>(73)
        var i = 0
        // While angle less than 360 degrees
        while (theta <= 2 * Math.PI) {
            addPoint(x + xc, y + yc, i, pointer)
            // Increase angle by 5 degrees
            theta = theta + Math.toRadians(2.5)
            // Obtain x and y parametric values
            val xd = radius * Math.cos(theta)
            x = Math.round(xd).toInt()
            val yd = radius * Math.sin(theta)
            y = yd.toInt()
            i++
        }
        return pointer
    }

    private fun obtainPolarCircleCoordinates(radius: Int, xc: Int, yc: Int): Array<MotionEvent.PointerCoords?> {
        // Variation angle
        var theta = Math.toRadians(0.0)
        // Initial point
        var x = radius
        var y = 0
        val pointer = arrayOfNulls<MotionEvent.PointerCoords>(73)
        var i = 0
        // While angle less than 360 degrees
        while (theta <= 2 * Math.PI) {
            addPoint(x + xc, y + yc, i, pointer)
            // Increase angle by 5 degrees
            theta = theta + Math.toRadians(5.0)
            // Obtain x and y parametric values
            val xd = radius * Math.cos(theta)
            x = Math.round(xd).toInt()
            val yd = radius * Math.sin(theta)
            y = yd.toInt()
            i++
        }
        return pointer
    }

    private fun addPoint(x: Int, y: Int, counter: Int, pointer: Array<MotionEvent.PointerCoords?>) {
        val point = MotionEvent.PointerCoords()
        point.x = x.toFloat()
        point.y = y.toFloat()
        point.pressure = 1f
        point.size = 1f
        pointer[counter] = point
    }
}


