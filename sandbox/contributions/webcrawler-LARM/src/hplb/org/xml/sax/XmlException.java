begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// $Id$
end_comment

begin_package
DECL|package|hplb.org.xml.sax
package|package
name|hplb
operator|.
name|org
operator|.
name|xml
operator|.
name|sax
package|;
end_package

begin_comment
comment|/**   * An exception for reporting XML parsing errors.   *<p><em>This interface is part of the Java implementation of SAX,    * the Simple API for XML.  It is free for both commercial and    * non-commercial use, and is distributed with no warrantee, real    * or implied.</em></p>   *<p>This exception is not a required part of SAX, and it is not   * referenced in any of the core interfaces.  It is used only in   * the optional HandlerBase base class, as a means of signalling   * parsing errors.</p>   * @author David Megginson, Microstar Software Ltd.   * @see hplb.org.xml.sax.HandlerBase#fatal   */
end_comment

begin_class
DECL|class|XmlException
specifier|public
class|class
name|XmlException
extends|extends
name|Exception
block|{
comment|/**     * Construct a new exception with information about the location.     */
DECL|method|XmlException
specifier|public
name|XmlException
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|systemID
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|systemID
operator|=
name|systemID
expr_stmt|;
name|this
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|this
operator|.
name|column
operator|=
name|column
expr_stmt|;
block|}
comment|/**     * Find the system identifier (URI) where the error occurred.     * @return A string representing the URI, or null if none is available.     */
DECL|method|getSystemID
specifier|public
name|String
name|getSystemID
parameter_list|()
block|{
return|return
name|systemID
return|;
block|}
comment|/**     * Find the line number where the error occurred.     * @return The line number, or -1 if none is available.     */
DECL|method|getLine
specifier|public
name|int
name|getLine
parameter_list|()
block|{
return|return
name|line
return|;
block|}
comment|/**     * Find the column number (line offset) where the error occurred.     * @return The column number, or -1 if none is available.     */
DECL|method|getColumn
specifier|public
name|int
name|getColumn
parameter_list|()
block|{
return|return
name|column
return|;
block|}
comment|//
comment|// Internal state.
comment|//
DECL|field|systemID
specifier|private
name|String
name|systemID
decl_stmt|;
DECL|field|line
specifier|private
name|int
name|line
decl_stmt|;
DECL|field|column
specifier|private
name|int
name|column
decl_stmt|;
block|}
end_class

end_unit

