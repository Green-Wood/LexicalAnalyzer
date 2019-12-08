delim   [\t\r\n ]
ws      {delim}+
number  {digits}(.{digits})?(e(\+|-)?{digits})?
digit   [0-9]
digits  {digit}+
primitive_type      boolean|byte|char|double|float|int|long|short
control_stmt   break|case|continue|default|do|else|for|if|instanceof|return|switch|while
modifier       abstract|class|extends|final|implements|interface|native|new|static|strictfp|synchronized|transient|volatile
key_words     assert|catch|const|enum|finally|goto|import|package|private|protected|public|super|this|throw|throws|try|void
reserved_words    false|true|null|{key_words}
id      {letter_}({letter_}|{digit})*
letter_     [A-Za-z_]
arithmetic_op       ~|-|\+\+|--|\+|-|\*|/|%|=|\+=|-=|\*=|/=|%=|&=|^=|\|=|<<=|>>=|<<|>>|>>>|&|\||^
bool_op         \?|\\|:|==|!=|>|<|>=|<=|&&|!|\|\|
punctuation     \(|\)|\{|\}|\[|\]|;|"|'|,|.