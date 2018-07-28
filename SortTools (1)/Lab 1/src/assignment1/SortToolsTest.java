package assignment1;

import org.junit.Test;

import static org.junit.Assert.*;


public class SortToolsTest {
	//General Function Tests
	@Test(timeout = 2000)
	public void testFindFoundFull(){
		int[] x = new int[]{-2, -1, 0, 1, 2, 3};
		assertEquals(3, SortTools.find(x, 6, 1));
	}
	
	@Test(timeout = 2000)
	public void testInsertGeneralPartialEnd(){
		int[] x = new int[]{10, 20, 30, 40, 50};
		int[] expected = new int[]{10, 20, 30, 35};
		assertArrayEquals(expected, SortTools.insertGeneral(x, 3, 35));
	}

	@Test(timeout = 2000)
	public void testIsSorted() {
	    int[] x = new int[]{1, 2, 3, 4, 1, 8};
	    assertFalse(SortTools.isSorted(x, 6));
    }

    @Test(timeout = 2000)
    public void testInsertInPlace1() {
	    int[] x = new int[]{0, 1, 2, 3, 4, 5, 6};
	    int a = SortTools.insertInPlace(x, 6, 7);
	    assertTrue(a == 7);
    }

    @Test(timeout = 2000)
    public void testInsertInPlace2() {
	    int[] x = new int[]{1, 2, 3, 4, 5, 7, 8, 9, 10};
	    int[] expected = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
	    int a = SortTools.insertInPlace(x, 8, 6);
	    assertArrayEquals(x, expected);
    }

    @Test(timeout = 2000)
    public void testInsertSort() {
	    int[] x = new int[]{3, 2, 1, 6, 4 , 9, 11, 15};
	    int[] expected = new int[]{1, 2, 3, 4, 6, 9, 11, 15};
        SortTools.insertSort(x, 8);
	    assertArrayEquals(x, expected);
    }
    //Advanced Corner-Case Tests
    @Test(timeout = 2000) //Tests cases of n = 1 and n = 0
    public void isSortedTest() {
	    int[] x = new int[]{0};
	    assertTrue(SortTools.isSorted(x, 1));
	    assertTrue(SortTools.isSorted(x, 0));
    }

    @Test(timeout = 2000)
    public void findTest() { //Tests value existing in array but out of scope of n along with general searches
	    int[] x = new int[]{0, 1};
	    assertTrue(SortTools.find(x, 1, 1) == -1);
	    int[] y = new int[]{0, 2, 5, 18, 23, 25, 31, 42, 43, 49, 55, 67, 87, 91};
	    assertTrue(SortTools.find(y, 14, 18) == 3);
	    assertTrue(SortTools.find(y, 14, 67) == 11);
    }

    @Test(timeout = 2000)
    public void insertGeneralTest() { //Tests value existing one index over from end
        int[] x = new int[]{0, 1, 2};
        int[] expected = new int[]{0, 1, 2};
        x = SortTools.insertGeneral(x, 2,2);
        assertArrayEquals(x, expected);
        int[] y = new int[]{0, 1, 4, 5, 6, 7};  //Tests value requiring it being sorted into the array
        int[] expected2 = new int[]{0, 1, 3, 4, 5, 6, 7};
        y = SortTools.insertGeneral(y, 6, 3);
        assertArrayEquals(y, expected2);
    }

    @Test(timeout = 2000)
    public void insertSortTest() {
	    int[] x = new int[]{4, 54, 34, 23, 21, 12, 1, 87, 15, 16, 22};
	    int[] expected = new int[]{4, 21, 23, 34, 54, 12, 1, 87, 15, 16, 22};
	    SortTools.insertSort(x, 5);
	    assertArrayEquals(x, expected);
    }
}

