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
import android.util.Log
import android.widget.Toast
import com.pguardiola.uigesturegen.dsl.*
import kategory.*
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StuffTest {

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
            doubleTap(mapView)
            val result = failFast(mapView)
            assertEquals("", result)
            //assertTrue(result)
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

    private fun failFast(element: UiObject): Boolean {
        val actions = pinchOut(100, 50).pinchOut(100, 50)
        return actions.failFast(element).hasCompletedCorrectly()
    }

    // TODO RJ Question: How can I start with this action? Now I'm unable...
    fun ActionDSL<Unit>.doubleClick() = click() + click()

    // TODO RJ Question: How can I work with GesturesDSL directly? See samples.kt
    // TODO RJ More examples including different ways to interact with GesturesDSL
    private fun moreStuff(element: UiObject) {
    }

    private fun doStuff(element: UiObject): Boolean {
//        val actions = click() + click() + pinchOut(100, 50) + pinchOut(100, 50)
        val actions = click().pinchOut(100, 50).doubleClick().pinchOut(100, 50)
        // TODO RJ Question: Why isn't this working?
//        val result: Validated<NonEmptyList<GestureError>, Boolean> = GesturesDSL.map(
//                click(),
//                click(),
//                click(),
//                pinchOut(100, 50),
//                pinchOut(100, 50),
//                { _ -> println("works!"); true }).validate(mapView)
//        return result
        return actions.validate(element).hasCompletedCorrectly()



//        val res: Boolean = GesturesDSL.map {
//            findObject(UiSelector().description("map view container"))
//                    .pinchOutAndThen(100, 50)
//                    .pinchOutAndThen(100, 50)
//                    .pinchIn(100, 50)
//        }.runUnsafe(device)
//        return res
    }

    // TODO Question: How can I include DoubleTap action into the DSL?
    private fun doubleTap(element: UiObject) {
        val configurator = Configurator.getInstance()
        configurator.actionAcknowledgmentTimeout = 40

        try {
            element.click()
            element.click()
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
        }

        configurator.actionAcknowledgmentTimeout = 3000
    }
}
