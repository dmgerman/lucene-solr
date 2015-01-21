begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Generated By:JavaCC: Do not edit this line. CharStream.java Version 5.0 */
end_comment

begin_comment
comment|/* JavaCCOptions:STATIC=false,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
end_comment

begin_package
DECL|package|org.apache.solr.parser
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|parser
package|;
end_package

begin_comment
comment|/**  * This interface describes a character stream that maintains line and  * column number positions of the characters.  It also has the capability  * to backup the stream to some extent.  An implementation of this  * interface is used in the TokenManager implementation generated by  * JavaCCParser.  *  * All the methods except backup can be implemented in any fashion. backup  * needs to be implemented correctly for the correct operation of the lexer.  * Rest of the methods are all used to get information like line number,  * column number and the String that constitutes a token and are not used  * by the lexer. Hence their implementation won't affect the generated lexer's  * operation.  */
end_comment

begin_interface
specifier|public
DECL|interface|CharStream
interface|interface
name|CharStream
block|{
comment|/**    * Returns the next character from the selected input.  The method    * of selecting the input is the responsibility of the class    * implementing this interface.  Can throw any java.io.IOException.    */
DECL|method|readChar
name|char
name|readChar
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
function_decl|;
comment|/**    * Returns the column number of the last character for current token (being    * matched after the last call to BeginTOken).    */
DECL|method|getEndColumn
name|int
name|getEndColumn
parameter_list|()
function_decl|;
comment|/**    * Returns the line number of the last character for current token (being    * matched after the last call to BeginTOken).    */
DECL|method|getEndLine
name|int
name|getEndLine
parameter_list|()
function_decl|;
comment|/**    * Returns the column number of the first character for current token (being    * matched after the last call to BeginTOken).    */
DECL|method|getBeginColumn
name|int
name|getBeginColumn
parameter_list|()
function_decl|;
comment|/**    * Returns the line number of the first character for current token (being    * matched after the last call to BeginTOken).    */
DECL|method|getBeginLine
name|int
name|getBeginLine
parameter_list|()
function_decl|;
comment|/**    * Backs up the input stream by amount steps. Lexer calls this method if it    * had already read some characters, but could not use them to match a    * (longer) token. So, they will be used again as the prefix of the next    * token and it is the implemetation's responsibility to do this right.    */
DECL|method|backup
name|void
name|backup
parameter_list|(
name|int
name|amount
parameter_list|)
function_decl|;
comment|/**    * Returns the next character that marks the beginning of the next token.    * All characters must remain in the buffer between two successive calls    * to this method to implement backup correctly.    */
DECL|method|BeginToken
name|char
name|BeginToken
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
function_decl|;
comment|/**    * Returns a string made up of characters from the marked token beginning    * to the current buffer position. Implementations have the choice of returning    * anything that they want to. For example, for efficiency, one might decide    * to just return null, which is a valid implementation.    */
DECL|method|GetImage
name|String
name|GetImage
parameter_list|()
function_decl|;
comment|/**    * Returns an array of characters that make up the suffix of length 'len' for    * the currently matched token. This is used to build up the matched string    * for use in actions in the case of MORE. A simple and inefficient    * implementation of this is as follows :    *    *   {    *      String t = GetImage();    *      return t.substring(t.length() - len, t.length()).toCharArray();    *   }    */
DECL|method|GetSuffix
name|char
index|[]
name|GetSuffix
parameter_list|(
name|int
name|len
parameter_list|)
function_decl|;
comment|/**    * The lexer calls this function to indicate that it is done with the stream    * and hence implementations can free any resources held by this class.    * Again, the body of this function can be just empty and it will not    * affect the lexer's operation.    */
DECL|method|Done
name|void
name|Done
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|/* JavaCC - OriginalChecksum=a81c9280a3ec4578458c607a9d95acb4 (do not edit this line) */
end_comment

end_unit

