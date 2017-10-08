package com.pguardiola.uigesturegen.dsl

import android.graphics.Point
import android.view.MotionEvent
import kategory.*


@higherkind sealed class GesturesDSL<A> : GesturesDSLKind<A> {

    object Click : GesturesDSL<Boolean>()
    data class PinchIn(val percent: Int, val steps: Int) : GesturesDSL<Boolean>()
    data class PinchOut(val percent: Int, val steps: Int) : GesturesDSL<Boolean>()
    data class SwipeLeft(val steps: Int) : GesturesDSL<Boolean>()
    data class SwipeRight(val steps: Int) : GesturesDSL<Boolean>()
    data class SwipeUp(val steps: Int) : GesturesDSL<Boolean>()
    data class SwipeDown(val steps: Int) : GesturesDSL<Boolean>()
    data class MultiTouch(val touches: List<Array<MotionEvent.PointerCoords>>) : GesturesDSL<Boolean>()
    data class TwoPointer(val firstStart: Point, val firstEnd: Point, val secondStart: Point, val secondEnd: Point, val steps: Int) : GesturesDSL<Boolean>()

    companion object : FreeMonadInstance<GesturesDSLHK>
}

typealias FreeGesturesDSL<A> = Free<GesturesDSLHK, A>

inline fun <reified F> Free<GesturesDSLHK, Boolean>.run(
        interpreter: FunctionK<GesturesDSLHK, F>, MF: Monad<F> = monad()): HK<F, Boolean> =
        this.foldMap(interpreter, MF)

fun click(): FreeGesturesDSL<Boolean> = Free.liftF(GesturesDSL.Click)

fun pinchIn(percent: Int, steps: Int): FreeGesturesDSL<Boolean> = Free.liftF(GesturesDSL.PinchIn(percent, steps))

fun pinchOut(percent: Int, steps: Int): FreeGesturesDSL<Boolean> = Free.liftF(GesturesDSL.PinchOut(percent, steps))

fun swipeLeft(steps: Int): FreeGesturesDSL<Boolean> = Free.liftF(GesturesDSL.SwipeLeft(steps))

fun swipeRight(steps: Int): FreeGesturesDSL<Boolean> = Free.liftF(GesturesDSL.SwipeRight(steps))

fun swipeUp(steps: Int): FreeGesturesDSL<Boolean> = Free.liftF(GesturesDSL.SwipeUp(steps))

fun swipeDown(steps: Int): FreeGesturesDSL<Boolean> = Free.liftF(GesturesDSL.SwipeDown(steps))

fun multiTouch(touches: List<Array<MotionEvent.PointerCoords>>): FreeGesturesDSL<Boolean> = Free.liftF(GesturesDSL.MultiTouch(touches))

fun twoPointer(firstStart: Point, firstEnd: Point, secondStart: Point, secondEnd: Point, steps: Int): FreeGesturesDSL<Boolean> = Free.liftF(GesturesDSL.TwoPointer(firstStart, secondStart, firstEnd, secondEnd, steps))
