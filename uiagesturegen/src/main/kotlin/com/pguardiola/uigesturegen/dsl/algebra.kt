package com.pguardiola.uigesturegen.dsl

import android.graphics.Point
import android.view.MotionEvent
import kategory.Free
import kategory.FreeMonad
import kategory.HK


typealias ActionDSL<A> = Free<GesturesDSL.F, A>

sealed class GesturesDSL<out A> : HK<GesturesDSL.F, A> {

    class F private constructor()

    object Click : ActionDSL<Boolean>()
    object PinchIn : ActionDSL<Boolean>()
    object PinchOut : ActionDSL<Boolean>()
    data class SwipeLeft(val steps: Integer) : ActionDSL<Boolean>()
    data class SwipeRight(val steps: Integer) : ActionDSL<Boolean>()
    data class SwipeUp(val steps: Integer) : ActionDSL<Boolean>()
    data class SwipeDown(val steps: Integer) : ActionDSL<Boolean>()
    data class MultiTouch(vararg touches: MotionEvent.PointerCoords[]) : ActionDSL<Boolean>()
    data class TwoPointer(val firstStart: Point, val secondStart: Point, val firstEnd: Point, val secondEnd: Point, val steps: Integer) : ActionDSL<Boolean>()

    companion object : FreeMonad<F>

}

fun click(): DSLAction<Boolean> = Free.liftF(GesturesDSL.Click)
fun pinchIn(): DSLAction<Boolean> = Free.liftF(GesturesDSL.PinchIn)
fun pinchOut(): DSLAction<Boolean> = Free.liftF(GesturesDSL.PinchOut)
fun swipeLeft(steps: Integer): ActionDSL<Boolean> = Free.liftF(GesturesDSL.SwipeLeft(steps))
fun swipeRight(steps: Integer): ActionDSL<Boolean> = Free.liftF(GesturesDSL.SwipeRight(steps))
fun swipeUp(steps: Integer): ActionDSL<Boolean> = Free.liftF(GesturesDSL.SwipeUp(steps))
fun swipeDown(steps: Integer): ActionDSL<Boolean> = Free.liftF(GesturesDSL.SwipeDown(steps))
fun multiTouch(touches: MotionEvent.PointerCoords[]): ActionDSL<Boolean> = Free.liftF(GesturesDSL.MultiTouch(*touches))
fun twoPointer(firstStart: Point, secondStart: Point, firstEnd: Point, secondEnd: Point, steps: Integer): ActionDSL<Boolean> = Free.liftF(GesturesDSL.TwoPointer(firstStart, secondStart, firstEnd, secondEnd, steps))
