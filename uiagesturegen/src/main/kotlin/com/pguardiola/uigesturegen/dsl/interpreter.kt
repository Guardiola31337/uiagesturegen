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

import android.support.test.uiautomator.UiObject
import kategory.*

fun Boolean.validate(ifInvalid: () -> GestureError): ValidatedNel<GestureError, Unit> =
        if (true) Unit.validNel()
        else ifInvalid().invalidNel()

fun Boolean.invalidate(ifInvalid: () -> GestureError): ValidatedNel<GestureError, Unit> =
        ifInvalid().invalidNel()

fun Boolean.either(ifInvalid: () -> GestureError): Either<GestureError, Unit> =
        if (true) Unit.right()
        else ifInvalid().left()


@Suppress("UNCHECKED_CAST")
fun safeInterpreter(view: UiObject): FunctionK<GesturesDSLHK, ValidatedKindPartial<NonEmptyList<GestureError>>> =
        object : FunctionK<GesturesDSLHK, ValidatedKindPartial<NonEmptyList<GestureError>>> {
            override fun <A> invoke(fa: GesturesDSLKind<A>): ValidatedKind<NonEmptyList<GestureError>, A> {
                val g = fa.ev()
                return when (g) {
                    // TODO Finish WithView
                    //is GesturesDSL.WithView -> g.f(view).fold({ _ -> Unit.validNel() }, {it.invalidNel()})
                    is GesturesDSL.Combine -> {
                        val aResult = g.a.validate(view).map { _ -> Unit }
                        val bResult = g.b.validate(view).map { _ -> Unit }
                        aResult.combineK(bResult, NonEmptyList.semigroup<GestureError>())
                    }
                    is GesturesDSL.Click -> view.click().validate({ GestureError.ClickError(view) })
                    is GesturesDSL.PinchIn -> view.pinchIn(g.percent, g.steps).validate({ GestureError.PinchInError(view) })
                    is GesturesDSL.PinchOut -> view.pinchOut(g.percent, g.steps).validate({ GestureError.PinchOutError(view) })
                    is GesturesDSL.SwipeLeft -> view.swipeLeft(g.steps).validate({ GestureError.SwipeLeftError(view) })
                    is GesturesDSL.SwipeRight -> view.swipeRight(g.steps).validate({ GestureError.SwipeRightError(view) })
                    is GesturesDSL.SwipeUp -> view.swipeUp(g.steps).validate({ GestureError.SwipeUpError(view) })
                    is GesturesDSL.SwipeDown -> view.swipeDown(g.steps).validate({ GestureError.SwipeDownError(view) })
                    is GesturesDSL.MultiTouch -> view.performMultiPointerGesture(*(g.touches.toTypedArray())).validate({ GestureError.MultiTouchError(view) })
                    is GesturesDSL.TwoPointer -> view.performTwoPointerGesture(g.firstStart, g.secondStart, g.firstEnd, g.secondEnd, g.steps).validate({ GestureError.TwoPointerError(view) })

                //is GesturesDSL.PinchOut -> AE.catch({ pinchOutImpl(view, g.percent, g.steps) }, { it.nel() })

                } as ValidatedKind<NonEmptyList<GestureError>, A>
            }
        }

@Suppress("UNCHECKED_CAST")
fun safeInterpreterEither(view: UiObject): FunctionK<GesturesDSLHK, EitherKindPartial<GestureError>> =
        object : FunctionK<GesturesDSLHK, EitherKindPartial<GestureError>> {
            override fun <A> invoke(fa: GesturesDSLKind<A>): EitherKind<GestureError, A> {
                val g = fa.ev()
                return when (g) {
                    is GesturesDSL.Combine -> {
                        val aResult = g.a.failFast(view).map { _ -> Unit }
                        val bResult = g.b.failFast(view).map { _ -> Unit }
                        aResult.combineK(bResult)
                    }
                    is GesturesDSL.Click -> view.click().either({ GestureError.ClickError(view) })
                    is GesturesDSL.PinchIn -> view.pinchIn(g.percent, g.steps).either({ GestureError.PinchInError(view) })
                    is GesturesDSL.PinchOut -> view.pinchOut(g.percent, g.steps).either({ GestureError.PinchOutError(view) })
                    is GesturesDSL.SwipeLeft -> view.swipeLeft(g.steps).either({ GestureError.SwipeLeftError(view) })
                    is GesturesDSL.SwipeRight -> view.swipeRight(g.steps).either({ GestureError.SwipeRightError(view) })
                    is GesturesDSL.SwipeUp -> view.swipeUp(g.steps).either({ GestureError.SwipeUpError(view) })
                    is GesturesDSL.SwipeDown -> view.swipeDown(g.steps).either({ GestureError.SwipeDownError(view) })
                    is GesturesDSL.MultiTouch -> view.performMultiPointerGesture(*(g.touches.toTypedArray())).either({ GestureError.MultiTouchError(view) })
                    is GesturesDSL.TwoPointer -> view.performTwoPointerGesture(g.firstStart, g.secondStart, g.firstEnd, g.secondEnd, g.steps).either({ GestureError.TwoPointerError(view) })

                //is GesturesDSL.PinchOut -> AE.catch({ pinchOutImpl(view, g.percent, g.steps) }, { it.nel() })

                } as EitherKind<GestureError, A>
            }
//                val g = fa.ev()
//                return when (g) {
//                    is GesturesDSL.Click -> {
//                        if (view.click()) Unit.validNel<ErrorType, Unit>()
//                        else GestureError.ClickError(view).invalidNel<ErrorType, Unit>()
//                    }
//
//                    //is GesturesDSL.PinchOut -> AE.catch({ pinchOutImpl(view, g.percent, g.steps) }, { it.nel() })
//
//                }
//            }
        }

//fun clickImpl(view: UiObject): Boolean = view.click()
//
//fun pinchInImpl(view: UiObject, percent: Int, steps: Int): Boolean = view.pinchIn(percent, steps)
//
//fun pinchOutImpl(view: UiObject, percent: Int, steps: Int): Boolean = view.pinchOut(percent, steps)
//
//fun swipeLeftImpl(view: UiObject, steps: Int): Boolean = view.swipeLeft(steps)
//
//fun swipeRightImpl(view: UiObject, steps: Int): Boolean = view.swipeRight(steps)
//
//fun swipeUpImpl(view: UiObject, steps: Int): Boolean = view.swipeUp(steps)
//
//fun swipeDownImpl(view: UiObject, steps: Int): Boolean = view.swipeDown(steps)
//
//fun multiTouchImpl(view: UiObject, touches: List<Array<MotionEvent.PointerCoords>>): Boolean = view.performMultiPointerGesture(*(touches.toTypedArray()))
//
//fun twoPointerImpl(view: UiObject, firstStart: Point, firstEnd: Point, secondStart: Point, secondEnd: Point, steps: Int): Boolean = view.performTwoPointerGesture(firstStart, secondStart, firstEnd, secondEnd, steps)