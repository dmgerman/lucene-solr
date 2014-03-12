begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryparser.ext
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|ext
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
operator|.
name|QueryParserBase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * The {@link Extensions} class represents an extension mapping to associate  * {@link ParserExtension} instances with extension keys. An extension key is a  * string encoded into a Lucene standard query parser field symbol recognized by  * {@link ExtendableQueryParser}. The query parser passes each extension field  * token to {@link #splitExtensionField(String, String)} to separate the  * extension key from the field identifier.  *<p>  * In addition to the key to extension mapping this class also defines the field  * name overloading scheme. {@link ExtendableQueryParser} uses the given  * extension to split the actual field name and extension key by calling  * {@link #splitExtensionField(String, String)}. To change the order or the key  * / field name encoding scheme users can subclass {@link Extensions} to  * implement their own.  *   * @see ExtendableQueryParser  * @see ParserExtension  */
end_comment

begin_class
DECL|class|Extensions
specifier|public
class|class
name|Extensions
block|{
DECL|field|extensions
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ParserExtension
argument_list|>
name|extensions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|extensionFieldDelimiter
specifier|private
specifier|final
name|char
name|extensionFieldDelimiter
decl_stmt|;
comment|/**    * The default extension field delimiter character. This constant is set to    * ':'    */
DECL|field|DEFAULT_EXTENSION_FIELD_DELIMITER
specifier|public
specifier|static
specifier|final
name|char
name|DEFAULT_EXTENSION_FIELD_DELIMITER
init|=
literal|':'
decl_stmt|;
comment|/**    * Creates a new {@link Extensions} instance with the    * {@link #DEFAULT_EXTENSION_FIELD_DELIMITER} as a delimiter character.    */
DECL|method|Extensions
specifier|public
name|Extensions
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_EXTENSION_FIELD_DELIMITER
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new {@link Extensions} instance    *     * @param extensionFieldDelimiter    *          the extensions field delimiter character    */
DECL|method|Extensions
specifier|public
name|Extensions
parameter_list|(
name|char
name|extensionFieldDelimiter
parameter_list|)
block|{
name|this
operator|.
name|extensionFieldDelimiter
operator|=
name|extensionFieldDelimiter
expr_stmt|;
block|}
comment|/**    * Adds a new {@link ParserExtension} instance associated with the given key.    *     * @param key    *          the parser extension key    * @param extension    *          the parser extension    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|String
name|key
parameter_list|,
name|ParserExtension
name|extension
parameter_list|)
block|{
name|this
operator|.
name|extensions
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|extension
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the {@link ParserExtension} instance for the given key or    *<code>null</code> if no extension can be found for the key.    *     * @param key    *          the extension key    * @return the {@link ParserExtension} instance for the given key or    *<code>null</code> if no extension can be found for the key.    */
DECL|method|getExtension
specifier|public
specifier|final
name|ParserExtension
name|getExtension
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|this
operator|.
name|extensions
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**    * Returns the extension field delimiter    *     * @return the extension field delimiter    */
DECL|method|getExtensionFieldDelimiter
specifier|public
name|char
name|getExtensionFieldDelimiter
parameter_list|()
block|{
return|return
name|extensionFieldDelimiter
return|;
block|}
comment|/**    * Splits a extension field and returns the field / extension part as a    * {@link Pair}. This method tries to split on the first occurrence of the    * extension field delimiter, if the delimiter is not present in the string    * the result will contain a<code>null</code> value for the extension key and    * the given field string as the field value. If the given extension field    * string contains no field identifier the result pair will carry the given    * default field as the field value.    *     * @param defaultField    *          the default query field    * @param field    *          the extension field string    * @return a {@link Pair} with the field name as the {@link Pair#cur} and the    *         extension key as the {@link Pair#cud}    */
DECL|method|splitExtensionField
specifier|public
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|splitExtensionField
parameter_list|(
name|String
name|defaultField
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|int
name|indexOf
init|=
name|field
operator|.
name|indexOf
argument_list|(
name|this
operator|.
name|extensionFieldDelimiter
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOf
operator|<
literal|0
condition|)
return|return
operator|new
name|Pair
argument_list|<>
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
return|;
specifier|final
name|String
name|indexField
init|=
name|indexOf
operator|==
literal|0
condition|?
name|defaultField
else|:
name|field
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|indexOf
argument_list|)
decl_stmt|;
specifier|final
name|String
name|extensionKey
init|=
name|field
operator|.
name|substring
argument_list|(
name|indexOf
operator|+
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|Pair
argument_list|<>
argument_list|(
name|indexField
argument_list|,
name|extensionKey
argument_list|)
return|;
block|}
comment|/**    * Escapes an extension field. The default implementation is equivalent to    * {@link QueryParser#escape(String)}.    *     * @param extfield    *          the extension field identifier    * @return the extension field identifier with all special chars escaped with    *         a backslash character.    */
DECL|method|escapeExtensionField
specifier|public
name|String
name|escapeExtensionField
parameter_list|(
name|String
name|extfield
parameter_list|)
block|{
return|return
name|QueryParserBase
operator|.
name|escape
argument_list|(
name|extfield
argument_list|)
return|;
block|}
comment|/**    * Builds an extension field string from a given extension key and the default    * query field. The default field and the key are delimited with the extension    * field delimiter character. This method makes no assumption about the order    * of the extension key and the field. By default the extension key is    * appended to the end of the returned string while the field is added to the    * beginning. Special Query characters are escaped in the result.    *<p>    * Note: {@link Extensions} subclasses must maintain the contract between    * {@link #buildExtensionField(String)} and    * {@link #splitExtensionField(String, String)} where the latter inverts the    * former.    *</p>    */
DECL|method|buildExtensionField
specifier|public
name|String
name|buildExtensionField
parameter_list|(
name|String
name|extensionKey
parameter_list|)
block|{
return|return
name|buildExtensionField
argument_list|(
name|extensionKey
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/**    * Builds an extension field string from a given extension key and the    * extensions field. The field and the key are delimited with the extension    * field delimiter character. This method makes no assumption about the order    * of the extension key and the field. By default the extension key is    * appended to the end of the returned string while the field is added to the    * beginning. Special Query characters are escaped in the result.    *<p>    * Note: {@link Extensions} subclasses must maintain the contract between    * {@link #buildExtensionField(String, String)} and    * {@link #splitExtensionField(String, String)} where the latter inverts the    * former.    *</p>    *     * @param extensionKey    *          the extension key    * @param field    *          the field to apply the extension on.    * @return escaped extension field identifier    * @see #buildExtensionField(String) to use the default query field    */
DECL|method|buildExtensionField
specifier|public
name|String
name|buildExtensionField
parameter_list|(
name|String
name|extensionKey
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|this
operator|.
name|extensionFieldDelimiter
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|extensionKey
argument_list|)
expr_stmt|;
return|return
name|escapeExtensionField
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * This class represents a generic pair.    *     * @param<Cur>    *          the pairs first element    * @param<Cud>    *          the pairs last element of the pair.    */
DECL|class|Pair
specifier|public
specifier|static
class|class
name|Pair
parameter_list|<
name|Cur
parameter_list|,
name|Cud
parameter_list|>
block|{
DECL|field|cur
specifier|public
specifier|final
name|Cur
name|cur
decl_stmt|;
DECL|field|cud
specifier|public
specifier|final
name|Cud
name|cud
decl_stmt|;
comment|/**      * Creates a new Pair      *       * @param cur      *          the pairs first element      * @param cud      *          the pairs last element      */
DECL|method|Pair
specifier|public
name|Pair
parameter_list|(
name|Cur
name|cur
parameter_list|,
name|Cud
name|cud
parameter_list|)
block|{
name|this
operator|.
name|cur
operator|=
name|cur
expr_stmt|;
name|this
operator|.
name|cud
operator|=
name|cud
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

