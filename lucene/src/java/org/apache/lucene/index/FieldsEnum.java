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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|AttributeSource
import|;
end_import

begin_comment
comment|/** Enumerates indexed fields.  You must first call {@link  *  #next} before calling {@link #terms}.  *  * @lucene.experimental */
end_comment

begin_class
DECL|class|FieldsEnum
specifier|public
specifier|abstract
class|class
name|FieldsEnum
block|{
comment|// TODO: maybe allow retrieving FieldInfo for current
comment|// field, as optional method?
DECL|field|atts
specifier|private
name|AttributeSource
name|atts
init|=
literal|null
decl_stmt|;
comment|/**    * Returns the related attributes.    */
DECL|method|attributes
specifier|public
name|AttributeSource
name|attributes
parameter_list|()
block|{
if|if
condition|(
name|atts
operator|==
literal|null
condition|)
block|{
name|atts
operator|=
operator|new
name|AttributeSource
argument_list|()
expr_stmt|;
block|}
return|return
name|atts
return|;
block|}
comment|/** Increments the enumeration to the next field.  The    *  returned field is always interned, so simple ==    *  comparison is allowed.  Returns null when there are no    *  more fields.*/
DECL|method|next
specifier|public
specifier|abstract
name|String
name|next
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Get {@link TermsEnum} for the current field.  You    *  should not call {@link #next} until you're done using    *  this {@link TermsEnum}.  After {@link #next} returns    *  null this method should not be called. This method    *  will not return null. */
DECL|method|terms
specifier|public
specifier|abstract
name|TermsEnum
name|terms
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|field|EMPTY_ARRAY
specifier|public
specifier|final
specifier|static
name|FieldsEnum
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|FieldsEnum
index|[
literal|0
index|]
decl_stmt|;
comment|/** Provides zero fields */
DECL|field|EMPTY
specifier|public
specifier|final
specifier|static
name|FieldsEnum
name|EMPTY
init|=
operator|new
name|FieldsEnum
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|TermsEnum
name|terms
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"this method should never be called"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

