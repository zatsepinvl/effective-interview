package org.speechify.tests.helpers

data class TestDataList<T>(val list: List<T>) {
    override fun toString(): String = list.joinToString("\n\n")
}