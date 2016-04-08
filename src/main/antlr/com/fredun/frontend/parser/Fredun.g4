grammar Fredun;

@header {
package com.fredun.frontend.parser;
}

start: (NEWLINE | defs)* ;

defs: (letDef | structDef | tupleDef) NEWLINE+ ;
letDef: 'let' varName '=' expr ;

structDef: 'struct' type NEWLINE? struct ;
tupleDef: 'type' type '=' tuple ;
struct: '{' NEWLINE* argList /*(COMMA_AND_NL spread)?*/ NEWLINE? '}' ;
tuple: '(' typeList ')' ;

//spread: '...' varName ;

typeList: type (COMMA_AND_NL type)* ;

number: UNSIGNED_INT | UNSIGNED_HEX | UNSIGNED_FLOAT;

varName: LOWERCASE_ID;

expr: block | '(' expr ')'
    | expr '.' LOWERCASE_ID
    | <assoc=right> expr POW expr          // powExpr
    | op=(MINUS | PLUS) expr               // unaryExpr
    | NOT expr                             // notExpr
    | expr op=(MULT | DIV | MOD) expr      // multiplicationExpr
    | expr op=(PLUS | MINUS) expr          // additiveExpr
    | expr op=(LTEQ | GTEQ | LT | GT) expr // relationalExpr
    | expr op=(EQ | NEQ) expr              // equalityExpr
    | expr AND expr                        // andExpr
    | expr OR expr                         // orExpr
    | '(' argList ')' (':' type)? '=>' expr
    | RETURN expr                          // return
    | constant=(CHAR | QUOTED_STRING) | number | LOWERCASE_ID ;

block: '{' blockStatement* '}' NEWLINE+ ;

blockStatement: (defs | (expr NEWLINE+)) ;

argList: arg (COMMA_AND_NL arg)* ;
arg: varName ':' type ;
type: UPPERCASE_ID ; /*typeKind? ;
typeKind: '[' type ']' ; */

ifSingle: 'if' expr 'then' expr 'else' expr ;
ifMulti: 'if' expr block ('else' block) ;
// Lexing

COMMA_AND_NL: COMMA NEWLINE* ;

COMMA: ',' ;
POW: '^';
MINUS: '-';
NOT: '!';
MULT: '*';
DIV: '/';
MOD: '%';
PLUS: '+';
LTEQ: '<=';
GTEQ: '>=';
LT: '<';
GT: '>';
EQ: '==';
NEQ: '!=';
AND: '&';
OR: '|';

RETURN: 'return' ;

fragment ESCAPED_QUOTE: '\\"';
fragment ESCAPED_SINGLE_QUOTE: '\\\'' ;
fragment HEX: [0-9a-zA-Z] ;
fragment UNICODE: '\\u' HEX HEX? HEX? HEX? HEX? ;

QUOTED_STRING: '"' ( ESCAPED_QUOTE | UNICODE | ~('\n'|'\r') )*? '"' ;
CHAR: '\'' (ESCAPED_SINGLE_QUOTE | UNICODE | ~('\n'|'\r')) '\'' ;

UNSIGNED_INT: [0-9]+ SUFFIX_INT? ;
UNSIGNED_HEX: '0x' HEX+ SUFFIX_INT? ;

UNSIGNED_FLOAT: (([0-9]+ '.' [0-9]* Exponent?) | ('.' [0-9]+ Exponent?) | ([0-9]+ Exponent)) SUFFIX_FLOAT? ;

SUFFIX_INT: [su] ('8'|'16'|'32'|'64') ;
SUFFIX_FLOAT: 'f' ('32'|'64') ;

fragment
Exponent : ('e'|'E') (PLUS|MINUS)? ('0'..'9')+ ;

fragment XID_Start : [_a-zA-Z] ;
fragment XID_Continue: [0-9_a-zA-Z] ;

LOWERCASE_ID: [_a-z] XID_Continue* ;
UPPERCASE_ID: [_A-Z] XID_Continue* ;
//ID : XID_Start XID_Continue* ;
DOC_COMMENT: '/**' .*? '*/' -> channel(HIDDEN) ;
BLOCK_COMMENT: '/*' .*? '*/' -> channel(HIDDEN) ;
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN) ;
NEWLINE: ('\r\n'|'\n'|'\r') ;
WS : [ \t]+ -> channel(HIDDEN) ; // skip spaces, tabs, newlines