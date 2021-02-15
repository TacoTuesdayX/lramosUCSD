/*
 * Filename: testextractParts.c
 * Author: Logan R. Ramos
 * UserID: cs30s120bo
 * Date: 7/30/2020
 * Sources of help: used pa1 as reference for tester syntax.
 */

#include "test.h"
#include "pa3.h"
/*
 * Function Name: testextractParts()
 * Function Prototype: void testextractParts()
 * Description: this is a tester function for the file extractParts.s
 * 		it tests the file by calling the extractParts() function
 * 		with various 8bit hexadecimals, and compares the struct
 * 		part values that exractParts() updates to the actual values
 *		obtained from a published IEEE converter. Success would be
 *		equivelancy.
 * Parameters:  None
 * Side Effects: None
 * Error Conditions: None
 * Return Value: void, none
 */
void testextractParts(){
	ieeeParts_t myNum;	
	unsigned long testVal;
	
	testVal = 0x41260000;		//testing normal positive val
	extractParts(testVal, &myNum);	//call and check if parts are correct
	TEST(myNum.sign == 0);
	TEST(myNum.exp == 3);		
	TEST(myNum.mantissa == 10878976); 
	
	testVal = 0xbf800000;		//testing normal negative val
	extractParts(testVal, &myNum);	//call and check if parts are correct
	TEST(myNum.sign == 1); 
	TEST(myNum.exp == 0);
	TEST(myNum.mantissa == 0x800000);
        
	testVal = 0xFAF8FA00;		//testing super large negative val
	extractParts(testVal, &myNum);	//call and check if parts are correct
	TEST(myNum.sign == 1);
	TEST(myNum.exp == 118);
	TEST(myNum.mantissa == 0xf8fa00);
	
	testVal = 0x01000020;		//testing super small val
	extractParts(testVal, &myNum);  //call and check if parts are correct
	TEST(myNum.sign == 0);
	TEST(myNum.exp == -125);
	TEST(myNum.mantissa == 8388640);
}
/*
 * Function Name: main()
 * Function Prototype: int main()
 * Description: this is the main function that initiates the test when the
 * 		program is called.
 * Parameters: None
 * Side Effects: None
 * Error Conditions: None
 * Return Value: EXIT_SUCCESS, 0
 */
int main(){
	fprintf(stderr, "testing extractParts...\n\n");
	testextractParts();
	fprintf(stderr, "\nDone running tests.\n");
	return 0;
}
