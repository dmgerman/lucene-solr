begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/*  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  *  */
end_comment

begin_comment
comment|/**  * Transparent access to the vector space model,  * either via TermFreqVector or by resolving it from the inverted index.  *<p/>  * Resolving a term vector from a large index can be a time consuming process.  *<p/>  * Warning! This class is not thread safe!  */
end_comment

begin_class
DECL|class|TermVectorAccessor
specifier|public
class|class
name|TermVectorAccessor
block|{
DECL|method|TermVectorAccessor
specifier|public
name|TermVectorAccessor
parameter_list|()
block|{   }
comment|/**    * Instance reused to save garbage collector some time    */
DECL|field|decoratedMapper
specifier|private
name|TermVectorMapperDecorator
name|decoratedMapper
init|=
operator|new
name|TermVectorMapperDecorator
argument_list|()
decl_stmt|;
comment|/**    * Visits the TermVectorMapper and populates it with terms available for a given document,    * either via a vector created at index time or by resolving them from the inverted index.    *    * @param indexReader    Index source    * @param documentNumber Source document to access    * @param fieldName      Field to resolve    * @param mapper         Mapper to be mapped with data    * @throws IOException    */
DECL|method|accept
specifier|public
name|void
name|accept
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|int
name|documentNumber
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|)
throws|throws
name|IOException
block|{
name|fieldName
operator|=
name|fieldName
operator|.
name|intern
argument_list|()
expr_stmt|;
name|decoratedMapper
operator|.
name|decorated
operator|=
name|mapper
expr_stmt|;
name|decoratedMapper
operator|.
name|termVectorStored
operator|=
literal|false
expr_stmt|;
name|indexReader
operator|.
name|getTermFreqVector
argument_list|(
name|documentNumber
argument_list|,
name|fieldName
argument_list|,
name|decoratedMapper
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|decoratedMapper
operator|.
name|termVectorStored
condition|)
block|{
name|mapper
operator|.
name|setDocumentNumber
argument_list|(
name|documentNumber
argument_list|)
expr_stmt|;
name|build
argument_list|(
name|indexReader
argument_list|,
name|fieldName
argument_list|,
name|mapper
argument_list|,
name|documentNumber
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Instance reused to save garbage collector some time */
DECL|field|tokens
specifier|private
name|List
comment|/*<String>*/
name|tokens
decl_stmt|;
comment|/** Instance reused to save garbage collector some time */
DECL|field|positions
specifier|private
name|List
comment|/*<int[]>*/
name|positions
decl_stmt|;
comment|/** Instance reused to save garbage collector some time */
DECL|field|frequencies
specifier|private
name|List
comment|/*<Integer>*/
name|frequencies
decl_stmt|;
comment|/**    * Populates the mapper with terms available for the given field in a document    * by resolving the inverted index.    *    * @param indexReader    * @param field interned field name    * @param mapper    * @param documentNumber    * @throws IOException    */
DECL|method|build
specifier|private
name|void
name|build
parameter_list|(
name|IndexReader
name|indexReader
parameter_list|,
name|String
name|field
parameter_list|,
name|TermVectorMapper
name|mapper
parameter_list|,
name|int
name|documentNumber
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tokens
operator|==
literal|null
condition|)
block|{
name|tokens
operator|=
operator|new
name|ArrayList
comment|/*<String>*/
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|positions
operator|=
operator|new
name|ArrayList
comment|/*<int[]>*/
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|frequencies
operator|=
operator|new
name|ArrayList
comment|/*<Integer>*/
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tokens
operator|.
name|clear
argument_list|()
expr_stmt|;
name|frequencies
operator|.
name|clear
argument_list|()
expr_stmt|;
name|positions
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|TermEnum
name|termEnum
init|=
name|indexReader
operator|.
name|terms
argument_list|()
decl_stmt|;
if|if
condition|(
name|termEnum
operator|.
name|skipTo
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|""
argument_list|)
argument_list|)
condition|)
block|{
while|while
condition|(
name|termEnum
operator|.
name|term
argument_list|()
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
name|TermPositions
name|termPositions
init|=
name|indexReader
operator|.
name|termPositions
argument_list|(
name|termEnum
operator|.
name|term
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|termPositions
operator|.
name|skipTo
argument_list|(
name|documentNumber
argument_list|)
condition|)
block|{
name|frequencies
operator|.
name|add
argument_list|(
operator|new
name|Integer
argument_list|(
name|termPositions
operator|.
name|freq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|termEnum
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|mapper
operator|.
name|isIgnoringPositions
argument_list|()
condition|)
block|{
name|int
index|[]
name|positions
init|=
operator|new
name|int
index|[
name|termPositions
operator|.
name|freq
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|positions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|positions
index|[
name|i
index|]
operator|=
name|termPositions
operator|.
name|nextPosition
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|positions
operator|.
name|add
argument_list|(
name|positions
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|positions
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|termPositions
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|termEnum
operator|.
name|next
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
name|mapper
operator|.
name|setDocumentNumber
argument_list|(
name|documentNumber
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|setExpectations
argument_list|(
name|field
argument_list|,
name|tokens
operator|.
name|size
argument_list|()
argument_list|,
literal|false
argument_list|,
operator|!
name|mapper
operator|.
name|isIgnoringPositions
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tokens
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|mapper
operator|.
name|map
argument_list|(
operator|(
name|String
operator|)
name|tokens
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|frequencies
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
argument_list|,
operator|(
name|TermVectorOffsetInfo
index|[]
operator|)
literal|null
argument_list|,
operator|(
name|int
index|[]
operator|)
name|positions
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|termEnum
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|class|TermVectorMapperDecorator
specifier|private
specifier|static
class|class
name|TermVectorMapperDecorator
extends|extends
name|TermVectorMapper
block|{
DECL|field|decorated
specifier|private
name|TermVectorMapper
name|decorated
decl_stmt|;
DECL|method|isIgnoringPositions
specifier|public
name|boolean
name|isIgnoringPositions
parameter_list|()
block|{
return|return
name|decorated
operator|.
name|isIgnoringPositions
argument_list|()
return|;
block|}
DECL|method|isIgnoringOffsets
specifier|public
name|boolean
name|isIgnoringOffsets
parameter_list|()
block|{
return|return
name|decorated
operator|.
name|isIgnoringOffsets
argument_list|()
return|;
block|}
DECL|field|termVectorStored
specifier|private
name|boolean
name|termVectorStored
init|=
literal|false
decl_stmt|;
DECL|method|setExpectations
specifier|public
name|void
name|setExpectations
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|numTerms
parameter_list|,
name|boolean
name|storeOffsets
parameter_list|,
name|boolean
name|storePositions
parameter_list|)
block|{
name|decorated
operator|.
name|setExpectations
argument_list|(
name|field
argument_list|,
name|numTerms
argument_list|,
name|storeOffsets
argument_list|,
name|storePositions
argument_list|)
expr_stmt|;
name|termVectorStored
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|map
specifier|public
name|void
name|map
parameter_list|(
name|String
name|term
parameter_list|,
name|int
name|frequency
parameter_list|,
name|TermVectorOffsetInfo
index|[]
name|offsets
parameter_list|,
name|int
index|[]
name|positions
parameter_list|)
block|{
name|decorated
operator|.
name|map
argument_list|(
name|term
argument_list|,
name|frequency
argument_list|,
name|offsets
argument_list|,
name|positions
argument_list|)
expr_stmt|;
block|}
DECL|method|setDocumentNumber
specifier|public
name|void
name|setDocumentNumber
parameter_list|(
name|int
name|documentNumber
parameter_list|)
block|{
name|decorated
operator|.
name|setDocumentNumber
argument_list|(
name|documentNumber
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

