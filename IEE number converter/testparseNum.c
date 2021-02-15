/*
 * Filename: testparseNum.c
 * Author: Logan R. Ramos
 * UserID: cs30s120bo
 * Date: 7/30/2020
 * Sources of help: Used pa1 as a reference for TESTER syntax
 */

#include "test.h"
#include "pa3.h"
/*
 * Function Name: testparseNum()
 * Function Prototype: void testparseNum()
 * Description: This is a tester function for the file parseNum.s, it tests
 * 		by setting argv[1] to various 8bit hex strings, and determines
 * 		if the return matches the actual numerical representation.
 * Parameters: None
 * Side Effects: None
 * Error Conditions: None
 * Return Value: None, void
 */
void testparseNum(){	
	char * argv[2];
	argv[0] = "parseNum";			//set argv[0] to mimic filename
	
	argv[1] = "0x41260000";			//test normal value
	TEST( parseNum(argv) == 0x41260000 );   //check if equal numerical val
	
	argv[1] = "0x00000000";			//test zero
	TEST( parseNum(argv) == 0x00000000 ); 	//check if equal numerical val
	
	argv[1] = "0xbf800000";			//test with varying hex chars
	TEST( parseNum(argv) == 0xbf800000 );   //check if equal numerical val

	argv[1] = "0xFFFFFFFF";			//test max possible 8bit val
	TEST( parseNum(argv) == 0xFFFFFFFF );
}		

/* 
 * Function Name: main()
 * Function Prototype: int main()
 * Description: This main function initiates the tester by calling to
 * 		testparseNum(). States when test starts and ends.
 * Parameters: None
 * Side Effects: None
 * Error Conditions: None
 * Return Value: 0, EXIT_SUCCESS
 */
int main(){
	fprintf(stderr, "testing parseNum...\n\n");
	testparseNum();
	fprintf(stderr, "\nDone running tests.\n");
	return 0;
}
