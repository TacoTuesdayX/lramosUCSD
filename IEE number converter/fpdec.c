/*
 * Filename: fdpec.c
 * Author: Logan R. Ramos
 * UserID: cs30s120bo
 * Date: 7/29/2020
 * Sources of help: I got some help from Matt's office hours
 * he helped me understand how to properly link my files.
 */

#include "pa3.h"
#include <stdio.h>
#include "pa3Strings.h"
#define EXIT_FAILURE 1
#define EXIT_SUCCESS 0
#define CORRECT_ARGS 2
/*
 * Function Name: main()
 * Function Prototype: int main( int argc, char * argv[]);
 * Description: This is the main driver of the entire program,
 * 		it basically first looks at the number of inputs and
 *		handles any errors associated. Then parses the string
 *		of numbers into an unsigned long. And from there
 *		extracts the mantissa, exponent, and sign bit into 
 *		a struct to be printed.
 * Parameters: int argc is used to count the number of arguments, and
 * 	       char * argv[] is used to access the inputted string.
 * Side Effects: One potential side effect is if the user does not put
 * 		 in a properly formatted 8bit hexadecimal, the result
 *		 is not an IEEE val. Amother is if an incorrect # of args
 *		 is passed, which results in exit_failure and usage print.
 * Return Value: This function returns either 1 or 0. The zero reflects
 * 		 EXIT_SUCCESS, and one reflects EXIT_FAILURE
 *
 */
int
main( int argc, char * argv[] ){
	//make sure correct number of args
	if(argc == CORRECT_ARGS){
		//parse into unsigned long
		unsigned long pNum = parseNum(argv);
		//create struct and instantiate parts
		ieeeParts_t valParts;				
		extractParts(pNum, &valParts);
		//print parts
		printf(SIGN_STR, valParts.sign);
		printf(EXP_STR, valParts.exp);
		printf(MANTISSA_STR, valParts.mantissa);
	}
	else{
		fprintf(stderr,"%s %s", INVALID_ARGS, SHORT_USAGE); 
		return EXIT_FAILURE;
	}
	return EXIT_SUCCESS;
}
