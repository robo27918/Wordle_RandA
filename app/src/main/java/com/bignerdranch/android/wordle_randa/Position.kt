package com.bignerdranch.android.cs4750finaproject

/***
 * class to provide basic methods to be able to move left or right in the row
 */
class Position (var row:Int, var col:Int) {
    fun nextCol (){
        col +=1
    }
    fun prevCol (){
        col -=1
    }
    fun nextRow (){
        row +=1
        col = 0
    }
    fun reset (){
        //simply set row and col to zero
        row = 0
        col =0
    }

}