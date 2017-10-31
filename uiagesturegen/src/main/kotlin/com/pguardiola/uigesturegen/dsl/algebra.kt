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

package com.pguardiola.uigesturegen.dsl

import android.graphics.Point
import android.support.test.uiautomator.UiObject
import android.view.MotionEvent
import kategory.*

@higherkind sealed class GesturesDSL<A> : GesturesDSLKind<A> {
    data class WithView<A>(val f: (UiObject) -> A) : GesturesDSL<A>()
    data class Combine(val a: ActionDSL<Unit>, val b: ActionDSL<Unit>) : GesturesDSL<Unit>()
    object Click : GesturesDSL<Unit>()
    object DoubleTap : GesturesDSL<Unit>()
    data class PinchIn(val percent: Int, val steps: Int) : GesturesDSL<Unit>()
    data class PinchOut(val percent: Int, val steps: Int) : GesturesDSL<Unit>()
    data class SwipeLeft(val steps: Int) : GesturesDSL<Unit>()
    data class SwipeRight(val steps: Int) : GesturesDSL<Unit>()
    data class SwipeUp(val steps: Int) : GesturesDSL<Unit>()
    data class SwipeDown(val steps: Int) : GesturesDSL<Unit>()
    data class MultiTouch(val touches: List<Array<MotionEvent.PointerCoords>>) : GesturesDSL<Unit>()
    data class TwoPointer(val firstStart: Point, val firstEnd: Point, val secondStart: Point, val secondEnd: Point, val steps: Int) : GesturesDSL<Unit>()
    companion object : FreeApplicativeApplicativeInstance<GesturesDSLHK>
}

sealed class GestureError {
    data class ClickError(val view: UiObject) : GestureError()
    data class DoubleTapError(val view: UiObject) : GestureError()
    data class PinchInError(val view: UiObject) : GestureError()
    data class PinchOutError(val view: UiObject) : GestureError()
    data class SwipeLeftError(val view: UiObject) : GestureError()
    data class SwipeRightError(val view: UiObject) : GestureError()
    data class SwipeUpError(val view: UiObject) : GestureError()
    data class SwipeDownError(val view: UiObject) : GestureError()
    data class MultiTouchError(val view: UiObject) : GestureError()
    data class TwoPointerError(val view: UiObject) : GestureError()
    data class UnknownError(val e: Throwable) : GestureError()
}

typealias ActionDSL<A> = FreeApplicative<GesturesDSLHK, A>

fun <A> ActionDSL<A>.failFast(view: UiObject): Either<GestureError, A> =
        Try {
            this.foldMap(
                    safeInterpreterEither(view),
                    Either.applicative<GestureError>()).ev()
        }.fold({ GestureError.UnknownError(it).left() }, { it })

fun <A> Either<GestureError, A>.hasCompletedCorrectly(): Boolean = this.fold({ false }, { true })

fun <A> ActionDSL<A>.validate(view: UiObject): ValidatedNel<GestureError, A> =
        Try {
            this.foldMap(
                    safeInterpreter(view),
                    Validated.applicative<NonEmptyList<GestureError>>()).ev()
        }.fold({ GestureError.UnknownError(it).invalidNel() }, { it })

fun <A> FreeApplicativeKind<GesturesDSLHK, A>.validate(view: UiObject): Validated<NonEmptyList<GestureError>, A> =
        this.ev().validate(view)

fun <A> ValidatedNel<GestureError, A>.hasCompletedCorrectly(): Boolean = this.isValid

fun <A> ValidatedNel<GestureError, A>.errors(): List<GestureError> =
        this.fold({ it.all }, { emptyList() })

fun <A> ValidatedNel<GestureError, A>.hasError(e: GestureError): Boolean =
        this.errors().contains(e)

fun <A> withView(f: (UiObject) -> A): ActionDSL<A> = FreeApplicative.liftF(GesturesDSL.WithView(f))

fun combine(a: ActionDSL<Unit>, b: ActionDSL<Unit>): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.Combine(a, b))

fun ActionDSL<Unit>.click(): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.click())
fun click(): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.Click)

fun ActionDSL<Unit>.doubleTap(): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.doubleTap())
fun doubleTap(): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.DoubleTap)

fun ActionDSL<Unit>.pinchIn(percent: Int, steps: Int): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.pinchIn(percent, steps))
fun pinchIn(percent: Int, steps: Int): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.PinchOut(percent, steps))

fun ActionDSL<Unit>.pinchOut(percent: Int, steps: Int): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.pinchOut(percent, steps))
fun pinchOut(percent: Int, steps: Int): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.PinchOut(percent, steps))

fun ActionDSL<Unit>.swipeLeft(steps: Int): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.swipeLeft(steps))
fun swipeLeft(steps: Int): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.SwipeLeft(steps))

fun ActionDSL<Unit>.swipeRight(steps: Int): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.swipeRight(steps))
fun swipeRight(steps: Int): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.SwipeRight(steps))

fun ActionDSL<Unit>.swipeUp(steps: Int): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.swipeUp(steps))
fun swipeUp(steps: Int): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.SwipeUp(steps))

fun ActionDSL<Unit>.swipeDown(steps: Int): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.swipeDown(steps))
fun swipeDown(steps: Int): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.SwipeDown(steps))

fun ActionDSL<Unit>.multiTouch(touches: List<Array<MotionEvent.PointerCoords>>): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.multiTouch(touches))
fun multiTouch(touches: List<Array<MotionEvent.PointerCoords>>): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.MultiTouch(touches))

fun ActionDSL<Unit>.twoPointer(firstStart: Point, firstEnd: Point, secondStart: Point, secondEnd: Point, steps: Int): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.twoPointer(firstStart, secondStart, firstEnd, secondEnd, steps))
fun twoPointer(firstStart: Point, firstEnd: Point, secondStart: Point, secondEnd: Point, steps: Int): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.TwoPointer(firstStart, secondStart, firstEnd, secondEnd, steps))

operator fun ActionDSL<Unit>.plus(other: ActionDSL<Unit>): ActionDSL<Unit> =
        combine(this, other)

inline fun <reified G, A, B> List<A>.traverse(crossinline f: (A) -> HK<G, B>, AP: Applicative<G> = applicative<G>()): HK<G, List<B>> =
        foldRight(AP.pure<List<B>>(emptyList()), { a: A, lglb: HK<G, List<B>> ->
            AP.map2(f(a), lglb, { it.b + it.a })
        })

inline fun <reified G, A> List<HK<G, A>>.sequence(AP: Applicative<G>): HK<G, List<A>> =
        traverse({ a -> a }, AP)

fun <A> List<ActionDSL<A>>.sequence(): ActionDSL<List<A>> =
        sequence(GesturesDSL).ev()
