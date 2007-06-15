begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
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
name|document
operator|.
name|Fieldable
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
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|Tokenizer
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
name|analysis
operator|.
name|Token
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
name|search
operator|.
name|SortField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|ValueSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|OrdFieldSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|Sorting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|XMLWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|TextResponseWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
operator|.
name|SolrAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Base class for all field types used by an index schema.  *  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|FieldType
specifier|public
specifier|abstract
class|class
name|FieldType
extends|extends
name|FieldProperties
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FieldType
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/** The name of the type (not the name of the field) */
DECL|field|typeName
specifier|protected
name|String
name|typeName
decl_stmt|;
comment|/** additional arguments specified in the field type declaration */
DECL|field|args
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
decl_stmt|;
comment|/** properties explicitly set to true */
DECL|field|trueProperties
specifier|protected
name|int
name|trueProperties
decl_stmt|;
comment|/** properties explicitly set to false */
DECL|field|falseProperties
specifier|protected
name|int
name|falseProperties
decl_stmt|;
DECL|field|properties
name|int
name|properties
decl_stmt|;
comment|/** Returns true if fields of this type should be tokenized */
DECL|method|isTokenized
specifier|public
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|TOKENIZED
operator|)
operator|!=
literal|0
return|;
block|}
comment|/** subclasses should initialize themselves with the args provided    * and remove valid arguments.  leftover arguments will cause an exception.    * Common boolean properties have already been handled.    *    */
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{   }
comment|// Handle additional arguments...
DECL|method|setArgs
name|void
name|setArgs
parameter_list|(
name|IndexSchema
name|schema
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
comment|// default to STORED and INDEXED, and MULTIVALUED depending on schema version
name|properties
operator|=
operator|(
name|STORED
operator||
name|INDEXED
operator|)
expr_stmt|;
if|if
condition|(
name|schema
operator|.
name|getVersion
argument_list|()
operator|<
literal|1.1f
condition|)
name|properties
operator||=
name|MULTIVALUED
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|initArgs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|trueProperties
operator|=
name|FieldProperties
operator|.
name|parseProperties
argument_list|(
name|initArgs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|falseProperties
operator|=
name|FieldProperties
operator|.
name|parseProperties
argument_list|(
name|initArgs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|properties
operator|&=
operator|~
name|falseProperties
expr_stmt|;
name|properties
operator||=
name|trueProperties
expr_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|FieldProperties
operator|.
name|propertyNames
control|)
name|initArgs
operator|.
name|remove
argument_list|(
name|prop
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|schema
argument_list|,
name|initArgs
argument_list|)
expr_stmt|;
name|String
name|positionInc
init|=
name|initArgs
operator|.
name|get
argument_list|(
literal|"positionIncrementGap"
argument_list|)
decl_stmt|;
if|if
condition|(
name|positionInc
operator|!=
literal|null
condition|)
block|{
name|Analyzer
name|analyzer
init|=
name|getAnalyzer
argument_list|()
decl_stmt|;
if|if
condition|(
name|analyzer
operator|instanceof
name|SolrAnalyzer
condition|)
block|{
operator|(
operator|(
name|SolrAnalyzer
operator|)
name|analyzer
operator|)
operator|.
name|setPositionIncrementGap
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|positionInc
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't set positionIncrementGap on custom analyzer "
operator|+
name|analyzer
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
name|analyzer
operator|=
name|getQueryAnalyzer
argument_list|()
expr_stmt|;
if|if
condition|(
name|analyzer
operator|instanceof
name|SolrAnalyzer
condition|)
block|{
operator|(
operator|(
name|SolrAnalyzer
operator|)
name|analyzer
operator|)
operator|.
name|setPositionIncrementGap
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|positionInc
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't set positionIncrementGap on custom analyzer "
operator|+
name|analyzer
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
name|initArgs
operator|.
name|remove
argument_list|(
literal|"positionIncrementGap"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|initArgs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"schema fieldtype "
operator|+
name|typeName
operator|+
literal|"("
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
operator|+
literal|" invalid arguments:"
operator|+
name|initArgs
argument_list|)
throw|;
block|}
block|}
comment|/** :TODO: document this method */
DECL|method|restrictProps
specifier|protected
name|void
name|restrictProps
parameter_list|(
name|int
name|props
parameter_list|)
block|{
if|if
condition|(
operator|(
name|properties
operator|&
name|props
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"schema fieldtype "
operator|+
name|typeName
operator|+
literal|"("
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
operator|+
literal|" invalid properties:"
operator|+
name|propertiesToString
argument_list|(
name|properties
operator|&
name|props
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/** The Name of this FieldType as specified in the schema file */
DECL|method|getTypeName
specifier|public
name|String
name|getTypeName
parameter_list|()
block|{
return|return
name|typeName
return|;
block|}
DECL|method|setTypeName
name|void
name|setTypeName
parameter_list|(
name|String
name|typeName
parameter_list|)
block|{
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|typeName
operator|+
literal|"{class="
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
comment|//            + propertiesToString(properties)
operator|+
operator|(
name|analyzer
operator|!=
literal|null
condition|?
literal|",analyzer="
operator|+
name|analyzer
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
else|:
literal|""
operator|)
operator|+
literal|",args="
operator|+
name|args
operator|+
literal|"}"
return|;
block|}
comment|/**    * Used for adding a document when a field needs to be created from a    * type and a string.    *    *<p>    * By default, the indexed value is the same as the stored value    * (taken from toInternal()).   Having a different representation for    * external, internal, and indexed would present quite a few problems    * given the current Lucene architecture.  An analyzer for adding docs    * would need to translate internal->indexed while an analyzer for    * querying would need to translate external-&gt;indexed.    *</p>    *<p>    * The only other alternative to having internal==indexed would be to have    * internal==external.   In this case, toInternal should convert to    * the indexed representation, toExternal() should do nothing, and    * createField() should *not* call toInternal, but use the external    * value and set tokenized=true to get Lucene to convert to the    * internal(indexed) form.    *</p>    *    * :TODO: clean up and clarify this explanation.    *    * @see #toInternal    */
DECL|method|createField
specifier|public
name|Field
name|createField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|String
name|val
decl_stmt|;
try|try
block|{
name|val
operator|=
name|toInternal
argument_list|(
name|externalVal
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Error while creating field '"
operator|+
name|field
operator|+
literal|"' from value '"
operator|+
name|externalVal
operator|+
literal|"'"
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
throw|;
block|}
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
operator|!
name|field
operator|.
name|indexed
argument_list|()
operator|&&
operator|!
name|field
operator|.
name|stored
argument_list|()
condition|)
block|{
name|log
operator|.
name|finest
argument_list|(
literal|"Ignoring unindexed/unstored field: "
operator|+
name|field
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|val
argument_list|,
name|getFieldStore
argument_list|(
name|field
argument_list|,
name|val
argument_list|)
argument_list|,
name|getFieldIndex
argument_list|(
name|field
argument_list|,
name|val
argument_list|)
argument_list|,
name|getFieldTermVec
argument_list|(
name|field
argument_list|,
name|val
argument_list|)
argument_list|)
decl_stmt|;
name|f
operator|.
name|setOmitNorms
argument_list|(
name|field
operator|.
name|omitNorms
argument_list|()
argument_list|)
expr_stmt|;
name|f
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
comment|/* Helpers for field construction */
DECL|method|getFieldTermVec
specifier|protected
name|Field
operator|.
name|TermVector
name|getFieldTermVec
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|internalVal
parameter_list|)
block|{
name|Field
operator|.
name|TermVector
name|ftv
init|=
name|Field
operator|.
name|TermVector
operator|.
name|NO
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|storeTermPositions
argument_list|()
operator|&&
name|field
operator|.
name|storeTermOffsets
argument_list|()
condition|)
name|ftv
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
expr_stmt|;
elseif|else
if|if
condition|(
name|field
operator|.
name|storeTermPositions
argument_list|()
condition|)
name|ftv
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS
expr_stmt|;
elseif|else
if|if
condition|(
name|field
operator|.
name|storeTermOffsets
argument_list|()
condition|)
name|ftv
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|WITH_OFFSETS
expr_stmt|;
elseif|else
if|if
condition|(
name|field
operator|.
name|storeTermVector
argument_list|()
condition|)
name|ftv
operator|=
name|Field
operator|.
name|TermVector
operator|.
name|YES
expr_stmt|;
return|return
name|ftv
return|;
block|}
DECL|method|getFieldStore
specifier|protected
name|Field
operator|.
name|Store
name|getFieldStore
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|internalVal
parameter_list|)
block|{
return|return
name|field
operator|.
name|stored
argument_list|()
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
return|;
block|}
DECL|method|getFieldIndex
specifier|protected
name|Field
operator|.
name|Index
name|getFieldIndex
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|String
name|internalVal
parameter_list|)
block|{
return|return
name|field
operator|.
name|indexed
argument_list|()
condition|?
operator|(
name|isTokenized
argument_list|()
condition|?
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
else|:
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
operator|)
else|:
name|Field
operator|.
name|Index
operator|.
name|NO
return|;
block|}
comment|/**    * Convert an external value (from XML update command or from query string)    * into the internal format.    * @see #toExternal    */
DECL|method|toInternal
specifier|public
name|String
name|toInternal
parameter_list|(
name|String
name|val
parameter_list|)
block|{
comment|// - used in delete when a Term needs to be created.
comment|// - used by the default getTokenizer() and createField()
return|return
name|val
return|;
block|}
comment|/**    * Convert the stored-field format to an external (string, human readable)    * value    * @see #toInternal    */
DECL|method|toExternal
specifier|public
name|String
name|toExternal
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
comment|// currently used in writing XML of the search result (but perhaps
comment|// a more efficient toXML(Fieldable f, Writer w) should be used
comment|// in the future.
return|return
name|f
operator|.
name|stringValue
argument_list|()
return|;
block|}
comment|/**    * Convert the stored-field format to an external object.      * @see #toInternal    * @since solr 1.3    */
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|toExternal
argument_list|(
name|f
argument_list|)
return|;
comment|// by default use the string
block|}
comment|/** :TODO: document this method */
DECL|method|indexedToReadable
specifier|public
name|String
name|indexedToReadable
parameter_list|(
name|String
name|indexedForm
parameter_list|)
block|{
return|return
name|indexedForm
return|;
block|}
comment|/** :TODO: document this method */
DECL|method|storedToReadable
specifier|public
name|String
name|storedToReadable
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
return|return
name|toExternal
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/** :TODO: document this method */
DECL|method|storedToIndexed
specifier|public
name|String
name|storedToIndexed
parameter_list|(
name|Fieldable
name|f
parameter_list|)
block|{
comment|// right now, the transformation of single valued fields like SortableInt
comment|// is done when the Field is created, not at analysis time... this means
comment|// that the indexed form is the same as the stored field form.
return|return
name|f
operator|.
name|stringValue
argument_list|()
return|;
block|}
comment|/*********   // default analyzer for non-text fields.   // Only reads 80 bytes, but that should be plenty for a single value.   public Analyzer getAnalyzer() {     if (analyzer != null) return analyzer;      // the default analyzer...     return new Analyzer() {       public TokenStream tokenStream(String fieldName, Reader reader) {         return new Tokenizer(reader) {           final char[] cbuf = new char[80];           public Token next() throws IOException {             int n = input.read(cbuf,0,80);             if (n<=0) return null;             String s = toInternal(new String(cbuf,0,n));             return new Token(s,0,n);           };         };       }     };   }   **********/
comment|/**    * Default analyzer for types that only produce 1 verbatim token...    * A maximum size of chars to be read must be specified    */
DECL|class|DefaultAnalyzer
specifier|protected
specifier|final
class|class
name|DefaultAnalyzer
extends|extends
name|SolrAnalyzer
block|{
DECL|field|maxChars
specifier|final
name|int
name|maxChars
decl_stmt|;
DECL|method|DefaultAnalyzer
name|DefaultAnalyzer
parameter_list|(
name|int
name|maxChars
parameter_list|)
block|{
name|this
operator|.
name|maxChars
operator|=
name|maxChars
expr_stmt|;
block|}
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|Tokenizer
argument_list|(
name|reader
argument_list|)
block|{
name|char
index|[]
name|cbuf
init|=
operator|new
name|char
index|[
name|maxChars
index|]
decl_stmt|;
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|n
init|=
name|input
operator|.
name|read
argument_list|(
name|cbuf
argument_list|,
literal|0
argument_list|,
name|maxChars
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<=
literal|0
condition|)
return|return
literal|null
return|;
name|String
name|s
init|=
name|toInternal
argument_list|(
operator|new
name|String
argument_list|(
name|cbuf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
argument_list|)
decl_stmt|;
comment|// virtual func on parent
return|return
operator|new
name|Token
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
return|;
block|}
empty_stmt|;
block|}
return|;
block|}
block|}
comment|/**    * Analyzer set by schema for text types to use when indexing fields    * of this type, subclasses can set analyzer themselves or override    * getAnalyzer()    * @see #getAnalyzer    */
DECL|field|analyzer
specifier|protected
name|Analyzer
name|analyzer
init|=
operator|new
name|DefaultAnalyzer
argument_list|(
literal|256
argument_list|)
decl_stmt|;
comment|/**    * Analyzer set by schema for text types to use when searching fields    * of this type, subclasses can set analyzer themselves or override    * getAnalyzer()    * @see #getQueryAnalyzer    */
DECL|field|queryAnalyzer
specifier|protected
name|Analyzer
name|queryAnalyzer
init|=
name|analyzer
decl_stmt|;
comment|/**    * Returns the Analyzer to be used when indexing fields of this type.    *<p>    * This method may be called many times, at any time.    *</p>    * @see #getQueryAnalyzer    */
DECL|method|getAnalyzer
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
comment|/**    * Returns the Analyzer to be used when searching fields of this type.    *<p>    * This method may be called many times, at any time.    *</p>    * @see #getAnalyzer    */
DECL|method|getQueryAnalyzer
specifier|public
name|Analyzer
name|getQueryAnalyzer
parameter_list|()
block|{
return|return
name|queryAnalyzer
return|;
block|}
comment|/**    * Sets the Analyzer to be used when indexing fields of this type.    * @see #getAnalyzer    */
DECL|method|setAnalyzer
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
name|log
operator|.
name|finest
argument_list|(
literal|"FieldType: "
operator|+
name|typeName
operator|+
literal|".setAnalyzer("
operator|+
name|analyzer
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets the Analyzer to be used when querying fields of this type.    * @see #getQueryAnalyzer    */
DECL|method|setQueryAnalyzer
specifier|public
name|void
name|setQueryAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|queryAnalyzer
operator|=
name|analyzer
expr_stmt|;
name|log
operator|.
name|finest
argument_list|(
literal|"FieldType: "
operator|+
name|typeName
operator|+
literal|".setQueryAnalyzer("
operator|+
name|analyzer
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Renders the specified field as XML    */
DECL|method|write
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|XMLWriter
name|xmlWriter
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * calls back to TextResponseWriter to write the field value    */
DECL|method|write
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|TextResponseWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|Fieldable
name|f
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the SortField instance that should be used to sort fields    * of this type.    */
DECL|method|getSortField
specifier|public
specifier|abstract
name|SortField
name|getSortField
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|top
parameter_list|)
function_decl|;
comment|/**    * Utility usable by subclasses when they want to get basic String sorting.    */
DECL|method|getStringSort
specifier|protected
name|SortField
name|getStringSort
parameter_list|(
name|SchemaField
name|field
parameter_list|,
name|boolean
name|reverse
parameter_list|)
block|{
return|return
name|Sorting
operator|.
name|getStringSortField
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|reverse
argument_list|,
name|field
operator|.
name|sortMissingLast
argument_list|()
argument_list|,
name|field
operator|.
name|sortMissingFirst
argument_list|()
argument_list|)
return|;
block|}
comment|/** called to get the default value source (normally, from the    *  Lucene FieldCache.)    */
DECL|method|getValueSource
specifier|public
name|ValueSource
name|getValueSource
parameter_list|(
name|SchemaField
name|field
parameter_list|)
block|{
return|return
operator|new
name|OrdFieldSource
argument_list|(
name|field
operator|.
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

