//package com.pguardiola.uigesturegen.dsl
//
//import android.support.test.uiautomator.*
//import kategory.*
//
//typealias DSLAction<A> = Free<GestureDSL.F, A>
//
//sealed class GestureDSL<out A> : HK<GestureDSL.F, A> {
//
//    class F private constructor()
//
//    object PressHome : GestureDSL<Boolean>()
//    data class Wait<R>(val condition: SearchCondition<R>, val timeout: Long) : GestureDSL<R>()
//    data class FindObject(val selector: UiSelector) : GestureDSL<UiObject>()
//    data class WithDevice<out A>(val f: (UiDevice) -> A) : GestureDSL<A>()
//
//    companion object : FreeMonad<GestureDSL.F>
//
//}
//
//@Suppress("UNCHECKED_CAST")
//class SafeInterpreter<F>(val M: MonadError<F, Throwable>, val device: UiDevice) : FunctionK<GestureDSL.F, F> {
//    override fun <A> invoke(fa: HK<GestureDSL.F, A>): HK<F, A> {
//        val g = fa.ev()
//        return when (g) {
//            is GestureDSL.PressHome -> M.pure(device.pressHome()) // all M.pure should be changed to M.catch once that is available in Kategory
//            is GestureDSL.Wait<*> -> M.pure(device.wait(g.condition, g.timeout))
//            is GestureDSL.FindObject -> M.pure(device.findObject(g.selector))
//            is GestureDSL.WithDevice<*> -> M.pure(g.f(device))
//        } as HK<F, A>
//    }
//}
//
//fun <A> HK<GestureDSL.F, A>.ev(): GestureDSL<A> = this as GestureDSL<A>
//
//inline fun <reified G, A, B> List<A>.traverse(crossinline f: (A) -> HK<G, B>, AP: Applicative<G> = applicative<G>()): HK<G, List<B>> =
//        foldRight(AP.pure<List<B>>(emptyList()), { a: A, lglb: HK<G, List<B>> ->
//            AP.map2(f(a), lglb, { it.b + it.a })
//        })
//
//inline fun <reified G, A> List<HK<G, A>>.sequence(AP: Applicative<G>): HK<G, List<A>> =
//        traverse({ a -> a }, AP)
//
//inline fun <A> List<DSLAction<A>>.sequence(): DSLAction<List<A>> =
//        sequence(GestureDSL).ev()
//
//fun <A, B> List<DSLAction<A>>.transform(f: (A) -> B): DSLAction<List<B>> =
//        sequence(GestureDSL).ev().map { list -> list.map(f) }
//
//fun pressHome(): DSLAction<Boolean> = Free.liftF(GestureDSL.PressHome)
//fun <R> wait(condition: SearchCondition<R>, timeout: Long): DSLAction<R> = Free.liftF(GestureDSL.Wait(condition, timeout))
//fun findObject(selector: UiSelector): DSLAction<UiObject> = Free.liftF(GestureDSL.FindObject(selector))
//fun <A> withDevice(f: (UiDevice) -> A): DSLAction<A> = Free.liftF(GestureDSL.WithDevice(f))
//
//fun <A> Free<GestureDSL.F, A>.run(device: UiDevice): Try<A> =
//        this.foldMap(SafeInterpreter(Try, device), Try).ev()
//
//fun <A> Free<GestureDSL.F, A>.unsafeRun(device: UiDevice): A =
//        run(device).fold({ e -> throw e }, { it })
