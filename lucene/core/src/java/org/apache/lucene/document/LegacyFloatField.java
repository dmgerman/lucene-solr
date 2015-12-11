begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
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
name|index
operator|.
name|IndexOptions
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
name|LegacyNumericUtils
import|;
end_import

begin_comment
comment|/**  *<p>  * Field that indexes<code>float</code> values  * for efficient range filtering and sorting. Here's an example usage:  *   *<pre class="prettyprint">  * document.add(new LegacyFloatField(name, 6.0F, Field.Store.NO));  *</pre>  *   * For optimal performance, re-use the<code>LegacyFloatField</code> and  * {@link Document} instance for more than one document:  *   *<pre class="prettyprint">  *  LegacyFloatField field = new LegacyFloatField(name, 0.0F, Field.Store.NO);  *  Document document = new Document();  *  document.add(field);  *   *  for(all documents) {  *    ...  *    field.setFloatValue(value)  *    writer.addDocument(document);  *    ...  *  }  *</pre>  *  * See also {@link LegacyIntField}, {@link LegacyLongField}, {@link  * LegacyDoubleField}.  *  *<p>To perform range querying or filtering against a  *<code>LegacyFloatField</code>, use {@link org.apache.lucene.search.LegacyNumericRangeQuery}.  * To sort according to a  *<code>LegacyFloatField</code>, use the normal numeric sort types, eg  * {@link org.apache.lucene.search.SortField.Type#FLOAT}.<code>LegacyFloatField</code>  * values can also be loaded directly from {@link org.apache.lucene.index.LeafReader#getNumericDocValues}.</p>  *  *<p>You may add the same field name as an<code>LegacyFloatField</code> to  * the same document more than once.  Range querying and  * filtering will be the logical OR of all values; so a range query  * will hit all documents that have at least one value in  * the range. However sort behavior is not defined.  If you need to sort,  * you should separately index a single-valued<code>LegacyFloatField</code>.</p>  *  *<p>A<code>LegacyFloatField</code> will consume somewhat more disk space  * in the index than an ordinary single-valued field.  * However, for a typical index that includes substantial  * textual content per document, this increase will likely  * be in the noise.</p>  *  *<p>Within Lucene, each numeric value is indexed as a  *<em>trie</em> structure, where each term is logically  * assigned to larger and larger pre-defined brackets (which  * are simply lower-precision representations of the value).  * The step size between each successive bracket is called the  *<code>precisionStep</code>, measured in bits.  Smaller  *<code>precisionStep</code> values result in larger number  * of brackets, which consumes more disk space in the index  * but may result in faster range search performance.  The  * default value, 8, was selected for a reasonable tradeoff  * of disk space consumption versus performance.  You can  * create a custom {@link FieldType} and invoke the {@link  * FieldType#setNumericPrecisionStep} method if you'd  * like to change the value.  Note that you must also  * specify a congruent value when creating {@link  * org.apache.lucene.search.LegacyNumericRangeQuery}.  * For low cardinality fields larger precision steps are good.  * If the cardinality is&lt; 100, it is fair  * to use {@link Integer#MAX_VALUE}, which produces one  * term per value.  *  *<p>For more information on the internals of numeric trie  * indexing, including the<a  * href="../search/LegacyNumericRangeQuery.html#precisionStepDesc"><code>precisionStep</code></a>  * configuration, see {@link org.apache.lucene.search.LegacyNumericRangeQuery}. The format of  * indexed values is described in {@link org.apache.lucene.util.LegacyNumericUtils}.  *  *<p>If you only need to sort by numeric value, and never  * run range querying/filtering, you can index using a  *<code>precisionStep</code> of {@link Integer#MAX_VALUE}.  * This will minimize disk space consumed.</p>  *  *<p>More advanced users can instead use {@link  * org.apache.lucene.analysis.LegacyNumericTokenStream} directly, when indexing numbers. This  * class is a wrapper around this token stream type for  * easier, more intuitive usage.</p>  *  * @deprecated Please use {@link DimensionalFloatField} instead  *  * @since 2.9  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|LegacyFloatField
specifier|public
specifier|final
class|class
name|LegacyFloatField
extends|extends
name|Field
block|{
comment|/**     * Type for a LegacyFloatField that is not stored:    * normalization factors, frequencies, and positions are omitted.    */
DECL|field|TYPE_NOT_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_NOT_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_NOT_STORED
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setNumericType
argument_list|(
name|FieldType
operator|.
name|LegacyNumericType
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|setNumericPrecisionStep
argument_list|(
name|LegacyNumericUtils
operator|.
name|PRECISION_STEP_DEFAULT_32
argument_list|)
expr_stmt|;
name|TYPE_NOT_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/**     * Type for a stored LegacyFloatField:    * normalization factors, frequencies, and positions are omitted.    */
DECL|field|TYPE_STORED
specifier|public
specifier|static
specifier|final
name|FieldType
name|TYPE_STORED
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|TYPE_STORED
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setNumericType
argument_list|(
name|FieldType
operator|.
name|LegacyNumericType
operator|.
name|FLOAT
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setNumericPrecisionStep
argument_list|(
name|LegacyNumericUtils
operator|.
name|PRECISION_STEP_DEFAULT_32
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TYPE_STORED
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
comment|/** Creates a stored or un-stored LegacyFloatField with the provided value    *  and default<code>precisionStep</code> {@link    *  org.apache.lucene.util.LegacyNumericUtils#PRECISION_STEP_DEFAULT_32} (8).    *  @param name field name    *  @param value 32-bit double value    *  @param stored Store.YES if the content should also be stored    *  @throws IllegalArgumentException if the field name is null.    */
DECL|method|LegacyFloatField
specifier|public
name|LegacyFloatField
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|,
name|Store
name|stored
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|stored
operator|==
name|Store
operator|.
name|YES
condition|?
name|TYPE_STORED
else|:
name|TYPE_NOT_STORED
argument_list|)
expr_stmt|;
name|fieldsData
operator|=
name|Float
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Expert: allows you to customize the {@link    *  FieldType}.     *  @param name field name    *  @param value 32-bit float value    *  @param type customized field type: must have {@link FieldType#numericType()}    *         of {@link org.apache.lucene.document.FieldType.LegacyNumericType#FLOAT}.    *  @throws IllegalArgumentException if the field name or type is null, or    *          if the field type does not have a FLOAT numericType()    */
DECL|method|LegacyFloatField
specifier|public
name|LegacyFloatField
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|numericType
argument_list|()
operator|!=
name|FieldType
operator|.
name|LegacyNumericType
operator|.
name|FLOAT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type.numericType() must be FLOAT but got "
operator|+
name|type
operator|.
name|numericType
argument_list|()
argument_list|)
throw|;
block|}
name|fieldsData
operator|=
name|Float
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

