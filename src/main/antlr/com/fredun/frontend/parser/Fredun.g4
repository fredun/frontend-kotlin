grammar Fredun;

@header {
package com.fredun.frontend.parser;
}

//start: (NEWLINE | defs)* ;
// FIXME: Remove this when backend can parse defs
start: (NEWLINE | expr)* ;

defs: (letDef | structDef | tupleDef) NEWLINE+ ;
letDef: LET varName '=' expr ;

structDef: STRUCT type NEWLINE? struct ;
tupleDef: TYPE type '=' tuple ;
struct: '{' NEWLINE* argDefList /*(COMMA_AND_NL spread)?*/ NEWLINE? '}' ;
tuple: '(' typeList ')' ;

//spread: '...' varName ;

typeList: type (COMMA_AND_NL type)* ;

number:
    UNSIGNED_INT        # intNumber
    | UNSIGNED_HEX      # hexNumber
    | UNSIGNED_FLOAT    # floatNumber
    ;

varName: LOWERCASE_ID;

expr: block                                 # blockExpr
    | expr '(' (expr (COMMA expr)*)? ')'    # funcApplicationExpr
    | '(' expr ')'                          # groupExpr
    | expr DOT LOWERCASE_ID                 # dotExpr
    | <assoc=right> expr POW expr           # powExpr
    | op=(MINUS | PLUS) expr                # unaryExpr
    | NOT expr                              # notExpr
    | expr op=(MULT | DIV | MOD) expr       # multiplicationExpr
    | expr op=(PLUS | MINUS) expr           # additiveExpr
    | expr op=(LTEQ | GTEQ | LT | GT) expr  # relationalExpr
    | expr op=(EQ | NEQ) expr               # equalityExpr
    | expr AND expr                         # andExpr
    | expr OR expr                          # orExpr
    | '(' argDefList ')' (':' type)? '=>' expr # funcAbstractionExpr
    | RETURN expr                           # returnExpr
    | number                                # numberExpr
    | CHAR                                  # charExpr
    | QUOTED_STRING                         # stringExpr
    | LOWERCASE_ID                          # idExpr
    ;

block: '{' blockStatement* '}' NEWLINE+ ;

blockStatement: (defs | (expr NEWLINE+)) ;

argDefList: argDef (COMMA_AND_NL argDef)* ;
argDef: varName ':' type ;
type: UPPERCASE_ID ; /*typeKind? ;
typeKind: '[' type ']' ; */

ifSingle: IF expr THEN expr ELSE expr ;
ifMulti: IF expr block (ELSE block) ;

// Lexing

COMMA: ',' ;
DOT: '.' ;

COMMA_AND_NL: COMMA NEWLINE* ;
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
AND: '&&';
OR: '||';

ASSIGN: '=' ;

L_CURLY: '{' ;
R_CURLY: '}' ;
L_PAREN: '(' ;
R_PAREN: ')' ;
COLON: ':' ;

FAT_ARROW: '=>' ;

RETURN: 'return' ;
LET: 'let';
STRUCT: 'struct';
TYPE: 'type';
IF: 'if';
THEN: 'then';
ELSE: 'else';

fragment ESCAPED_QUOTE: '\\"';
fragment ESCAPED_SINGLE_QUOTE: '\\\'' ;
fragment HEX: [0-9a-zA-Z] ;
fragment UNICODE: '\\u' HEX HEX? HEX? HEX? HEX? ;

QUOTED_STRING: '"' ( ESCAPED_QUOTE | UNICODE | ~('\n'|'\r') )*? '"' ;
CHAR: '\'' (ESCAPED_SINGLE_QUOTE | UNICODE | ~('\n'|'\r')) '\'' ;

UNSIGNED_INT: [0-9]+ SUFFIX_INT? ;
UNSIGNED_HEX: '0x' HEX+ SUFFIX_INT? ;

UNSIGNED_FLOAT: (([0-9]+ '.' [0-9]* Exponent?) | ('.' [0-9]+ Exponent?) | ([0-9]+ Exponent)) SUFFIX_FLOAT? ;

SUFFIX_INT: ([iu] ('8'|'16'|'32'|'64')) | SUFFIX_FLOAT ;
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

/** "catch all" rule for any char not matched in a token rule of your
 *  grammar. Lexers in Intellij must return all tokens good and bad.
 *  There must be a token to cover all characters, which makes sense for
 *  an IDE. The parser however should not see these bad tokens because
 *  it just confuses the issue. Hence, the hidden channel.
 */
ERRCHAR
	:	.	-> channel(HIDDEN)
	;