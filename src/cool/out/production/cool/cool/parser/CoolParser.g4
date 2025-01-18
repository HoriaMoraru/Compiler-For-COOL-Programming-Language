parser grammar CoolParser;

options {
    tokenVocab = CoolLexer;
}

@header{
    package cool.parser;
}

/* The program in COOL is a list of classes */
program : class+ EOF;

class: CLASS main=(TYPE | SELF_TYPE) (CLASS_INHERITANCE inherited=(TYPE | SELF_TYPE))? LEFT_BRACE feature* RIGHT_BRACE SEMICOLON;

feature : attribute | method;

attribute : IDENTIFIER COLON (TYPE | SELF_TYPE) (ASSIGN expr)? SEMICOLON;
method : IDENTIFIER LEFT_PARAN (formals += formal (COMMA formals += formal)*)? RIGHT_PARAN COLON type=(TYPE | SELF_TYPE) LEFT_BRACE expr RIGHT_BRACE SEMICOLON;

formal : IDENTIFIER COLON type=(TYPE | SELF_TYPE);

definition : IDENTIFIER COLON type=(TYPE | SELF_TYPE) (ASSIGN expr)?;

case_branch : IDENTIFIER COLON type=(TYPE | SELF_TYPE) RIGHT_ARROW expr SEMICOLON;

expr
    : IDENTIFIER LEFT_PARAN (args += expr (COMMA args += expr)*)? RIGHT_PARAN                                               # implicit_call
    | caller=expr (AT type=(TYPE | SELF_TYPE))? DOT IDENTIFIER LEFT_PARAN (args += expr (COMMA args += expr)*)? RIGHT_PARAN # call
    | LET definitions += definition (COMMA definitions += definition)* IN expr                                              # let
    | IF condition=expr THEN thenBranch=expr ELSE elseBranch=expr IF_END                                                    # if
    | CASE expr OF (case_branch)+ CASE_END                                                                                  # case
    | WHILE condition=expr LOOP body=expr LOOP_END                                                                          # while
    | NEW type=(TYPE | SELF_TYPE)                                                                                           # new
    | ISVOID expr                                                                                                           # isvoid
    | LEFT_PARAN expr RIGHT_PARAN                                                                                           # paran_expression
    | COMPLEMENT expr                                                                                                       # complement
    | INT                                                                                                                   # int
    | STRING                                                                                                                # string
    | BOOL                                                                                                                  # bool
    | IDENTIFIER                                                                                                            # id
    | left=expr op=(MULTIPLICATION | DIVISION) right=expr                                                                   # arithmetic
    | left=expr op=(PLUS | MINUS) right=expr                                                                                # arithmetic  /* Plus and Minus operation have lower priority than multiplication and division */
    | left=expr op=(EQUAL | LESS_THAN | LESS_THAN_OR_EQUAL) right=expr                                                      # relational
    | IDENTIFIER ASSIGN expr                                                                                                # assignment
    | NOT expr                                                                                                              # negation
    | LEFT_BRACE (statements+=expr SEMICOLON)* final=expr SEMICOLON RIGHT_BRACE                                             # block_expr /* We need at least 1 expression at the end of the block */
    ;