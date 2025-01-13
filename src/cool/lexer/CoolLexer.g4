lexer grammar CoolLexer;

tokens { ERROR }

@header{
    package cool.lexer;
}

@members{
    private static final int MAX_STRING_LEN = 1026; /* Take quotes into consideration */
    private static final String NULL_CHARACTER = "\0";

    private void raiseError(String msg) {
        setText(msg);
        setType(ERROR);
    }
}

/* FRAGMENTS */
fragment DIGIT : [0-9];
fragment NEW_LINE : '\r'? '\n';
fragment UPPER_CASE : [A-Z];
fragment LOWER_CASE : [a-z];
fragment LETTER : UPPER_CASE | LOWER_CASE;
fragment ESCAPED_NEWLINE : '\\' NEW_LINE;
fragment ESCAPED_CHAR : '\\' . ;
/* FRAGMENTS */

CLASS : 'class';
CLASS_INHERITANCE : 'inherits';
NEW : 'new';
ISVOID : 'isvoid';
SELF_TYPE : 'SELF_TYPE';

INT : DIGIT+;
BOOL : 'false' | 'true';

WHILE : 'while';
LOOP : 'loop';
LOOP_END : 'pool';

IF : 'if';
ELSE : 'else';
THEN : 'then';
IF_END: 'fi';

CASE : 'case';
OF : 'of';
CASE_END : 'esac';

LET : 'let';
IN : 'in';

NOT : 'not';
AT : '@';

PLUS : '+';
MINUS : '-';
MULTIPLICATION : '*';
DIVISION : '/';
EQUAL : '=';
LESS_THAN : '<';
LESS_THAN_OR_EQUAL : '<=';
ASSIGN : '<-';
RIGHT_ARROW : '=>';
COMPLEMENT : '~';
LEFT_PARAN : '(';
RIGHT_PARAN : ')';
LEFT_BRACE : '{';
RIGHT_BRACE : '}';
SEMICOLON : ';';
COLON : ':';
COMMA : ',';
DOT : '.';

TYPE : 'Int' | 'Bool' | 'String' | UPPER_CASE (LETTER | DIGIT | '_')*;
IDENTIFIER : 'self' | LOWER_CASE (LETTER | DIGIT | '_')*;

STRING
    : '"' ( ESCAPED_NEWLINE
          | ESCAPED_CHAR
          | ~["\r\n]
          )* '"'
      {
          final String text = getText();

          if (text.length() > MAX_STRING_LEN) {
              raiseError("String constant too long");
          }

          if (text.contains(NULL_CHARACTER)) {
              raiseError("String contains null character");
          }
      }
    | '"' ( ESCAPED_NEWLINE
          | ESCAPED_CHAR
          | ~["\r\n]
          )* EOF
      {
          raiseError("EOF in string constant");
      }
    | '"' ( ESCAPED_NEWLINE
          | ESCAPED_CHAR
          | ~["\r\n]
          )*
      {
          raiseError("Unterminated string constant");
      }
    ;

/* SKIPPABLES */
WHITESPACE
    :   [ \n\f\r\t]+ -> skip
    ;

SINGLE_LINE_COMMENT
    : '--' .*? (NEW_LINE | EOF { raiseError("EOF in comment"); }) -> skip
    ;

BLOCK_COMMENT
    : '(*' (BLOCK_COMMENT | .)*? ('*)' { skip(); } | EOF { raiseError("EOF in comment"); })
    ;

UNMATCHED_BLOCK_COMMENT : '*)' { raiseError("Unmatched *)"); };
/* SKIPPABLES */

INVALID : . { raiseError("Invalid character: " + getText()); };