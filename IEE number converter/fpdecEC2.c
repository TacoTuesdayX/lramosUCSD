/*
 * Filename: fpdecEC2.c
 * Author: Logan R. Ramos
 * UserID: cs30s120bo
 * Date: 7/29/2020
 * Sources of Help: none
 */


#include "pa3.h"
#include <stdio.h>
#include <math.h>
#include "pa3Strings.h"
#define EXIT_FAILURE 1
#define EXIT_SUCCESS 0
#define SHIFT_CONST 23
#define CURR_BASE 2
#define VALID_ARGS 2
/*
 * Function Name: main()
 * Function Prototype: int main( int argc, char * argv[] );
 * Description: This function behaves similarly to fdpec.c's
 * 		main function, but includes a mechanism for
 * 		converting into floating point. It is a IEE
 * 		to Decimal converter.
 * Parameters:  int argc determines number of args, argv[]
 * 		is how we access the input argument.
 * Side Effects:If the user does not format the 8bit hex string
 * 		upon execution, the result will not be IEE.
 * Error Conditions: if the user does not give a hexadecimal string
 * 		     the program will throw an error and display
 * 		     program usage.
 * Return Value: 0 or 1, EXIT_SUCCESS or EXIT_FAILURE. signifying
 * 		 end of program.
 *
 */
int
main( int argc, char * argv[] ){
	if(argc == VALID_ARGS){
		unsigned long pNum = parseNum(argv);
		ieeeParts_t valParts;				
		extractParts(pNum, &valParts);

		//separate whole and fractional bits into 2 sep vars
		int whole_part = valParts.mantissa>>(SHIFT_CONST-valParts.exp);
		int fract_part = valParts.mantissa - 
			(whole_part<<(SHIFT_CONST-valParts.exp));	
		
		//convert fractional part to a double/float
		//and calculate it's value as a float
		double frac_part = (double)fract_part;	
		frac_part = frac_part/pow(CURR_BASE,(SHIFT_CONST-valParts.exp));
		
		//sum the whole part and fractional part
		float total = (whole_part+frac_part);

		//make negative if sign bit is marked on
		if(valParts.sign == 1){
			total = total * -1;
		}

		//print results
		printf(SIGN_STR, valParts.sign);
		printf(EXP_STR, valParts.exp);
		printf(MANTISSA_STR, valParts.mantissa);
		printf(DECIMAL_STR, total);
	}
	else{
		fprintf(stderr,"%s %s", INVALID_ARGS, SHORT_USAGE); 
		return EXIT_FAILURE;
	}
	return EXIT_SUCCESS;
}
