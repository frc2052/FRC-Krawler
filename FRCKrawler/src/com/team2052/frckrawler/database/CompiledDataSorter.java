package com.team2052.frckrawler.database;

/*****
 * Class: CompiledDataSorter
 * 
 * @author Charles Hofer
 *
 * Description: this class is used to sort a list of objects, based on 
 * their keys.
 *****/

public class CompiledDataSorter {
	double[] values;
	private Object[] items;
	private int number;

	public void sort(double[] _keys, Object[] _items) {
		// Check for empty or null array
		if (values == null || values.length == 0 || 
				_keys.length != _items.length){
			return;
		}

		values = _keys;
		items = _items;
		number = values.length;
		quicksort(0, number - 1);
	}

	private void quicksort(int low, int high) {
		int i = low, j = high;
		// Get the pivot element from the middle of the list
		double pivot = values[low + (high-low)/2];

		// Divide into two lists
		while (i <= j) {
			// If the current value from the left list is smaller then the pivot
			// element then get the next element from the left list
			while (values[i] < pivot) {
				i++;
			}
			// If the current value from the right list is larger then the pivot
			// element then get the next element from the right list
			while (values[j] > pivot) {
				j--;
			}

			// If we have found a values in the left list which is larger then
			// the pivot element and if we have found a value in the right list
			// which is smaller then the pivot element then we exchange the
			// values.
			// As we are done we can increase i and j
			if (i <= j) {
				exchange(i, j);
				i++;
				j--;
			}
		}
		// Recursion
		if (low < j)
			quicksort(low, j);
		if (i < high)
			quicksort(i, high);
	}

	private void exchange(int i, int j) {
		double tempKey = values[i];
		Object tempOb = items[i];
		values[i] = values[j];
		items[i] = items[j];
		values[j] = tempKey;
		items[j] = tempOb;
	}
}
