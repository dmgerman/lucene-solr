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
name|document
operator|.
name|Fieldable
import|;
end_import

begin_class
DECL|class|InvertedDocConsumerPerField
specifier|abstract
class|class
name|InvertedDocConsumerPerField
block|{
comment|// Called once per field, and is given all Fieldable
comment|// occurrences for this field in the document.  Return
comment|// true if you wish to see inverted tokens for these
comment|// fields:
DECL|method|start
specifier|abstract
name|boolean
name|start
parameter_list|(
name|Fieldable
index|[]
name|fields
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|// Called before a field instance is being processed
DECL|method|start
specifier|abstract
name|void
name|start
parameter_list|(
name|Fieldable
name|field
parameter_list|)
function_decl|;
comment|// Called once per inverted token
DECL|method|add
specifier|abstract
name|void
name|add
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|// Called once per field per document, after all Fieldable
comment|// occurrences are inverted
DECL|method|finish
specifier|abstract
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|// Called on hitting an aborting exception
DECL|method|abort
specifier|abstract
name|void
name|abort
parameter_list|()
function_decl|;
block|}
end_class

end_unit

