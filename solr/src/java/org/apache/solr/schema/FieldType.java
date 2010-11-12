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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|tokenattributes
operator|.
name|OffsetAttribute
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
name|lucene
operator|.
name|search
operator|.
name|Query
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
name|TermRangeQuery
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
name|TermQuery
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
name|index
operator|.
name|Term
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|UnicodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|CharArr
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
name|search
operator|.
name|QParser
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
name|response
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
name|response
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
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
name|params
operator|.
name|MapSolrParams
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
name|util
operator|.
name|ByteUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * Base class for all field types used by an index schema.  *  * @version $Id$  */
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
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FieldType
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * The default poly field separator.    *    * @see #createFields(SchemaField, String, float)    * @see #isPolyField()    */
DECL|field|POLY_FIELD_SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|POLY_FIELD_SEPARATOR
init|=
literal|"___"
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
comment|/** Returns true if fields can have multiple values */
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|MULTIVALUED
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**    * A "polyField" is a FieldType that can produce more than one Fieldable instance for a single value, via the {@link #createFields(org.apache.solr.schema.SchemaField, String, float)} method.  This is useful    * when hiding the implementation details of a field from the Solr end user.  For instance, a spatial point may be represented by multiple different fields.    * @return true if the {@link #createFields(org.apache.solr.schema.SchemaField, String, float)} method may return more than one field    */
DECL|method|isPolyField
specifier|public
name|boolean
name|isPolyField
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/** Returns true if a single field value of this type has multiple logical values    *  for the purposes of faceting, sorting, etc.  Text fields normally return    *  true since each token/word is a logical value.    */
DECL|method|multiValuedFieldCache
specifier|public
name|boolean
name|multiValuedFieldCache
parameter_list|()
block|{
return|return
name|isTokenized
argument_list|()
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
block|{    }
DECL|method|getArg
specifier|protected
name|String
name|getArg
parameter_list|(
name|String
name|n
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
name|String
name|s
init|=
name|args
operator|.
name|remove
argument_list|(
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
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
literal|"Missing parameter '"
operator|+
name|n
operator|+
literal|"' for FieldType="
operator|+
name|typeName
operator|+
name|args
argument_list|)
throw|;
block|}
return|return
name|s
return|;
block|}
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
comment|// default to STORED, INDEXED, OMIT_TF_POSITIONS and MULTIVALUED depending on schema version
name|properties
operator|=
operator|(
name|STORED
operator||
name|INDEXED
operator|)
expr_stmt|;
name|float
name|schemaVersion
init|=
name|schema
operator|.
name|getVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|schemaVersion
operator|<
literal|1.1f
condition|)
name|properties
operator||=
name|MULTIVALUED
expr_stmt|;
if|if
condition|(
name|schemaVersion
operator|>
literal|1.1f
condition|)
name|properties
operator||=
name|OMIT_TF_POSITIONS
expr_stmt|;
if|if
condition|(
name|schemaVersion
operator|<
literal|1.3
condition|)
block|{
name|args
operator|.
name|remove
argument_list|(
literal|"compressThreshold"
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * Used for adding a document when a field needs to be created from a    * type and a string.    *    *<p>    * By default, the indexed value is the same as the stored value    * (taken from toInternal()).   Having a different representation for    * external, internal, and indexed would present quite a few problems    * given the current Lucene architecture.  An analyzer for adding docs    * would need to translate internal->indexed while an analyzer for    * querying would need to translate external-&gt;indexed.    *</p>    *<p>    * The only other alternative to having internal==indexed would be to have    * internal==external.   In this case, toInternal should convert to    * the indexed representation, toExternal() should do nothing, and    * createField() should *not* call toInternal, but use the external    * value and set tokenized=true to get Lucene to convert to the    * internal(indexed) form.    *</p>    *    * :TODO: clean up and clarify this explanation.    *    * @see #toInternal    *    *    */
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
if|if
condition|(
name|log
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|log
operator|.
name|trace
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
name|RuntimeException
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
return|return
name|createField
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
argument_list|,
name|field
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|field
operator|.
name|omitTf
argument_list|()
argument_list|,
name|boost
argument_list|)
return|;
block|}
comment|/**    * Create the field from native Lucene parts.  Mostly intended for use by FieldTypes outputing multiple    * Fields per SchemaField    * @param name The name of the field    * @param val The _internal_ value to index    * @param storage {@link org.apache.lucene.document.Field.Store}    * @param index {@link org.apache.lucene.document.Field.Index}    * @param vec {@link org.apache.lucene.document.Field.TermVector}    * @param omitNorms true if norms should be omitted    * @param omitTFPos true if term freq and position should be omitted.    * @param boost The boost value    * @return the {@link org.apache.lucene.document.Field}.    */
DECL|method|createField
specifier|protected
name|Field
name|createField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|val
parameter_list|,
name|Field
operator|.
name|Store
name|storage
parameter_list|,
name|Field
operator|.
name|Index
name|index
parameter_list|,
name|Field
operator|.
name|TermVector
name|vec
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|omitTFPos
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
name|name
argument_list|,
name|val
argument_list|,
name|storage
argument_list|,
name|index
argument_list|,
name|vec
argument_list|)
decl_stmt|;
name|f
operator|.
name|setOmitNorms
argument_list|(
name|omitNorms
argument_list|)
expr_stmt|;
name|f
operator|.
name|setOmitTermFreqAndPositions
argument_list|(
name|omitTFPos
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
comment|/**    * Given a {@link org.apache.solr.schema.SchemaField}, create one or more {@link org.apache.lucene.document.Fieldable} instances    * @param field the {@link org.apache.solr.schema.SchemaField}    * @param externalVal The value to add to the field    * @param boost The boost to apply    * @return An array of {@link org.apache.lucene.document.Fieldable}    *    * @see #createField(SchemaField, String, float)    * @see #isPolyField()    */
DECL|method|createFields
specifier|public
name|Fieldable
index|[]
name|createFields
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
name|Field
name|f
init|=
name|createField
argument_list|(
name|field
argument_list|,
name|externalVal
argument_list|,
name|boost
argument_list|)
decl_stmt|;
return|return
name|f
operator|==
literal|null
condition|?
operator|new
name|Fieldable
index|[]
block|{}
else|:
operator|new
name|Fieldable
index|[]
block|{
name|f
block|}
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
name|ANALYZED
else|:
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
operator|)
else|:
name|Field
operator|.
name|Index
operator|.
name|NO
return|;
block|}
comment|/**    * Convert an external value (from XML update command or from query string)    * into the internal format for both storing and indexing (which can be modified by any analyzers).    * @see #toExternal    */
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
comment|/**    * Convert the stored-field format to an external object.    * @see #toInternal    * @since solr 1.3    */
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
DECL|method|toObject
specifier|public
name|Object
name|toObject
parameter_list|(
name|SchemaField
name|sf
parameter_list|,
name|BytesRef
name|term
parameter_list|)
block|{
name|CharArr
name|ext
init|=
operator|new
name|CharArr
argument_list|(
name|term
operator|.
name|length
argument_list|)
decl_stmt|;
name|indexedToReadable
argument_list|(
name|term
argument_list|,
name|ext
argument_list|)
expr_stmt|;
name|Field
name|f
init|=
name|createField
argument_list|(
name|sf
argument_list|,
name|ext
operator|.
name|toString
argument_list|()
argument_list|,
literal|1.0f
argument_list|)
decl_stmt|;
return|return
name|toObject
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/** Given an indexed term, return the human readable representation */
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
comment|/** Given an indexed term, append the human readable representation to out */
DECL|method|indexedToReadable
specifier|public
name|void
name|indexedToReadable
parameter_list|(
name|BytesRef
name|input
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
name|ByteUtils
operator|.
name|UTF8toUTF16
argument_list|(
name|input
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
comment|/** Given the stored field, return the human readable representation */
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
comment|/** Given the stored field, return the indexed form */
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
comment|/** Given the readable value, return the term value that will match it. */
DECL|method|readableToIndexed
specifier|public
name|String
name|readableToIndexed
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
name|toInternal
argument_list|(
name|val
argument_list|)
return|;
block|}
comment|/** Given the readable value, return the term value that will match it. */
DECL|method|readableToIndexed
specifier|public
name|void
name|readableToIndexed
parameter_list|(
name|CharSequence
name|val
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|String
name|internal
init|=
name|readableToIndexed
argument_list|(
name|val
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|internal
argument_list|,
literal|0
argument_list|,
name|internal
operator|.
name|length
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
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
DECL|method|getStream
specifier|public
name|TokenStreamInfo
name|getStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|Tokenizer
name|ts
init|=
operator|new
name|Tokenizer
argument_list|(
name|reader
argument_list|)
block|{
specifier|final
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
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|OffsetAttribute
name|offsetAtt
init|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
name|clearAttributes
argument_list|()
expr_stmt|;
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
literal|false
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
name|termAtt
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
literal|0
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|TokenStreamInfo
argument_list|(
name|ts
argument_list|,
name|ts
argument_list|)
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
name|trace
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
name|trace
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
parameter_list|,
name|QParser
name|parser
parameter_list|)
block|{
return|return
name|getValueSource
argument_list|(
name|field
argument_list|)
return|;
block|}
comment|/**    * @deprecated use {@link #getValueSource(SchemaField, QParser)}    */
annotation|@
name|Deprecated
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
comment|/**    * Returns a Query instance for doing range searches on this field type. {@link org.apache.solr.search.SolrQueryParser}    * currently passes part1 and part2 as null if they are '*' respectively. minInclusive and maxInclusive are both true    * currently by SolrQueryParser but that may change in the future. Also, other QueryParser implementations may have    * different semantics.    *<p/>    * Sub-classes should override this method to provide their own range query implementation. They should strive to    * handle nulls in part1 and/or part2 as well as unequal minInclusive and maxInclusive parameters gracefully.    *    * @param parser    * @param field        the schema field    * @param part1        the lower boundary of the range, nulls are allowed.    * @param part2        the upper boundary of the range, nulls are allowed    * @param minInclusive whether the minimum of the range is inclusive or not    * @param maxInclusive whether the maximum of the range is inclusive or not    *  @return a Query instance to perform range search according to given parameters    *    * @see org.apache.solr.search.SolrQueryParser#getRangeQuery(String, String, String, boolean)    */
DECL|method|getRangeQuery
specifier|public
name|Query
name|getRangeQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|part1
parameter_list|,
name|String
name|part2
parameter_list|,
name|boolean
name|minInclusive
parameter_list|,
name|boolean
name|maxInclusive
parameter_list|)
block|{
comment|// constant score mode is now enabled per default
return|return
operator|new
name|TermRangeQuery
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|part1
operator|==
literal|null
condition|?
literal|null
else|:
name|toInternal
argument_list|(
name|part1
argument_list|)
argument_list|,
name|part2
operator|==
literal|null
condition|?
literal|null
else|:
name|toInternal
argument_list|(
name|part2
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
block|}
comment|/**    * Returns a Query instance for doing searches against a field.    * @param parser The {@link org.apache.solr.search.QParser} calling the method    * @param field The {@link org.apache.solr.schema.SchemaField} of the field to search    * @param externalVal The String representation of the value to search    * @return The {@link org.apache.lucene.search.Query} instance.  This implementation returns a {@link org.apache.lucene.search.TermQuery} but overriding queries may not    *     */
DECL|method|getFieldQuery
specifier|public
name|Query
name|getFieldQuery
parameter_list|(
name|QParser
name|parser
parameter_list|,
name|SchemaField
name|field
parameter_list|,
name|String
name|externalVal
parameter_list|)
block|{
return|return
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|toInternal
argument_list|(
name|externalVal
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

