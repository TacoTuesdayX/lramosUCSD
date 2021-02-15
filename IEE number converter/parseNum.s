/*
 * Filename: parseNum.s
 * Author: Logan R. Ramos
 * UserID: cs30s120bo
 * Date: 7/26/2020
 * Sources of help: I used class slides to help understand offsets
 *		    when accessing array elements in ARM. And I briefly
 *		    looked over PA2 for references on syntax.
 */

@assembly hardware
	.arch	armv6		@armv6 instruction set
	.cpu	cortex-a53	@pi-cluster server cpu
	.syntax	unified		@modern syntax

@External functions called, #defines, and magic numbers
	.extern	strtoul

@read only data segment (Rodata)
	.equ	FP_OFFSET, 12	@fp offset is in simple frame, temp
	.equ	NULL, 0x0	@'\0'
	.equ	BASE, 16	@base being converted
	.equ	STR_OFFSET, 4	@offset for argv[] elements
	.section .rodata

@Text segment
	.text				@start of text seg
	.type parseNum, %function	@define parseNum as a func
	.global	parseNum		@make parseNum global for linking

/*
 * Function Name: parseNum() 
 * Function Prototype: unsigned long parseNum( char * argv[] );
 * Description: This function will be used to parse a single-precision
 *		IEE-754 number in hexadecimal as a string to an 
 *		unsigned long.
 * Parameters: parseNum's prototype uses the parameter 'char * argv[]'
 * 	       it's used to access the input argument (hex IEE)
 * Side Effects: none
 * Error Conditions: none
 * Return Value: The return value of this function is the user's inputed
 *		 IEE hex represented as an unsigned long. It is used to
 *		 map out and split the number into it's parts: exp, sign
 *		 bit, and mantissa.
 * Registers Used:
 *	r0 - arg1 - used to hold the inputed hex string, and as a parameter
 *	r1 - local var - used as a parameter in stroul
 *	r2 - local var - used as a parameter in stroul
 */

parseNum:
	push	{r4, r5, fp, lr}	
	add	fp, sp,	FP_OFFSET	
	ldr	r0, [r0, STR_OFFSET]	@move argv[1] into r0
	ldr	r1,=NULL		@put '\0' in r1
	ldr	r2,=BASE		@put '2' into r2
	bl	strtoul			@strtoul(r0, r1, r2)
	sub	sp, fp, FP_OFFSET	
	pop	{r4, r5, fp, lr}
	bx lr			
.end

