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
comment|/**   * A callback interface for basic XML error events.   *<p><em>This interface is part of the Java implementation of SAX,    * the Simple API for XML.  It is free for both commercial and    * non-commercial use, and is distributed with no warrantee, real    * or implied.</em></p>   *<p>If you do not set an error handler, then a parser will report   * warnings to<code>System.err</code>, and will throw an (unspecified)   * exception for fata errors.</p>   * @author David Megginson, Microstar Software Ltd.   * @see hplb.org.xml.sax.Parser#setErrorHandler   */
end_comment

begin_interface
DECL|interface|ErrorHandler
specifier|public
interface|interface
name|ErrorHandler
block|{
comment|/**     * Handle a non-fatal warning.     *<p>A SAX parser will use this callback to report a condition     * that is not serious enough to stop the parse (though you may     * still stop the parse if you wish).</p>     * @param message The warning message.     * @param systemID The URI of the entity that caused the warning, or     *                 null if not available.     * @param line The line number in the entity, or -1 if not available.     * @param column The column number in the entity, or -1 if not available.     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|warning
specifier|public
name|void
name|warning
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
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
function_decl|;
comment|/**     * Handle a fatal error.     *<p>A SAX parser will use this callback to report a condition     * that is serious enough to invalidate the parse, and may not     * report all (or any) significant parse events after this.  Ordinarily,     * you should stop immediately with an exception, but you can continue     * to try to collect more errors if you wish.</p>     * @param message The error message.     * @param systemID The URI of the entity that caused the error, or     *                 null if not available.     * @param line The line number in the entity, or -1 if not available.     * @param column The column number in the entity, or -1 if not available.     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|fatal
specifier|public
name|void
name|fatal
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
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

