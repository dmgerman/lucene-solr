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
comment|/**   * A standard interface for event-driven XML parsers.   *<p><em>This interface is part of the Java implementation of SAX,    * the Simple API for XML.  It is free for both commercial and    * non-commercial use, and is distributed with no warrantee, real    * or implied.</em></p>   *<p>All SAX-conformant XML parsers (or their front-end SAX drivers)   *<em>must</em> implement this interface, together with a zero-argument   * constructor.</p>   *<p>You can plug three different kinds of callback interfaces into   * a basic SAX parser: one for entity handling, one for basic document   * events, and one for error reporting.  It is not an error to start   * a parse without setting any handlers.</p>   * @author David Megginson, Microstar Software Ltd.   */
end_comment

begin_interface
DECL|interface|Parser
specifier|public
interface|interface
name|Parser
block|{
comment|/**     * Register the handler for basic entity events.     *<p>If you begin a parse without setting an entity handler,     * the parser will by default resolve all entities to their     * default system IDs.</p>     * @param handler An object to receive callbacks for events.     * @see hplb.org.xml.sax.EntityHandler     */
DECL|method|setEntityHandler
specifier|public
name|void
name|setEntityHandler
parameter_list|(
name|EntityHandler
name|handler
parameter_list|)
function_decl|;
comment|/**     * Register the handler for basic document events.     *<p>You may begin the parse without setting a handler, but     * in that case no document events will be reported.</p>     * @param handler An object to receive callbacks for events.     * @see hplb.org.xml.sax.DocumentHandler     */
DECL|method|setDocumentHandler
specifier|public
name|void
name|setDocumentHandler
parameter_list|(
name|DocumentHandler
name|handler
parameter_list|)
function_decl|;
comment|/**     * Register the handler for errors and warnings.     *<p>If you begin a parse without setting an error handlers,     * warnings will be printed to System.err, and errors will     * throw an unspecified exception.</p>     * @param handler An object to receive callbacks for errors.     * @see hplb.org.xml.sax.ErrorHandler     */
DECL|method|setErrorHandler
specifier|public
name|void
name|setErrorHandler
parameter_list|(
name|ErrorHandler
name|handler
parameter_list|)
function_decl|;
comment|/**     * Parse an XML document.     *<p>Nothing exciting will happen unless you have set handlers.</p>     * @param publicID The public identifier for the document, or null     *                 if none is available.     * @param systemID The system identifier (URI) for the document.     * @exception java.lang.Exception This method may throw any exception,      *            but the parser itself     *            will throw only exceptions derived from java.io.IOException;     *            anything else will come from your handlers.     * @see #setEntityHandler     * @see #setDocumentHandler     * @see #setErrorHandler     */
DECL|method|parse
name|void
name|parse
parameter_list|(
name|String
name|publicID
parameter_list|,
name|String
name|systemID
parameter_list|)
throws|throws
name|java
operator|.
name|lang
operator|.
name|Exception
function_decl|;
block|}
end_interface

end_unit

