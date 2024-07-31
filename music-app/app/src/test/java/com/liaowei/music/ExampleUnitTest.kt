package com.liaowei.music

import org.junit.Test

import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        // println("${228206/1000/60} : ${228206/1000%60}")
        val random = java.util.Random()
        for (i in 0 until 20) {
            print("${random.nextInt(11)} ")
        }
    }
}