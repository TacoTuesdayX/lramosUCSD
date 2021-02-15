/*
 * Filename: fpdecEC.s
 * Author: Logan R. Ramos
 * UserID: cs30s120bo
 * Date: 7/26/2020
 * Sources of help: Needed some clarification on how to link and use
 *		    assembly files so I got some help from piazza on
 *		    ".global" and ".extern".
 */

@assembly hardware
	.arch	armv6		@armv6 instruction set
	.cpu	cortex-a53	@pi-cluster server cpu
	.syntax	unified		@modern syntax

@External functions called, #defines, and magic numbers
	.extern	printf
	.extern fprintf
	.extern parseNum
	.extern extractParts
	.extern	stderr


@read only data segment (Rodata)
	.equ	FP_OFFSET, 12	@fp offset is in simple frame, temp
	.equ	NULL, 0x0	@'\0'
	.equ	BASE, 16	@base being converted
	.equ	STCT_SIZE, 8	@1b + 1b + 4b + 2b(padding)
	.equ	STCT_LOC, -18   @location of struct ref to fp
	.equ	VALID_ARGS, 2	@valid num of args
	.equ	MNT_OFFSET, 4	@mantissa offset ref to struct offset
	.section .rodata
	err:.asciz	"%s %s"
	INVALID_ARGS:.asciz "Incorrect number of arguments.\n"
	SHORT_USAGE:.asciz "\n Usage ./fpdec {hexString} \n\n"
	SIGN_STR:.asciz "Sign Bit: %u\n"
	EXP_STR:.asciz "Unbiased Exponent: %hhd\n"
	MANTISSA_STR:.asciz "Mantissa: %lx\n"
@Text segment
	.text				@start of text seg
	.type main, %function		@define main as a func
	.global	main			@make main global for linking

/*
 * Function Name: main() 
 * Function Prototype: int main(int argc, char * argv[] );
 * Description: This function will be used as the main driver of the program
 *		it first takes the user input, a string 8bit hex, and parses
 *		it into an unsigned long. Then the unsigned long is extracted
 *		into it's various parts: exp mantissa sign bit. and All parts
 *		are printed out, and exit code 0 or 1 is printed.
 * Parameters:  Main's function uses parameter 'int argc' to determine whether
 *	        the user has inputed the correct number of args. 'char* argv[]'
 *	        is used to access the inputed hex string.
 * Side Effects:if the user does not put in a correctly formatted hex string,
 * 		the program will still run, but the program  will not print a
 *		valid IEEE 754 num part set.
 * Error Conditions: One condition is if the user does not input a valid num
 *	             of arguments, returns exit failure and prints proper usg
 * Registers Used:
 *	r0 - arg1 and local var - used to acess argc and temp store parts
 *	r1 - arg2 - used to access inputed string hex
 * 	r2 - parameter - used as a param in fprintf
 *	r3 - parameter - used as a paran in fprintf
 *	r4 - local var - used to load IEEpart adresses
 *	
 * Stack variables:
 *	ieeeParts_t - [fp - 18] -- holds the 3 parts of our IEEE num.
 */

main:
	push	{r4, r5, fp, lr}	
	add	fp, sp,	FP_OFFSET
	sub	sp, sp, STCT_SIZE
	cmp	r0, VALID_ARGS		@if(argc == 2)
	blne	invalid 		@then go to invalid
	mov	r0, r1			@load argv[] into r0
	bl 	parseNum		@call parseNum(r0)
	add	r1, fp, STCT_LOC	@load adr of struct into r1
	bl	extractParts		@call extractParts(r0, r1)
	mov	r4, r1			@move struct into r4 with updated vals	
	ldrb	r1, [r4]		@move sign bit into r1
	ldr	r0, =SIGN_STR		@move SIGN_STR into r0
	bl	printf			@call printf(r0, r1) -- sign bit
	ldrb	r1, [r4,1]		@move exp bit into r1
	ldr	r0, =EXP_STR		@move EXP_STR into r0
	bl	printf			@call printf(r0, r1) -- exp bit
	ldr	r1, [r4,MNT_OFFSET]	@move mantissa into r1
	ldr	r0, =MANTISSA_STR	@move MANTISSA_STR into r0
	bl	printf			@call printf(r0, r1) -- mantissa ULon
	mov	r0, 0			@exit_success is now return
	bl	done
invalid:
	ldr	r0,=stderr		@load stderr into param1
	ldr	r1,=err			@load err into param2
	ldr	r2,=INVALID_ARGS	@load INVALID_ARGS into param3
	ldr	r3,=SHORT_USAGE		@load SHORT_USAGE into param4
	bl	fprintf			@call fprintf(r0,r1,r2,r3) error+usg
	mov	r0,1			@exit_failure is now return
	bl	done
done:
	sub	sp, fp, FP_OFFSET	
	pop	{r4, r5, fp, lr}
	bx lr
.end

