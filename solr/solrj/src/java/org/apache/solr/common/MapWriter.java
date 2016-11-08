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
name|Map
import|;
end_import

begin_comment
comment|/**  * Use this class if the Map size is not known  */
end_comment

begin_interface
DECL|interface|MapWriter
specifier|public
interface|interface
name|MapWriter
extends|extends
name|MapSerializable
block|{
annotation|@
name|Override
DECL|method|toMap
specifier|default
name|Map
name|toMap
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
try|try
block|{
name|writeMap
argument_list|(
operator|new
name|EntryWriter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|EntryWriter
name|put
parameter_list|(
name|String
name|k
parameter_list|,
name|Object
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|map
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
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
name|map
return|;
block|}
DECL|method|writeMap
name|void
name|writeMap
parameter_list|(
name|EntryWriter
name|ew
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|interface|EntryWriter
interface|interface
name|EntryWriter
block|{
comment|/**Writes a key value into the map      * @param k The key      * @param v The value can be any supported object      */
DECL|method|put
name|EntryWriter
name|put
parameter_list|(
name|String
name|k
parameter_list|,
name|Object
name|v
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|put
specifier|default
name|EntryWriter
name|put
parameter_list|(
name|String
name|k
parameter_list|,
name|int
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|put
argument_list|(
name|k
argument_list|,
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
DECL|method|put
specifier|default
name|EntryWriter
name|put
parameter_list|(
name|String
name|k
parameter_list|,
name|long
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|put
argument_list|(
name|k
argument_list|,
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
DECL|method|put
specifier|default
name|EntryWriter
name|put
parameter_list|(
name|String
name|k
parameter_list|,
name|float
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|put
argument_list|(
name|k
argument_list|,
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
DECL|method|put
specifier|default
name|EntryWriter
name|put
parameter_list|(
name|String
name|k
parameter_list|,
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|put
argument_list|(
name|k
argument_list|,
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
DECL|method|put
specifier|default
name|EntryWriter
name|put
parameter_list|(
name|String
name|k
parameter_list|,
name|boolean
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|put
argument_list|(
name|k
argument_list|,
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
block|}
end_interface

end_unit

