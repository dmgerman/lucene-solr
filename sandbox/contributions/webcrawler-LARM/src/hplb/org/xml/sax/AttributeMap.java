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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_comment
comment|/**   * A map of attributes for the current element.   *<p><em>This interface is part of the Java implementation of SAX,    * the Simple API for XML.  It is free for both commercial and    * non-commercial use, and is distributed with no warrantee, real    * or implied.</em></p>   *<p>This map will be valid only during the invocation of the   *<code>startElement</code> callback: if you need to use attribute   * information elsewhere, you will need to make your own copies.</p>   * @author David Megginson, Microstar Software Ltd.   * @see hplb.org.xml.sax.DocumentHandler#startElement   */
end_comment

begin_interface
DECL|interface|AttributeMap
specifier|public
interface|interface
name|AttributeMap
block|{
comment|/**     * Find the names of all available attributes for an element.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return An enumeration of zero or more Strings.     * @see java.util.Enumeration     * @see hplb.org.xml.sax.DocumentHandler#startElement     */
DECL|method|getAttributeNames
specifier|public
name|Enumeration
name|getAttributeNames
parameter_list|()
function_decl|;
comment|/**     * Get the value of an attribute as a String.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return The value as a String, or null if the attribute has no value.     * @see hplb.org.xml.sax.DocumentHandler#startElement     */
DECL|method|getValue
specifier|public
name|String
name|getValue
parameter_list|(
name|String
name|attributeName
parameter_list|)
function_decl|;
comment|/**     * Check if an attribute value is the name of an entity.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return true if the attribute is an entity name.     * @see #getEntityPublicID     * @see #getEntitySystemID     * @see #getNotationName     * @see #getNotationPublicID     * @see #getNotationSystemID     * @see hplb.org.xml.sax.DocumentHandler#startElement     */
DECL|method|isEntity
specifier|public
name|boolean
name|isEntity
parameter_list|(
name|String
name|aname
parameter_list|)
function_decl|;
comment|/**     * Check if an attribute value is the name of a notation.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return true if the attribute is a notation name.     * @see #getNotationPublicID     * @see #getNotationSystemID     * @see hplb.org.xml.sax.DocumentHandler#startElement     */
DECL|method|isNotation
specifier|public
name|boolean
name|isNotation
parameter_list|(
name|String
name|aname
parameter_list|)
function_decl|;
comment|/**     * Check if an attribute value is a unique identifier.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return true if the attribute is a unique identifier.     * @see hplb.org.xml.sax.DocumentHandler#startElement     */
DECL|method|isId
specifier|public
name|boolean
name|isId
parameter_list|(
name|String
name|aname
parameter_list|)
function_decl|;
comment|/**     * Check if an attribute value is a reference to an ID.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return true if the attribute is a reference to an ID.     * @see hplb.org.xml.sax.DocumentHandler#startElement     */
DECL|method|isIdref
specifier|public
name|boolean
name|isIdref
parameter_list|(
name|String
name|aname
parameter_list|)
function_decl|;
comment|/**     * Get the public identifier for an ENTITY attribute.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return The public identifier or null if there is none (or if     *         the attribute value is not an entity name)     * @see #isEntity     */
DECL|method|getEntityPublicID
specifier|public
name|String
name|getEntityPublicID
parameter_list|(
name|String
name|aname
parameter_list|)
function_decl|;
comment|/**     * Get the system identifer for an ENTITY attribute.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return The system identifier or null if there is none (or if     *         the attribute value is not an entity name)     * @see #isEntity     */
DECL|method|getEntitySystemID
specifier|public
name|String
name|getEntitySystemID
parameter_list|(
name|String
name|aname
parameter_list|)
function_decl|;
comment|/**     * Get the notation name for an ENTITY attribute.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return The notation name or null if there is none (or if     *         the attribute value is not an entity name)     * @see #isEntity     */
DECL|method|getNotationName
specifier|public
name|String
name|getNotationName
parameter_list|(
name|String
name|aname
parameter_list|)
function_decl|;
comment|/**     * Get the notation public ID for an ENTITY or NOTATION attribute.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return The public identifier or null if there is none (or if     *         the attribute value is not an entity or notation name)     * @see #isEntity     * @see #isNotation     */
DECL|method|getNotationPublicID
specifier|public
name|String
name|getNotationPublicID
parameter_list|(
name|String
name|aname
parameter_list|)
function_decl|;
comment|/**     * Get the notation system ID for an ENTITY or NOTATION attribute.     *<p>This applies to the current element, and can be called only     * during an invocation of<code>startElement</code>.</p>      * @return The system identifier or null if there is none (or if     *         the attribute value is not an entity or notation name)     * @see #isEntity     * @see #isNotation     */
DECL|method|getNotationSystemID
specifier|public
name|String
name|getNotationSystemID
parameter_list|(
name|String
name|aname
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

