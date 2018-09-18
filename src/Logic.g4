grammar Logic;

start : expr;
expr:
        '(' expr ')'
    |   'not' expr
    |   expr 'or' expr
    |   expr 'and' expr
    |   expr '->' expr
    |   Char
    ;
Char:
        [a-z]+[0-9]*
    |   [A-Z]+[0-9]*
    ;
WhiteSpace: [ \t\n]+ -> skip;