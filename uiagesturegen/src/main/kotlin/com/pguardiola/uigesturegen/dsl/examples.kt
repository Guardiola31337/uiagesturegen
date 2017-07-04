package com.pguardiola.uigesturegen.dsl

import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiObject
import android.support.test.uiautomator.UiSelector

import kategory.*

object GestureDSLExamples {

    /**
     * compose multiple actions independently regardless of return types
     */
    fun independentActionsWorkflow(): DSLAction<Tuple4<Boolean, UiObject, UiObject, UiObject>> =
            GestureDSL.tupled(
                    pressHome(),
                    findObject(UiSelector().description("1")),
                    findObject(UiSelector().description("2")),
                    findObject(UiSelector().description("3"))).ev()


    /**
     * compose actions based on results of other actions
     */
    fun dependentActionsWorkflow(): DSLAction<UiObject> = GestureDSL.binding {
        val homePressed = !pressHome()
        val ui = !if (homePressed) {
            findObject(UiSelector().description("1"))
        } else {
            findObject(UiSelector().description("2"))
        }
        yields(ui)
    }.ev()

    /**
     * Create a bigger action from smaller actions
     */
    fun uberAction(): DSLAction<List<UiObject>> = listOf(
            findObject(UiSelector().description("1")),
            findObject(UiSelector().description("2"))).sequence()

    /**
     * Map over multiple actions of the same type extracting inner values or performing effects
     */
    fun mappedAction(): DSLAction<List<Boolean>> = listOf(
            findObject(UiSelector().description("1")),
            findObject(UiSelector().description("2"))).transform {
        it.longClick()
    }

    fun runAll() = {
        val program = GestureDSL.tupled(
                independentActionsWorkflow(),
                dependentActionsWorkflow(),
                uberAction(),
                mappedAction()).ev()
        //handle errors in the workflow appropriately, or unsafeRun to bubble up exceptions
        // Notice how the device is just passed to the workflow at the end
        // the entire program is also stack safe because is built a top of the Free monad which reifies ops in memory
        program.run(UiDevice.getInstance()).recover { e -> throw e }
    }

}