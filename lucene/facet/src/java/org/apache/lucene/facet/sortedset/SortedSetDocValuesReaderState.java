begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.facet.sortedset
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|SortedSetDocValues
import|;
end_import

begin_comment
comment|/** Wraps a {@link IndexReader} and resolves ords  *  using existing {@link SortedSetDocValues} APIs without a  *  separate taxonomy index.  This only supports flat facets  *  (dimension + label), and it makes faceting a bit  *  slower, adds some cost at reopen time, but avoids  *  managing the separate taxonomy index.  It also requires  *  less RAM than the taxonomy index, as it manages the flat  *  (2-level) hierarchy more efficiently.  In addition, the  *  tie-break during faceting is now meaningful (in label  *  sorted order).  *  *<p><b>NOTE</b>: creating an instance of this class is  *  somewhat costly, as it computes per-segment ordinal maps,  *  so you should create it once and re-use that one instance  *  for a given {@link IndexReader}. */
end_comment

begin_class
DECL|class|SortedSetDocValuesReaderState
specifier|public
specifier|abstract
class|class
name|SortedSetDocValuesReaderState
block|{
comment|/** Holds start/end range of ords, which maps to one    *  dimension (someday we may generalize it to map to    *  hierarchies within one dimension). */
DECL|class|OrdRange
specifier|public
specifier|static
specifier|final
class|class
name|OrdRange
block|{
comment|/** Start of range, inclusive: */
DECL|field|start
specifier|public
specifier|final
name|int
name|start
decl_stmt|;
comment|/** End of range, inclusive: */
DECL|field|end
specifier|public
specifier|final
name|int
name|end
decl_stmt|;
comment|/** Start and end are inclusive. */
DECL|method|OrdRange
specifier|public
name|OrdRange
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
block|}
comment|/** Sole constructor. */
DECL|method|SortedSetDocValuesReaderState
specifier|protected
name|SortedSetDocValuesReaderState
parameter_list|()
block|{   }
comment|/** Return top-level doc values. */
DECL|method|getDocValues
specifier|public
specifier|abstract
name|SortedSetDocValues
name|getDocValues
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Indexed field we are reading. */
DECL|method|getField
specifier|public
specifier|abstract
name|String
name|getField
parameter_list|()
function_decl|;
comment|/** Returns the {@link OrdRange} for this dimension. */
DECL|method|getOrdRange
specifier|public
specifier|abstract
name|OrdRange
name|getOrdRange
parameter_list|(
name|String
name|dim
parameter_list|)
function_decl|;
comment|/** Returns mapping from prefix to {@link OrdRange}. */
DECL|method|getPrefixToOrdRange
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|OrdRange
argument_list|>
name|getPrefixToOrdRange
parameter_list|()
function_decl|;
comment|/** Returns top-level index reader. */
DECL|method|getOrigReader
specifier|public
specifier|abstract
name|IndexReader
name|getOrigReader
parameter_list|()
function_decl|;
comment|/** Number of unique labels. */
DECL|method|getSize
specifier|public
specifier|abstract
name|int
name|getSize
parameter_list|()
function_decl|;
block|}
end_class

end_unit

