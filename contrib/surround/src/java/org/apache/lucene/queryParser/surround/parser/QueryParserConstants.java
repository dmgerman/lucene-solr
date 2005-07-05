begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Generated By:JavaCC: Do not edit this line. QueryParserConstants.java */
end_comment

begin_package
DECL|package|org.apache.lucene.queryParser.surround.parser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|parser
package|;
end_package

begin_interface
DECL|interface|QueryParserConstants
specifier|public
interface|interface
name|QueryParserConstants
block|{
DECL|field|EOF
name|int
name|EOF
init|=
literal|0
decl_stmt|;
DECL|field|_NUM_CHAR
name|int
name|_NUM_CHAR
init|=
literal|1
decl_stmt|;
DECL|field|_TERM_CHAR
name|int
name|_TERM_CHAR
init|=
literal|2
decl_stmt|;
DECL|field|_WHITESPACE
name|int
name|_WHITESPACE
init|=
literal|3
decl_stmt|;
DECL|field|_STAR
name|int
name|_STAR
init|=
literal|4
decl_stmt|;
DECL|field|_ONE_CHAR
name|int
name|_ONE_CHAR
init|=
literal|5
decl_stmt|;
DECL|field|_DISTOP_NUM
name|int
name|_DISTOP_NUM
init|=
literal|6
decl_stmt|;
DECL|field|OR
name|int
name|OR
init|=
literal|8
decl_stmt|;
DECL|field|AND
name|int
name|AND
init|=
literal|9
decl_stmt|;
DECL|field|NOT
name|int
name|NOT
init|=
literal|10
decl_stmt|;
DECL|field|W
name|int
name|W
init|=
literal|11
decl_stmt|;
DECL|field|N
name|int
name|N
init|=
literal|12
decl_stmt|;
DECL|field|LPAREN
name|int
name|LPAREN
init|=
literal|13
decl_stmt|;
DECL|field|RPAREN
name|int
name|RPAREN
init|=
literal|14
decl_stmt|;
DECL|field|COMMA
name|int
name|COMMA
init|=
literal|15
decl_stmt|;
DECL|field|COLON
name|int
name|COLON
init|=
literal|16
decl_stmt|;
DECL|field|CARAT
name|int
name|CARAT
init|=
literal|17
decl_stmt|;
DECL|field|TRUNCQUOTED
name|int
name|TRUNCQUOTED
init|=
literal|18
decl_stmt|;
DECL|field|QUOTED
name|int
name|QUOTED
init|=
literal|19
decl_stmt|;
DECL|field|SUFFIXTERM
name|int
name|SUFFIXTERM
init|=
literal|20
decl_stmt|;
DECL|field|TRUNCTERM
name|int
name|TRUNCTERM
init|=
literal|21
decl_stmt|;
DECL|field|TERM
name|int
name|TERM
init|=
literal|22
decl_stmt|;
DECL|field|NUMBER
name|int
name|NUMBER
init|=
literal|23
decl_stmt|;
DECL|field|Boost
name|int
name|Boost
init|=
literal|0
decl_stmt|;
DECL|field|DEFAULT
name|int
name|DEFAULT
init|=
literal|1
decl_stmt|;
DECL|field|tokenImage
name|String
index|[]
name|tokenImage
init|=
block|{
literal|"<EOF>"
block|,
literal|"<_NUM_CHAR>"
block|,
literal|"<_TERM_CHAR>"
block|,
literal|"<_WHITESPACE>"
block|,
literal|"\"*\""
block|,
literal|"\"?\""
block|,
literal|"<_DISTOP_NUM>"
block|,
literal|"<token of kind 7>"
block|,
literal|"<OR>"
block|,
literal|"<AND>"
block|,
literal|"<NOT>"
block|,
literal|"<W>"
block|,
literal|"<N>"
block|,
literal|"\"(\""
block|,
literal|"\")\""
block|,
literal|"\",\""
block|,
literal|"\":\""
block|,
literal|"\"^\""
block|,
literal|"<TRUNCQUOTED>"
block|,
literal|"<QUOTED>"
block|,
literal|"<SUFFIXTERM>"
block|,
literal|"<TRUNCTERM>"
block|,
literal|"<TERM>"
block|,
literal|"<NUMBER>"
block|,   }
decl_stmt|;
block|}
end_interface

end_unit

