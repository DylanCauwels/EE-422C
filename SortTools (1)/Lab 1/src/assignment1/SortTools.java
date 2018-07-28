//assignment1.SortTools.java
/*
 * EE422C Project 1 submission by
 * Dylan Cauwels
 * dmc3692
 * 15505
 * Spring 2017
 * Slip days used: 0
 */

package assignment1;
public class SortTools {
	public static boolean isSorted(int[] x, int n) {
		if(n == 1 || n == 0)
		    return true;
	    for(int i = 0; i < n; i++) {
            if (x[i] > x[i + 1])
                return false;
        }
        return true;
	}

	public static int find(int[] nums, int n, int v) {
        int low = 0;
        int high = n - 1;
        while(high >= low) {
            int mid = (high + low) / 2;
            if(nums[mid] == v)
                return mid;
            else if(nums[mid] < v)
                low = mid + 1;
            else if(nums[mid] > v)
                high = mid - 1;
        }
        return -1;
    }

    public static int[] insertGeneral(int[] nums, int n, int v) {
        if(n == 0) {
            int[] empty = new int[0];
            return empty;
        }
	    if(find(nums, n, v) != -1) {
            int[] generalInsert = new int[n];
            for(int i = 0; i < n; i++)
                generalInsert[i] = nums[i];
            return generalInsert;
        }
        else {
            int[] generalInsert = new int[n + 1];
            int i = 0;
            while(nums[i] < v && i < n) {
                generalInsert[i] = nums[i];
                i++;
            }
            generalInsert[i] = v;
            for(; i < n; i++) {
                generalInsert[i + 1] = nums[i];
            }
            return generalInsert;
        }
    }

    public static int insertInPlace(int[] nums, int n, int v) {
        if(find(nums, n, v) != -1)
            return n;
        else {
            int i = 0;
            while(nums[i] < v && i < n)
                i++;
            if(i > n) {
                nums[n] = v;
                return (n + 1);
            }
            for(int j = n; j > i; j--)
                nums[j] = nums[j - 1];
            nums[i] = v;
            return (n + 1);
        }

    }

    public static void insertSort(int[] nums, int n) {
        for(int i = 1; i < n; i++){
            int index = i;
            while(index >= 1 && nums[index] < nums[index - 1]) {
                int temp = nums[index];
                nums[index] = nums[index - 1];
                nums[index - 1] = temp;
                index--;
            }
        }
    }
}