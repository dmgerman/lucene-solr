begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|bkd
operator|.
name|BKDWriter
import|;
end_import

begin_comment
comment|/** Allows recursively visiting point values indexed with {@link org.apache.lucene.document.IntPoint},  *  {@link org.apache.lucene.document.FloatPoint}, {@link org.apache.lucene.document.LongPoint}, {@link org.apache.lucene.document.DoublePoint}  *  or {@link org.apache.lucene.document.BinaryPoint}.  *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|PointValues
specifier|public
specifier|abstract
class|class
name|PointValues
block|{
comment|/** Maximum number of bytes for each dimension */
DECL|field|MAX_NUM_BYTES
specifier|public
specifier|static
specifier|final
name|int
name|MAX_NUM_BYTES
init|=
literal|16
decl_stmt|;
comment|/** Maximum number of dimensions */
DECL|field|MAX_DIMENSIONS
specifier|public
specifier|static
specifier|final
name|int
name|MAX_DIMENSIONS
init|=
name|BKDWriter
operator|.
name|MAX_DIMS
decl_stmt|;
comment|/** Default constructor */
DECL|method|PointValues
specifier|protected
name|PointValues
parameter_list|()
block|{   }
comment|/** Used by {@link #intersect} to check how each recursive cell corresponds to the query. */
DECL|enum|Relation
specifier|public
enum|enum
name|Relation
block|{
comment|/** Return this if the cell is fully contained by the query */
DECL|enum constant|CELL_INSIDE_QUERY
name|CELL_INSIDE_QUERY
block|,
comment|/** Return this if the cell and query do not overlap */
DECL|enum constant|CELL_OUTSIDE_QUERY
name|CELL_OUTSIDE_QUERY
block|,
comment|/** Return this if the cell partially overlapps the query */
DECL|enum constant|CELL_CROSSES_QUERY
name|CELL_CROSSES_QUERY
block|}
empty_stmt|;
comment|/** We recurse the BKD tree, using a provided instance of this to guide the recursion.    *    * @lucene.experimental */
DECL|interface|IntersectVisitor
specifier|public
interface|interface
name|IntersectVisitor
block|{
comment|/** Called for all docs in a leaf cell that's fully contained by the query.  The      *  consumer should blindly accept the docID. */
DECL|method|visit
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called for all docs in a leaf cell that crosses the query.  The consumer      *  should scrutinize the packedValue to decide whether to accept it. */
DECL|method|visit
name|void
name|visit
parameter_list|(
name|int
name|docID
parameter_list|,
name|byte
index|[]
name|packedValue
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Called for non-leaf cells to test how the cell relates to the query, to      *  determine how to further recurse down the treer. */
DECL|method|compare
name|Relation
name|compare
parameter_list|(
name|byte
index|[]
name|minPackedValue
parameter_list|,
name|byte
index|[]
name|maxPackedValue
parameter_list|)
function_decl|;
comment|/** Notifies the caller that this many documents (from one block) are about      *  to be visited */
DECL|method|grow
specifier|default
name|void
name|grow
parameter_list|(
name|int
name|count
parameter_list|)
block|{}
empty_stmt|;
block|}
comment|/** Finds all documents and points matching the provided visitor.    *  This method does not enforce live docs, so it's up to the caller    *  to test whether each document is deleted, if necessary. */
DECL|method|intersect
specifier|public
specifier|abstract
name|void
name|intersect
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|IntersectVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns minimum value for each dimension, packed, or null if no points were indexed */
DECL|method|getMinPackedValue
specifier|public
specifier|abstract
name|byte
index|[]
name|getMinPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns maximum value for each dimension, packed, or null if no points were indexed */
DECL|method|getMaxPackedValue
specifier|public
specifier|abstract
name|byte
index|[]
name|getMaxPackedValue
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns how many dimensions were indexed */
DECL|method|getNumDimensions
specifier|public
specifier|abstract
name|int
name|getNumDimensions
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of bytes per dimension */
DECL|method|getBytesPerDimension
specifier|public
specifier|abstract
name|int
name|getBytesPerDimension
parameter_list|(
name|String
name|fieldName
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

