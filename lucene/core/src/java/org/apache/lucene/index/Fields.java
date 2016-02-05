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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/** Flex API for access to fields and terms  *  @lucene.experimental */
end_comment

begin_class
DECL|class|Fields
specifier|public
specifier|abstract
class|class
name|Fields
implements|implements
name|Iterable
argument_list|<
name|String
argument_list|>
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|Fields
specifier|protected
name|Fields
parameter_list|()
block|{   }
comment|/** Returns an iterator that will step through all fields    *  names.  This will not return null.  */
annotation|@
name|Override
DECL|method|iterator
specifier|public
specifier|abstract
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
function_decl|;
comment|/** Get the {@link Terms} for this field.  This will return    *  null if the field does not exist. */
DECL|method|terms
specifier|public
specifier|abstract
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Returns the number of fields or -1 if the number of    * distinct field names is unknown. If&gt;= 0,    * {@link #iterator} will return as many field names. */
DECL|method|size
specifier|public
specifier|abstract
name|int
name|size
parameter_list|()
function_decl|;
comment|/** Zero-length {@code Fields} array. */
DECL|field|EMPTY_ARRAY
specifier|public
specifier|final
specifier|static
name|Fields
index|[]
name|EMPTY_ARRAY
init|=
operator|new
name|Fields
index|[
literal|0
index|]
decl_stmt|;
block|}
end_class

end_unit

