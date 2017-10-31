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

import android.support.test.uiautomator.Configurator
import android.support.test.uiautomator.UiObject
import kategory.*

@Suppress("UNCHECKED_CAST")
fun safeInterpreter(view: UiObject): FunctionK<GesturesDSLHK, ValidatedKindPartial<NonEmptyList<GestureError>>> =
        object : FunctionK<GesturesDSLHK, ValidatedKindPartial<NonEmptyList<GestureError>>> {
            override fun <A> invoke(fa: GesturesDSLKind<A>): ValidatedKind<NonEmptyList<GestureError>, A> {
                val g = fa.ev()
                return when (g) {
                    is GesturesDSL.WithView -> Try { g.f(view) }.fold(
                            { GestureError.UnknownError(it).invalidNel<GestureError, A>() },
                            { it.validNel() }
                    )
                    is GesturesDSL.Combine -> {
                        val aResult = g.a.validate(view).map { _ -> Unit }
                        val bResult = g.b.validate(view).map { _ -> Unit }
                        aResult.combineK(bResult, NonEmptyList.semigroup<GestureError>())
                    }
                    is GesturesDSL.Click -> view.click().validate({ GestureError.ClickError(view) })
                    is GesturesDSL.DoubleTap -> Try {
                        doubleTapImpl(view)
                    }.fold(
                            { GestureError.DoubleTapError(view).invalidNel<GestureError, Unit>() },
                            { it.validNel() }
                    )
                    is GesturesDSL.PinchIn -> view.pinchIn(g.percent, g.steps).validate({ GestureError.PinchInError(view) })
                    is GesturesDSL.PinchOut -> view.pinchOut(g.percent, g.steps).validate({ GestureError.PinchOutError(view) })
                    is GesturesDSL.SwipeLeft -> view.swipeLeft(g.steps).validate({ GestureError.SwipeLeftError(view) })
                    is GesturesDSL.SwipeRight -> view.swipeRight(g.steps).validate({ GestureError.SwipeRightError(view) })
                    is GesturesDSL.SwipeUp -> view.swipeUp(g.steps).validate({ GestureError.SwipeUpError(view) })
                    is GesturesDSL.SwipeDown -> view.swipeDown(g.steps).validate({ GestureError.SwipeDownError(view) })
                    is GesturesDSL.MultiTouch -> view.performMultiPointerGesture(*(g.touches.toTypedArray())).validate({ GestureError.MultiTouchError(view) })
                    is GesturesDSL.TwoPointer -> view.performTwoPointerGesture(g.firstStart, g.secondStart, g.firstEnd, g.secondEnd, g.steps).validate({ GestureError.TwoPointerError(view) })

                } as ValidatedKind<NonEmptyList<GestureError>, A>
            }
        }

private fun doubleTapImpl(view: UiObject) {
    val configurator = Configurator.getInstance()
    configurator.actionAcknowledgmentTimeout = 40
    view.click()
    view.click()
    configurator.actionAcknowledgmentTimeout = 3000
}

@Suppress("UNCHECKED_CAST")
fun safeInterpreterEither(view: UiObject): FunctionK<GesturesDSLHK, EitherKindPartial<GestureError>> =
        object : FunctionK<GesturesDSLHK, EitherKindPartial<GestureError>> {
            override fun <A> invoke(fa: GesturesDSLKind<A>): EitherKind<GestureError, A> {
                val g = fa.ev()
                return when (g) {
                    is GesturesDSL.WithView -> {
                        Either.monadError<GestureError>().catch({ g.f(view) }, { GestureError.UnknownError(it) })
                    }
                    is GesturesDSL.Combine -> {
                        val aResult = g.a.failFast(view).map { _ -> Unit }
                        val bResult = g.b.failFast(view).map { _ -> Unit }
                        aResult.combineK(bResult)
                    }
                    is GesturesDSL.Click -> view.click().either({ GestureError.ClickError(view) })
                    is GesturesDSL.DoubleTap -> Either.monadError<GestureError>().catch({
                        doubleTapImpl(view)
                    }, { GestureError.DoubleTapError(view) }
                    )
                    is GesturesDSL.PinchIn -> view.pinchIn(g.percent, g.steps).either({ GestureError.PinchInError(view) })
                    is GesturesDSL.PinchOut -> view.pinchOut(g.percent, g.steps).either({ GestureError.PinchOutError(view) })
                    is GesturesDSL.SwipeLeft -> view.swipeLeft(g.steps).either({ GestureError.SwipeLeftError(view) })
                    is GesturesDSL.SwipeRight -> view.swipeRight(g.steps).either({ GestureError.SwipeRightError(view) })
                    is GesturesDSL.SwipeUp -> view.swipeUp(g.steps).either({ GestureError.SwipeUpError(view) })
                    is GesturesDSL.SwipeDown -> view.swipeDown(g.steps).either({ GestureError.SwipeDownError(view) })
                    is GesturesDSL.MultiTouch -> view.performMultiPointerGesture(*(g.touches.toTypedArray())).either({ GestureError.MultiTouchError(view) })
                    is GesturesDSL.TwoPointer -> view.performTwoPointerGesture(g.firstStart, g.secondStart, g.firstEnd, g.secondEnd, g.steps).either({ GestureError.TwoPointerError(view) })

                } as EitherKind<GestureError, A>
            }
        }

@Suppress("UNCHECKED_CAST")
fun loggingInterpreter(view: UiObject): FunctionK<GesturesDSLHK, EitherKindPartial<GestureError>> =
        object : FunctionK<GesturesDSLHK, EitherKindPartial<GestureError>> {
            override fun <A> invoke(fa: GesturesDSLKind<A>): EitherKind<GestureError, A> {
                println(fa)
                return safeInterpreter(view) as EitherKind<GestureError, A>
            }
        }

fun Boolean.validate(ifInvalid: () -> GestureError): ValidatedNel<GestureError, Unit> =
        if (true) Unit.validNel()
        else ifInvalid().invalidNel()

fun Boolean.invalidate(ifInvalid: () -> GestureError): ValidatedNel<GestureError, Unit> =
        ifInvalid().invalidNel()

fun Boolean.either(ifInvalid: () -> GestureError): Either<GestureError, Unit> =
        if (true) Unit.right()
        else ifInvalid().left()
