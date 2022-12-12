package com.bignerdranch.android.cs4750finaproject
import kotlinx.coroutines.flow.MutableSharedFlow
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "WordleViewModel"
const val EMPTY_STRING = ""
const val NUMBER_OF_ROWS = 6
const val ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val ALPHA_LEN = 26
val DEFAULT_KEY = Key (backgrdColor=R.color.gray, textColor=R.color.black)
val DEFAULT_LETTER = Letter("", R.drawable.border, R.color.black)


class WordleViewModel(): ViewModel(){
    val currPos = Position (0,0) //the game starts off at the left top corner (0,0)
    var guess: String = ""
    var wordle = "ouija"
    /***
     *  How is this used?:
     *      Provides functions to emit values to the flow'
     *      Also provides the abiltites to emit a value
     */
    val signal = MutableSharedFlow<Signal>()

    /**
     * listOfTextViews is a 2d list used to model the grid (6 rows and 5 columns)
     * We initialize each element with the " " to denote the start of the game
     * The elements of this list are of type MutableStateFlow
     */
    val listOfTextViews = List(NUMBER_OF_ROWS)
    {
        List(WORD_LEN){ MutableStateFlow(Letter(" "))}
    }

    /***
     * creating a map of listofKeys and mapping each letter of the alpahabet
     * to the default val of keys (start of game scenario)
     */
    val listOfKeys = mutableMapOf<String, MutableStateFlow<Key>>().apply{
        ALPHA.forEach{char ->
            this[char.toString()] = MutableStateFlow<Key>(DEFAULT_KEY)
        }
    }

    /***
     * EXPLAIN ME PLEASE
     * Initialize a stack to store those words that are not in letter
     */
    val letterNotInWord = Stack<MutableStateFlow<Letter>> ()

    /***
     * EXPLAIN ME PLEASE
     * Same idea as above but instead doing it for the keys
     */
    val keyNotInWord = Stack<MutableStateFlow<Key>>()

    fun setLetter(letter:String){
        if (currPos.col < WORD_LEN){
            /**
             * Launch coroutine that will be canceled when ViewModel is cleared
             *      Specifically used to emit letter to view?
             *      Emitting letter that was enter by user to the current row of user
             */
            viewModelScope.launch{
                listOfTextViews[currPos.row][currPos.col].emit(Letter(letter))
            }
            currPos.nextCol() // move to next entry from user
        }
    }

    fun deleteLetter(){
        if (currPos.col>0){ //
            currPos.prevCol() // move back on

            viewModelScope.launch {
                listOfTextViews[currPos.row][currPos.col].emit(Letter("")) // setting delete col to empty string
            }

        }
    }

    fun resetGame(){
        currPos.reset()
        viewModelScope.launch{
            while (!letterNotInWord.empty()){
                //pop all values from and reset to default
                letterNotInWord.pop().emit(DEFAULT_LETTER)
                keyNotInWord.pop().emit(DEFAULT_KEY)
            }
        }
        getNewWord()
    }


    /***
     * Basic function definitions
     */
    /***
     * this function can only be called within a coroutine
     * and does not block the underlying thread
     */
    suspend fun checkRow (){
        guess = listToWord(listOfTextViews[currPos.row]
            .filter{it.value.letter != " "} // first check that there are no empty spots in the guess
            .map {it.value.letter}).lowercase(Locale.getDefault())
        // checking possible outcomes and setting state according to
        //users word input
        when {

            // the user has won the game so the game should end
            wordle == guess ->{
                signal.emit(Signal.GAMEOVER)
            }
            // the user has not entered enough words
            guess.length < 5 -> {
                signal.emit(Signal.NEEDLETTER)
            }
            // the word is not the wordle of the day, but need to check that it is a valid entry so
            // we can either end the game or move to the next row in the view
            // make the checking function call here !!!!

            //
            else ->{
                signal.emit(Signal.NOTAWORD)
            }

        }
    }

    /***
     * this function is used to compare the users guess
     * to the actual wordle and then determine what color the
     * Letter should be in the View -- however the actual view isnt adjusted here
     * but in the function that follows
     */
    fun checkLetterColor ():List<Letter>{
        val list = mutableListOf<Letter>()
        listOfTextViews[currPos.row].forEachIndexed { index, flow ->
            list.add(when (guess[index]) {
                wordle[index] -> {
                    Letter(flow.value.letter, R.color.green, R.color.white)
                }
                in wordle.filterIndexed { i, s -> guess[i] != s } -> {
                    Letter(flow.value.letter, R.color.yellow, R.color.white)
                }
                else -> {
                    Letter(flow.value.letter, R.color.dark_gray, R.color.white)
                }
            })
        }
        return list
    }

    /***
     * This function changes the view according to the results of the function above
     */
    fun emitColor(list: List<Letter> = checkLetterColor()) {
        viewModelScope.launch {
            listOfTextViews[currPos.row].forEachIndexed { index, flow ->
                val letter = list[index]
                flow.emit(letter)
                letterNotInWord.push(flow)

                val key = listOfKeys[letter.letter]!!
                val backgrd = when (key.value.backgrdColor) {
                    R.color.green -> R.color.green
                    R.color.yellow -> if (letter.backgrdColor == R.color.green) R.color.green else R.color.yellow
                    else -> letter.backgrdColor
                }

                key.emit(Key(backgrd, letter.textColor))
                keyNotInWord.push(key)
            }
        }
    }

    /***
     * implememt fully once the list of differnt wordles is implemented.
     */
    fun getNewWord ()
    {
        wordle = "vowel"
    }
}

