begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Copyright 2002-2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
comment|/**  * A simple interface representing a Dictionary  * @author Nicolas Maisonneuve  * @version 1.0  */
end_comment

begin_interface
DECL|interface|Dictionary
specifier|public
interface|interface
name|Dictionary
block|{
comment|/**    * Return all words present in the dictionary    * @return Iterator    */
DECL|method|getWordsIterator
name|Iterator
name|getWordsIterator
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

