begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
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
name|LinkedHashMap
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
comment|/**  * Interface to help do push writing to an array  */
end_comment

begin_interface
DECL|interface|IteratorWriter
specifier|public
interface|interface
name|IteratorWriter
block|{
comment|/**    * @param iw after this method returns , the ItemWriter Object is invalid    *          Do not hold a reference to this object    */
DECL|method|writeIter
name|void
name|writeIter
parameter_list|(
name|ItemWriter
name|iw
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|interface|ItemWriter
interface|interface
name|ItemWriter
block|{
comment|/**The item could be any supported type      */
DECL|method|add
name|ItemWriter
name|add
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|add
specifier|default
name|ItemWriter
name|add
parameter_list|(
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
operator|(
name|Integer
operator|)
name|v
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|default
name|ItemWriter
name|add
parameter_list|(
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
operator|(
name|Long
operator|)
name|v
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|default
name|ItemWriter
name|add
parameter_list|(
name|float
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
operator|(
name|Float
operator|)
name|v
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|default
name|ItemWriter
name|add
parameter_list|(
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
operator|(
name|Double
operator|)
name|v
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|default
name|ItemWriter
name|add
parameter_list|(
name|boolean
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|add
argument_list|(
operator|(
name|Boolean
operator|)
name|v
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|method|toList
specifier|default
name|List
name|toList
parameter_list|(
name|List
name|l
parameter_list|)
block|{
try|try
block|{
name|writeIter
argument_list|(
operator|new
name|ItemWriter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ItemWriter
name|add
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|o
operator|instanceof
name|MapWriter
condition|)
name|o
operator|=
operator|(
operator|(
name|MapWriter
operator|)
name|o
operator|)
operator|.
name|toMap
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|IteratorWriter
condition|)
name|o
operator|=
operator|(
operator|(
name|IteratorWriter
operator|)
name|o
operator|)
operator|.
name|toList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|l
return|;
block|}
block|}
end_interface

end_unit

