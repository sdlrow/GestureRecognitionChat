package com.example.gesturerecognitionwebchat.utils

import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.coroutines.CoroutineContext

/**
 * Класс предоставляет потоки для корутин - [Dispatchers.Main] и [Dispatchers.IO]
 * в тестах мокается на [Dispatchers.Unconfined], чтобы все проходило там же, в тестовом потоке
 *
 * Used in [BaseViewModel] to make coroutine scope
 * should be mocked in tests (find [initTestKoin])
 */
class CoroutineProvider : KoinComponent {
    val Main: CoroutineContext by inject(named("main"))
    val IO: CoroutineContext by inject(named("io"))
}