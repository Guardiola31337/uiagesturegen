package com.pguardiola.uigesturegen.dsl

import android.graphics.Point
import android.support.test.uiautomator.UiObject
import android.view.MotionEvent
import kategory.FunctionK
import kategory.HK
import kategory.MonadError


@Suppress("UNCHECKED_CAST")
class SafeInterpreter<F>(val M: MonadError<F, Throwable>, val view: UiObject) : FunctionK<GesturesDSL.F, F> {
    override fun <A> invoke(fa: HK<GestureDSL.F, A>): HK<F, A> {
        val g = fa.ev()
        return when (g) {
            is GesturesDSL.Click -> M.pure(clickImpl(view)) // all M.pure should be changed to M.catch using the new version o Kategory
            is GesturesDSL.PinchIn -> M.pure(pinchInImpl(view, g.percent, g.steps))
            is GesturesDSL.PinchOut -> M.pure(pinchOutImpl(view, g.percent, g.steps))
            is GesturesDSL.SwipeLeft -> M.pure(swipeLeftImpl(view, g.steps))
            is GesturesDSL.SwipeRight -> M.pure(swipeRightImpl(view, g.steps))
            is GesturesDSL.SwipeUp -> M.pure(swipeUpImpl(view, g.steps))
            is GesturesDSL.SwipeDown -> M.pure(swipeDownImpl(view, g.steps))
            is GesturesDSL.MultiTouch -> M.pure(multiTouchImpl(view, g.touches))
            is GesturesDSL.TwoPointer -> M.pure(twoPointerImpl(view, g.firstStart, g.firstEnd, g.secondStart, g.secondEnd, g.steps))
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