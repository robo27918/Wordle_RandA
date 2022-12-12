package com.bignerdranch.android.wordle_randa

/***
 * recursive function to turn list of characters into a string type
 */
fun listToWord(list: List<String>, res: String = ""): String {
    if (list.isEmpty()) {
        return res
    }else {
        return listToWord(list.drop(1), res + list.first())
    }
}