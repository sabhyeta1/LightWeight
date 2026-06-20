package com.example.lightweight.util

/** zur umwandlung von Exceptions in eine verständliche Sprache für den user - NFR07*/

fun Throwable.httpStatusOrNull(): Int? = (this as? retrofit2.HttpException)?.code()
fun Throwable.isNetworkError(): Boolean = this is java.io.IOException