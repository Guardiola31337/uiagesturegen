package com.pguardiola.uigesturegen.dsl

import android.graphics.Point
import android.support.test.uiautomator.UiObject
import android.view.MotionEvent
import kategory.*


@Suppress("UNCHECKED_CAST")
inline fun <reified F> safeInterpreter(ME: MonadError<F, Throwable>, view: UiObject) : FunctionK<GesturesDSLHK, F> =
    object : FunctionK<GesturesDSLHK, F> {
        override fun <A> invoke(fa: HK<GesturesDSLHK, A>): HK<F, A> {
            val g = fa.ev()
            return when (g) {
                is GesturesDSL.Click -> ME.catch({ clickImpl(view) })
                is GesturesDSL.PinchIn -> ME.catch({ pinchInImpl(view, g.percent, g.steps) })
                is GesturesDSL.PinchOut -> ME.catch({ pinchOutImpl(view, g.percent, g.steps) })
                is GesturesDSL.SwipeLeft -> ME.catch({ swipeLeftImpl(view, g.steps) })
                is GesturesDSL.SwipeRight -> ME.catch({ swipeRightImpl(view, g.steps) })
                is GesturesDSL.SwipeUp -> ME.catch({ swipeUpImpl(view, g.steps) })
                is GesturesDSL.SwipeDown -> ME.catch({ swipeDownImpl(view, g.steps) })
                is GesturesDSL.MultiTouch -> ME.catch({ multiTouchImpl(view, g.touches) })
                is GesturesDSL.TwoPointer -> ME.catch({ twoPointerImpl(view, g.firstStart, g.firstEnd, g.secondStart, g.secondEnd, g.steps) })
            } as HK<F, A>
        }
    }

fun clickImpl(view: UiObject): Boolean = view.click()

fun pinchInImpl(view: UiObject, percent: Int, steps: Int): Boolean = view.pinchIn(percent, steps)

fun pinchOutImpl(view: UiObject, percent: Int, steps: Int): Boolean = view.pinchOut(percent, steps)

fun swipeLeftImpl(view: UiObject, steps: Int): Boolean = view.swipeLeft(steps)

fun swipeRightImpl(view: UiObject, steps: Int): Boolean = view.swipeRight(steps)

fun swipeUpImpl(view: UiObject, steps: Int): Boolean = view.swipeUp(steps)

fun swipeDownImpl(view: UiObject, steps: Int): Boolean = view.swipeDown(steps)

fun multiTouchImpl(view: UiObject, touches: List<Array<MotionEvent.PointerCoords>>): Boolean = view.performMultiPointerGesture(*(touches.toTypedArray()))

fun twoPointerImpl(view: UiObject, firstStart: Point, firstEnd: Point, secondStart: Point, secondEnd: Point, steps: Int): Boolean = view.performTwoPointerGesture(firstStart, secondStart, firstEnd, secondEnd, steps)