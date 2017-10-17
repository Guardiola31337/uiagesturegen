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

import android.gesture.Gesture
import android.graphics.Point
import android.support.test.uiautomator.SearchCondition
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiObject
import android.support.test.uiautomator.UiSelector
import android.view.MotionEvent
import kategory.*

@higherkind sealed class GesturesDSL<A> : GesturesDSLKind<A> {

//    object PressHome : GesturesDSL<Unit>()
//    data class Wait<R>(val condition: SearchCondition<R>, val timeout: Long) : GesturesDSL<R>()
//    data class FindObject(val selector: UiSelector) : GesturesDSL<UiObject>()
//    data class WithDevice<A>(val f: (UiDevice) -> A) : GesturesDSL<A>()

    // TODO RJ Finish WithView
    //data class WithView(val f: (UiObject) -> Option<GestureError>) : GesturesDSL<Unit>()
    data class Combine(val a: ActionDSL<Unit>, val b: ActionDSL<Unit>): GesturesDSL<Unit>()
    object Click : GesturesDSL<Unit>()
    data class PinchIn(val percent: Int, val steps: Int) : GesturesDSL<Unit>()
    data class PinchOut(val percent: Int, val steps: Int) : GesturesDSL<Unit>()
    data class SwipeLeft(val steps: Int) : GesturesDSL<Unit>()
    data class SwipeRight(val steps: Int) : GesturesDSL<Unit>()
    data class SwipeUp(val steps: Int) : GesturesDSL<Unit>()
    data class SwipeDown(val steps: Int) : GesturesDSL<Unit>()
    data class MultiTouch(val touches: List<Array<MotionEvent.PointerCoords>>) : GesturesDSL<Unit>()
    data class TwoPointer(val firstStart: Point, val firstEnd: Point, val secondStart: Point, val secondEnd: Point, val steps: Int) : GesturesDSL<Unit>()

    // TODO RJ Question: Where are we using this?
    companion object : FreeApplicativeApplicativeInstance<GesturesDSLHK>
}

sealed class GestureError {
    data class ClickError(val view: UiObject): GestureError()
    data class PinchInError(val view: UiObject): GestureError()
    data class PinchOutError(val view: UiObject): GestureError()
    data class SwipeLeftError(val view: UiObject): GestureError()
    data class SwipeRightError(val view: UiObject): GestureError()
    data class SwipeUpError(val view: UiObject): GestureError()
    data class SwipeDownError(val view: UiObject): GestureError()
    data class MultiTouchError(val view: UiObject): GestureError()
    data class TwoPointerError(val view: UiObject): GestureError()
    data class UnknownError(val e: Throwable): GestureError()
}

typealias ActionDSL<A> = FreeApplicative<GesturesDSLHK, A>

fun ActionDSL<Unit>.failFast(view: UiObject): Either<GestureError, Unit> =
        Try {
            this.foldMap(
                    safeInterpreterEither(view),
                    Either.applicative<GestureError>()).ev()
        }.fold({ GestureError.UnknownError(it).left() }, { it })

fun Either<GestureError, Unit>.hasCompletedCorrectly(): Boolean = this.fold({ false }, { true })

fun ActionDSL<Unit>.validate(view: UiObject): ValidatedNel<GestureError, Unit> =
        Try {
            this.foldMap(
                    safeInterpreter(view),
                    Validated.applicative<NonEmptyList<GestureError>>()).ev()
        }.fold({ GestureError.UnknownError(it).invalidNel() }, { it })

fun FreeApplicativeKind<GesturesDSLHK, Unit>.validate(view: UiObject): Validated<NonEmptyList<GestureError>, Unit> =
        this.ev().validate(view)

fun ValidatedNel<GestureError, Unit>.hasCompletedCorrectly(): Boolean = this.isValid

fun ValidatedNel<GestureError, Unit>.errors(): List<GestureError> =
        this.fold({ it.all }, { emptyList() })

fun ValidatedNel<GestureError, Unit>.hasError(e: GestureError): Boolean =
        this.errors().contains(e)

// TODO RJ Question: Does it make sense to support `invoke` notation?
//operator fun <A> ActionDSL<A>.invoke(uiDevice: UiDevice): Try<A> =
//        this.runF<TryHK, A>(uiDevice).ev()
//
//operator fun <A> FreeApplicativeKind<GesturesDSLHK, A>.invoke(uiDevice: UiDevice): Try<A> =
//        this.ev().invoke(uiDevice)

//data class ManyExceptions(val ex: NonEmptyList<Throwable>) : RuntimeException()
//
//fun <A> ActionDSL<A>.runUnsafe(uiDevice: UiDevice): A =
//        this.run(uiDevice).fold({ e -> throw ManyExceptions(e) }, { it })
//
//fun <A> FreeApplicativeKind<GesturesDSLHK, A>.runUnsafe(uiDevice: UiDevice): A =
//        this.ev().runUnsafe(uiDevice)
//
//fun pressHome(): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.PressHome)

//fun <R> wait(condition: SearchCondition<R>, timeout: Long): ActionDSL<R> = FreeApplicative.liftF(GesturesDSL.Wait(condition, timeout))

//fun findObject(selector: UiSelector): ActionDSL<UiObject> = FreeApplicative.liftF(GesturesDSL.FindObject(selector))

//fun <A> withDevice(f: (UiDevice) -> A): ActionDSL<A> = FreeApplicative.liftF(GesturesDSL.WithDevice(f))
fun combine(a: ActionDSL<Unit>, b: ActionDSL<Unit>): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.Combine(a, b))

fun ActionDSL<Unit>.click(): ActionDSL<Unit> = combine(this, com.pguardiola.uigesturegen.dsl.click())
fun click(): ActionDSL<Unit> = FreeApplicative.liftF(GesturesDSL.Click)

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

//fun ActionDSL<UiObject>.swipeLeft(steps: Int): ActionDSL<Unit> = TODO()

//private fun <A> ActionDSL<UiObject>.andThen(action: (UiObject) -> GesturesDSL<A>) = this.flatMap { ui -> FreeApplicative.liftF(action(ui)).map { _ -> ui } }
//
//fun ActionDSL<UiObject>.pinchIn(percent: Int, steps: Int): ActionDSL<Boolean> = this.flatMap { ui -> FreeApplicative.liftF(GesturesDSL.PinchIn(ui, percent, steps)) }
//
//fun ActionDSL<UiObject>.pinchOut(percent: Int, steps: Int): ActionDSL<Boolean> = this.flatMap { ui -> FreeApplicative.liftF(GesturesDSL.PinchOut(ui, percent, steps)) }
//
//fun ActionDSL<UiObject>.swipeRight(steps: Int): ActionDSL<Boolean> = this.flatMap { ui -> FreeApplicative.liftF(GesturesDSL.SwipeRight(ui, steps)) }
//
//fun ActionDSL<UiObject>.swipeUp(steps: Int): ActionDSL<Boolean> = this.flatMap { ui -> FreeApplicative.liftF(GesturesDSL.SwipeUp(ui, steps)) }
//
//fun ActionDSL<UiObject>.swipeDown(steps: Int): ActionDSL<Boolean> = this.flatMap { ui -> FreeApplicative.liftF(GesturesDSL.SwipeDown(ui, steps)) }
//
//fun ActionDSL<UiObject>.multiTouch(touches: List<Array<MotionEvent.PointerCoords>>): ActionDSL<Boolean> = this.flatMap { ui -> FreeApplicative.liftF(GesturesDSL.MultiTouch(ui, touches)) }
//
//fun ActionDSL<UiObject>.twoPointer(firstStart: Point, firstEnd: Point, secondStart: Point, secondEnd: Point, steps: Int): ActionDSL<Boolean> = this.flatMap { ui -> FreeApplicative.liftF(GesturesDSL.TwoPointer(ui, firstStart, secondStart, firstEnd, secondEnd, steps)) }

// TODO RJ `traverse` usage + examples
inline fun <reified G, A, B> List<A>.traverse(crossinline f: (A) -> HK<G, B>, AP: Applicative<G> = applicative<G>()): HK<G, List<B>> =
        foldRight(AP.pure<List<B>>(emptyList()), { a: A, lglb: HK<G, List<B>> ->
            AP.map2(f(a), lglb, { it.b + it.a })
        })

//inline fun <reified G, A> List<HK<G, A>>.sequence(AP: Applicative<G>): HK<G, List<A>> =
//        traverse({ a -> a }, AP)
//
//fun <A> List<ActionDSL<A>>.sequence(): ActionDSL<List<A>> =
//        sequence(GesturesDSL).ev()
//
//fun <A> UiObject.actions(vararg perform: ActionDSL<A>): ActionDSL<List<A>> =
//        perform.map { it }.sequence()
//
//fun <A> List<UiObject>.actions(vararg perform: ActionDSL<A>): ActionDSL<List<A>> =
//        this.map { it.perform(*perform) }.sequence().map { it.flatten() }

// TODO findObject only returns one UiObject, consider findObjects instead
//fun clickables(): ActionDSL<UiObject> = findObject(UiSelector().clickable(true))

// TODO findObject only returns one UiObject, consider findObjects instead
//fun images(): ActionDSL<UiObject> = findObject(UiSelector().description("image"))
