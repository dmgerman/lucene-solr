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
comment|/**   * A callback interface for basic XML document events.   *<p><em>This interface is part of the Java implementation of SAX,    * the Simple API for XML.  It is free for both commercial and    * non-commercial use, and is distributed with no warrantee, real    * or implied.</em></p>   *<p>This is the main handler for basic document events; it provides   * information on roughly the same level as the ESIS in full SGML,   * concentrating on logical structure rather than lexical    * representation.</p>   *<p>If you do not set a document handler, then by default all of these   * events will simply be ignored.</p>   * @author David Megginson, Microstar Software Ltd.   * @see hplb.org.xml.sax.Parser@setDocumentHandler   */
end_comment

begin_interface
DECL|interface|DocumentHandler
specifier|public
interface|interface
name|DocumentHandler
block|{
comment|/**     * Handle the start of a document.     *<p>This is the first event called by a     * SAX-conformant parser, so you can use it to allocate and     * initialise new objects for the document.</p>     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|startDocument
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**     * Handle the end of a document.     *<p>This is the last event called by a     * SAX-conformant parser, so you can use it to finalize and     * clean up objects for the document.</p>     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|endDocument
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**     * Handle the document type declaration.     *<p>This will appear only if the XML document contains a     *<code>DOCTYPE</code> declaration.</p>     * @param name The document type name.     * @param publicID The public identifier of the external DTD subset     *                 (if any), or null.     * @param systemID The system identifier of the external DTD subset     *                 (if any), or null.     * @param name The document type name.     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|doctype
specifier|public
name|void
name|doctype
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicID
parameter_list|,
name|String
name|systemID
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**     * Handle the start of an element.     *<p>Please note that the information in the<code>attributes</code>     * parameter will be accurate only for the duration of this handler:     * if you need to use the information elsewhere, you should copy      * it.</p>     * @param name The element type name.     * @param attributes The available attributes.     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|startElement
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|name
parameter_list|,
name|AttributeMap
name|attributes
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**     * Handle the end of an element.     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|endElement
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**     * Handle significant character data.     *<p>Please note that the contents of the array will be     * accurate only for the duration of this handler: if you need to     * use them elsewhere, you should make your own copy, possible     * by constructing a string:</p>     *<pre>     * String data = new String(ch, start, length);     *</pre>     * @param ch An array of characters.     * @param start The starting position in the array.     * @param length The number of characters to use in the array.     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|characters
specifier|public
name|void
name|characters
parameter_list|(
name|char
name|ch
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**     * Handle ignorable whitespace.     *<p>Please note that the contents of the array will be     * accurate only for the duration of this handler: if you need to     * use them elsewhere, you should make your own copy, possible     * by constructing a string:</p>     *<pre>     * String whitespace = new String(ch, start, length);     *</pre>     * @param ch An array of whitespace characters.     * @param start The starting position in the array.     * @param length The number of characters to use in the array.     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|ignorable
specifier|public
name|void
name|ignorable
parameter_list|(
name|char
name|ch
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**     * Handle a processing instruction.     *<p>XML processing instructions have two parts: a target, which     * is a name, followed optionally by data.</p>     * @exception java.lang.Exception You may throw any exception.     */
DECL|method|processingInstruction
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|remainder
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

