package com.team2052.frckrawler.database;

import android.util.Log;

import com.team2052.frckrawler.util.Tuple;

/**
 * **
 * Class: CompiledDataSorter
 *
 * @author Charles Hofer
 *         <p/>
 *         Description: Used to sort Tuples by their keys or CompileData by the keys provided.
 *         Uses an implementation of the Merge Sort algorithm.
 *         ***
 */

public class Sorter {

    public static void sortCompiledData(double[] keys) {//, CompiledData[] vals
        /*if (keys.length != vals.length)
            return;

        Tuple<CompiledData>[] tuples = new Tuple[keys.length];
        for (int i = 0; i < tuples.length; i++)
            tuples[i] = new Tuple<CompiledData>(keys[i], vals[i]);
        mergeSort(tuples);
        for (int i = 0; i < tuples.length; i++)
            vals[i] = tuples[i].getValue();*/
    }

    public static void mergeSort(Tuple[] arr) {
        //Base case
        if (arr.length == 1)
            return;

        //Split the array into two halves and sort each half
        Tuple[] arr1 = new Tuple[arr.length / 2];
        Tuple[] arr2 = new Tuple[arr.length - arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            Log.d("FRCKrawler", "arr1[" + i + "] = " + arr[i].getKey());
            arr1[i] = arr[i];
        }
        for (int i = 0; i < arr2.length; i++) {
            Log.d("FRCKrawler", "arr2[" + i + "] = " + arr[arr1.length + i].getKey());
            arr2[i] = arr[arr1.length + i];
        }
        mergeSort(arr1);
        mergeSort(arr2);

        //Merge the two new arrays
        int arrIdx = 0;
        int arr1Idx = 0;
        int arr2Idx = 0;

        while (arr1Idx < arr1.length && arr2Idx < arr2.length) {
            if (arr1[arr1Idx].getKey() >= arr2[arr2Idx].getKey()) {
                arr[arrIdx] = arr1[arr1Idx];
                arr1Idx++;
            } else {
                arr[arrIdx] = arr2[arr2Idx];
                arr2Idx++;
            }

            arrIdx++;
        }

        while (arr1Idx < arr1.length) {
            arr[arrIdx] = arr1[arr1Idx];
            arr1Idx++;
            arrIdx++;
        }

        while (arr2Idx < arr2.length) {
            arr[arrIdx] = arr2[arr2Idx];
            arr2Idx++;
            arrIdx++;
        }
    }
}
