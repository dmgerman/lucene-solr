begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|FieldInfo
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
name|Fields
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
name|FieldsEnum
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
name|TermsEnum
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
name|codecs
operator|.
name|docvalues
operator|.
name|DocValuesConsumer
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
name|values
operator|.
name|DocValues
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
name|values
operator|.
name|Writer
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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

begin_comment
comment|/** Abstract API that consumes terms, doc, freq, prox and  *  payloads postings.  Concrete implementations of this  *  actually do "something" with the postings (write it into  *  the index in a specific format).  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|FieldsConsumer
specifier|public
specifier|abstract
class|class
name|FieldsConsumer
implements|implements
name|Closeable
block|{
comment|/** Add a new field */
DECL|method|addField
specifier|public
specifier|abstract
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Adds a new DocValuesField */
DECL|method|addValuesField
specifier|public
name|DocValuesConsumer
name|addValuesField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"docvalues are not supported"
argument_list|)
throw|;
block|}
comment|/** Called when we are done adding everything. */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|MergeState
name|mergeState
parameter_list|,
name|Fields
name|fields
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldsEnum
name|fieldsEnum
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
assert|assert
name|fieldsEnum
operator|!=
literal|null
assert|;
name|String
name|field
decl_stmt|;
while|while
condition|(
operator|(
name|field
operator|=
name|fieldsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|mergeState
operator|.
name|fieldInfo
operator|=
name|mergeState
operator|.
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
expr_stmt|;
assert|assert
name|mergeState
operator|.
name|fieldInfo
operator|!=
literal|null
operator|:
literal|"FieldInfo for field is null: "
operator|+
name|field
assert|;
name|TermsEnum
name|terms
init|=
name|fieldsEnum
operator|.
name|terms
argument_list|()
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
specifier|final
name|TermsConsumer
name|termsConsumer
init|=
name|addField
argument_list|(
name|mergeState
operator|.
name|fieldInfo
argument_list|)
decl_stmt|;
name|termsConsumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
name|terms
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeState
operator|.
name|fieldInfo
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
specifier|final
name|DocValues
name|docValues
init|=
name|fieldsEnum
operator|.
name|docValues
argument_list|()
decl_stmt|;
comment|// TODO: is this assert values and if so when?
comment|//        assert docValues != null : "DocValues are null for " + mergeState.fieldInfo.getDocValues();
if|if
condition|(
name|docValues
operator|==
literal|null
condition|)
block|{
comment|// for now just continue
continue|continue;
block|}
specifier|final
name|DocValuesConsumer
name|docValuesConsumer
init|=
name|addValuesField
argument_list|(
name|mergeState
operator|.
name|fieldInfo
argument_list|)
decl_stmt|;
assert|assert
name|docValuesConsumer
operator|!=
literal|null
assert|;
name|docValuesConsumer
operator|.
name|merge
argument_list|(
name|mergeState
argument_list|,
name|docValues
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

