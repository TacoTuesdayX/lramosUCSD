/*
 * Filename: extractParts.s
 * Author: Logan R. Ramos
 * UserID: cs30s120bo
 * Date: 7/26/2020
 * Sources of help: I visited Matt's office hours for help on the struct
 *		    offsets, more in particular, accessing the adr of the
 *		    mantissa. I also used the class slides to remind myself
 *		    on storing values at memory addresses. 
 */

@assembly hardware
	.arch	armv6		@armv6 instruction set
	.cpu	cortex-a53	@pi-cluster server cpu
	.syntax	unified		@modern syntax

@External functions called, #defines, and magic numbers

@read only data segment (Rodata)
	.equ	FP_OFFSET, 16	     	@fp offset is in simple frame, temp
	.equ	NULL, 	0x0	     	@'\0'
	.equ	BASE, 16	     	@base to convert to
	.equ	SP_OFFSET, 10		@howmuch the struct stack offset is
	.equ	LEADING_ONE, 8388608 	@creates a leading one at bit 24
	.equ	EXP_CONST, 127		@used to calculate exponent bit.
	.equ	EXP_SHIFT, 24		@used to isolate/remove exp bits
	.equ	SIGN_SHIFT, 31		@used to isolate/remove sign bit
	.equ	MANT_SHIFT, 9		@used to isolate/remove mantissa
	.equ	MANT_OFFSET, 4		@the offset of the mantissa
	.section .rodata

@Text segment
	.text				@start of text seg
	.global	extractParts		@make parseNum global for linking
	.type extractParts, %function	@define parseNum as a func

/*
 * Function Name: extractParts() 
 * Function Prototype: void extractParts(unsigned long ieeeBin, ieeeParts_t *
 *			fill);
 * Description: This function will be used to extract the individual parts of
 * 		a IEE 754 number (mantissa, exp, sign bit) by populating the
 *		members of a struct passed in as a parameter. It uses bit
 *		shifting and registers to accomplish this.
 * Parameters:  extractParts has the parameter 'unsigned long ieeeBin' which is
 *	    	the IEE number to extract parts from. We will use the data from
 *		this parameter to populate our second parameter, a struct,
 *		'ieeeParts_t * fill'
 * Side Effects: none
 * Error Conditions: none
 * Return Value: This function does not return anything, but it psuedo-returns
 *		 by changing the value of the struct ieeeParts_t fill
 * Registers used:
 *	r0 - arg 1 -- the iee unsigned long to part out
 *	r1 - arg 2 -- the address of the struct to store the parted vals
 *	r4 - local var -- temp holds the extracted sign bit to store
 *	r5 - local var -- temp holds extracted exp bit to be stored
 *	r6 - local var -- temp holds mantissa to be stored 
 */

extractParts:
	push	{r4, r5, r6, fp, lr}
	add	fp, sp,	FP_OFFSET
	lsr	r4, r0, SIGN_SHIFT	@last bit shifted to right most bit,
	str	r4, [r1]		@store sign bit in r4, holding adr
	lsl	r5, r0, 1		@remove sign bit
	lsr	r5, r5, EXP_SHIFT	@shift over 24 so exp bits are lsb
	sub	r5, r5, EXP_CONST	@subtract from 127 to get exp val
	str	r5, [r1,1]		@store exp bits in adr in r5
	lsl	r6, r0, MANT_SHIFT	@remove sign bit and exp bits
	lsr	r6, r6, MANT_SHIFT	@shift back to lsb
	add	r6, r6, LEADING_ONE	@flip 24th bit to 1
	str	r6, [r1,MANT_OFFSET]	@store mantissa at adr stored in r6
	sub	sp, fp, FP_OFFSET	
	pop	{r4, r5, r6, fp, lr}
	bx lr
.end

