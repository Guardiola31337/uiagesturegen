package com.pguardiola.uigesturegen.dsl

import android.graphics.Point
import android.support.test.uiautomator.UiObject
import android.view.MotionEvent


fun clickImpl(view: UiObject): Boolean = view.click()

fun pinchInImpl(view: UiObject, percent: Int, steps: Int): Boolean = view.pinchIn(percent, steps)

fun pinchOutImpl(view: UiObject, percent: Int, steps: Int): Boolean = view.pinchOut(percent, steps)

fun swipeLeftImpl(view: UiObject, steps: Int): Boolean = view.swipeLeft(steps)

fun swipeRightImpl(view: UiObject, steps: Int): Boolean = view.swipeRight(steps)

fun swipeUpImpl(view: UiObject, steps: Int): Boolean = view.swipeUp(steps)

fun swipeDownImpl(view: UiObject, steps: Int): Boolean = view.swipeDown(steps)

fun multiTouchImpl(view: UiObject, vararg touches: Array<MotionEvent.PointerCoords>): Boolean = view.performMultiPointerGesture(*touches)

fun twoPointerImpl(view: UiObject, firstStart: Point, firstEnd: Point, secondStart: Point, secondEnd: Point, steps: Int): Boolean = view.performTwoPointerGesture(firstStart, secondStart, firstEnd, secondEnd, steps)