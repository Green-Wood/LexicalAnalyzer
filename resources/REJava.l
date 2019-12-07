delim   [\t\r\n ]
ws      {delim}+
literal "(digit|letter)*"|'(digit|letter)'
number  {digits}(.{digits})?(e(\+|-)?{digits})?
digit   [0-9]
digits  {digit}+
key     abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|strictfp|short|static|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while
id      {letter_}({letter_}|{digit})*
letter_     [A-Za-z_]
operator       ~|!|-|\+\+|--|\+|-|\*|/|%|\?|\\|:|==|!=|>|<|>=|<=|=|\+=|-=|\*=|/=|%=|&=|^=|\|=|<<=|>>=|<<|>>|>>>|&|\||^|&&
punctuation     \(|\)|\{|\}|\[|\]|;|"|'|,|.