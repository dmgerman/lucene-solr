begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
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
name|search
operator|.
name|suggest
operator|.
name|InputIterator
import|;
end_import

begin_comment
comment|/**  * A simple interface representing a Dictionary. A Dictionary  * here is a list of entries, where every entry consists of  * term, weight and payload.  *   */
end_comment

begin_interface
DECL|interface|Dictionary
specifier|public
interface|interface
name|Dictionary
block|{
comment|/**    * Returns an iterator over all the entries    * @return Iterator    */
DECL|method|getEntryIterator
name|InputIterator
name|getEntryIterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

