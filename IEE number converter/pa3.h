/*
 * File: pa3.h
 * TODO Complete Header
 *
 *
 * Description: Header file that contains function prototypes and constants
 *
 *
 * XXX Only add constants to this file at the bottom.
 * DO NOT EDIT FUNCTION PROTOTYPES OR PREDEFINED CONSTANTS
 *
 */

#ifndef PA3_H		/* Macro Guard */
#define PA3_H
#include <stdio.h>

typedef struct fpbits{
	unsigned char sign;
	signed char exp;
	unsigned long mantissa;
}ieeeParts_t;


/* Assembly Function Prototypes */
unsigned long parseNum(char * argv[]);
void extractParts(unsigned long ieeeBin, ieeeParts_t * fill);

#endif
