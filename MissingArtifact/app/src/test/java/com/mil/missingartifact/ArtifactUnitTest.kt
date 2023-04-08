package com.mil.missingartifact

import com.mil.missingartifact.viewModel.ArtifactViewModel
import org.junit.Assert
import org.junit.Test


class ArtifactUnitTest {

    private val vm = ArtifactViewModel()

    @Test
    fun check_SortList() {
        //Unsorted
        val listString = mutableListOf<String>("15", "4", "10", "6")
        //Sorted
        val listStringSorted = mutableListOf<String>( "4", "6", "10", "15")

        //Assert.assertEquals({EXPECTED_VALUE}, {ACTUAL_VALUE})
        Assert.assertEquals(listStringSorted, vm.sortList(listString))
    }

    @Test
    fun check_removeDuplicateFromList() {
        val intListWithDuplicates = mutableListOf<Int>(1, 2, 3, 1, 1, 4, 7, 4)
        val intListWithOutDuplicates = mutableListOf<Int>(1, 2, 3, 4, 5, 6, 7)

        //Duplicate List Size should be reduced
        assert(vm.removeDuplicates(intListWithDuplicates).size < intListWithDuplicates.size)
        //Non Duplicate List Size should remain
        assert(vm.removeDuplicates(intListWithOutDuplicates).size == intListWithOutDuplicates.size)
    }

    @Test
    fun check_isNumber() {
        //True if Numbers
        assert(vm.isNumber("123"))
        //True if !Numbers
        assert(!vm.isNumber("abcd"))
        //!True if Symbols
        assert(!vm.isNumber("!@#$%"))
    }
}