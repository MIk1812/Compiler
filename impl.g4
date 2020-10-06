grammar impl;

/* A small imperative language */

start   :  cs+=command* EOF ;

program : c=command                      # SingleCommand
	| '{' cs+=command* '}'           # MultipleCommands
	;
	
command : x=ID '=' e=expr ';'	         				# Assignment
	| x=ID '[' e1=expr ']' '=' e2=expr ';'				# ArraySet
	| 'output' e=expr ';'            				# Output
        | 'while' '('c=condition')' p=program 	 			# WhileLoop
	| 'if' '('c=condition')' 'then'? p=program 			# IfStatement
	| 'for' '(' x=ID '=' n1=expr '..' n2=expr ')' p=program 	# ForLoop
	;
	
expr	: c=FLOAT     	      		# Constant
	| e1=expr s=MULDEV e2=expr 	# MulDev
	| e1=expr s=ADDSUB e2=expr 	# AddSub
	| s=ADDSUB e=FLOAT		# Prefix
	| x=ID		      		# Variable
	| x=ID '[' e=expr ']'		# ArrayGet
	| '(' e=expr ')'      		# ParenthesisExpr
	;

condition : 	  e1=expr '!=' e2=expr 			# Unequal
	  	| e1=expr '==' e2=expr 			# Equal
		| e1=expr '<' e2=expr 			# LessThan
		| e1=expr '>' e2=expr 			# GreaterThan
		| e1=expr '<=' e2=expr 			# LessThanOrEqual
		| e1=expr '>=' e2=expr 			# GreaterThanOrEqual
		| '!' c=condition			# Not
		| c1=condition '&&' c2=condition 	# And
		| c1=condition '||' c2=condition 	# Or
		| '(' c=condition ')'			# ParenthesisCondition
	  	;  

MULDEV : ('*'|'/') ;
ADDSUB : ('+'|'-') ;

ID    : ALPHA (ALPHA|NUM)* ;
FLOAT : NUM+ ('.' NUM+)? ;

ALPHA : [a-zA-Z_ÆØÅæøå] ;
NUM   : [0-9] ;

WHITESPACE : [ \n\t\r]+ -> skip;
COMMENT    : '//'~[\n]*  -> skip;
COMMENT2   : '/*' (~[*] | '*'~[/]  )*   '*/'  -> skip;
