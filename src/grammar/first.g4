grammar first;

prog:	stat* EOF;

stat: expr #expr_stat
    | IF_kw '(' cond=expr ')' then=block  ('else' else=block)? #if_stat
    | WHILE_kw '(' cond=expr ')' then=block #while_stat
    | '>>' expr #print_stat
    ;

block : stat #block_single
    | '{' block* '}' #block_real
    ;

expr:
        l=expr op=(MUL|DIV) r=expr #binOp
    |	l=expr op=(ADD|SUB) r=expr #binOp
    |   l=expr op=(EQ|NEQ|LT|GT) r=expr #logOp
    |   l=expr op=AND r=expr #logOp
    |   l=expr op=OR r=expr #logOp
    |   op=NOT r=expr #logOp
    |   DOUBLE #double_tok
    |	INT #int_tok
    |   BOOL #bool_tok
    |	'(' expr ')' #pars
    | <assoc=right> ID '=' expr # assign
    |   ID #read
    ;

IF_kw : 'if' ;

BOOL: 'true'|'false';

WHILE_kw: 'while';

DIV : '/' ;

MUL : '*' ;

SUB : '-' ;

ADD : '+' ;

EQ :  '==';

GT : '>';

LT : '<';

NEQ: '!=';

AND: '&&';

OR: '||';

NOT: '!';

//NEWLINE : [\r\n]+ -> skip;
NEWLINE : [\r\n]+ -> channel(HIDDEN);

//WS : [ \t]+ -> skip ;
WS : [ \t]+ -> channel(HIDDEN) ;

INT     : [0-9]+ ;

DOUBLE: [0-9].[0-9]+;


ID : [a-zA-Z_][a-zA-Z0-9_]* ;

COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
LINE_COMMENT : '//' ~'\n'* '\n' -> channel(HIDDEN) ;