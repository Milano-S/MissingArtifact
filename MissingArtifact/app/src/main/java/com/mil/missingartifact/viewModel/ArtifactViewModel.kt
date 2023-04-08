package com.mil.missingartifact.viewModel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel

private const val TAG = "ArtifactViewModel"
class ArtifactViewModel : ViewModel() {

    var listSum = 0

    //Reads input.txt file
    fun readFile(fileName: String, application: Application): MutableList<String> {
        val resultList = mutableListOf<String>()
        try {

            //Reads File
            val buffer = application.assets.open(fileName).bufferedReader()
            buffer.forEachLine {

                //Checks that File only contains numbers
                if (!isNumber(it)) {
                    Toast.makeText(application.applicationContext, "File Invalid, Values are not all numbers", Toast.LENGTH_SHORT).show()
                    return@forEachLine
                } else {
                    resultList.addAll(listOf(it))
                }
            }

        } catch (e: java.lang.Exception) {
            Log.i(TAG, e.message.toString())
        }

        return sortList(resultList)
    }

    //Sorts List
    fun sortList(listString: MutableList<String>): MutableList<String> {
        val sortedList = mutableListOf<String>()

        //Creates New Int List from string
        val listInt = mutableListOf<Int>()
        listString.forEach { item ->
            listInt.addAll(listOf(item.toInt()))
        }

        //Sum
        listInt.forEach { num -> listSum += num }

        //Adds Sorted Int Values to sortedList
        listInt.sort()
        val noDupList = removeDuplicates(listInt)
        sortedList.clear()
        noDupList.forEach { item ->
            sortedList.addAll(listOf(item.toString()))
        }

        return sortedList
    }

    //Returns New List with no duplicates
    fun removeDuplicates(list: List<Int>): List<Int> {
        val distinctList = mutableListOf<Int>()
        for (element in list) {
            if (!distinctList.contains(element)) {
                distinctList.add(element)
            }
        }

        return distinctList
    }

    //Checks for numbers
    fun isNumber(input: String): Boolean {
        val integerChars = '0'..'9'
        var dotOccurred = 0
        return input.all { it in integerChars || it == '.' && dotOccurred++ < 1 }
    }

}

